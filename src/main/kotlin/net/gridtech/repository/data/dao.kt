package net.gridtech.repository.data

import net.gridtech.core.data.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.find
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Component

const val FIELD_VALUE_RECORD_HISTORY_CAPACITY = 500

@Component
class NodeClassDao : INodeClassDao {
    @Autowired
    lateinit var repository: NodeClassRepository

    override fun getAll(): List<INodeClass> = repository.findAll()
    override fun getById(id: String): INodeClass? =
            repository.findById(id).let { if (it.isPresent) it.get() else null }

    override fun save(d: INodeClass) {
        repository.save(NodeClass.create(d))
    }

    override fun delete(id: String) {
        repository.deleteById(id)
    }

    override fun getByTags(tags: List<String>): List<INodeClass> =repository.findNodeClassesByTagsContains(tags)
}


@Component
class FieldDao : IFieldDao {
    @Autowired
    lateinit var repository: FieldRepository

    override fun getAll(): List<IField> = repository.findAll()
    override fun getById(id: String): IField? =
            repository.findById(id).let { if (it.isPresent) it.get() else null }

    override fun save(d: IField) {
        repository.save(Field.create(d))
    }

    override fun delete(id: String) {
        repository.deleteById(id)
    }

    override fun getByNodeClassId(nodeClassId: String): List<IField> = repository.findFieldsByNodeClassId(nodeClassId)
}


@Component
class NodeDao : INodeDao {
    @Autowired
    lateinit var repository: NodeRepository
    @Autowired
    lateinit var template: MongoTemplate

    override fun getAll(): List<INode> = repository.findAll()
    override fun getById(id: String): INode? =
            repository.findById(id).let { if (it.isPresent) it.get() else null }

    override fun save(d: INode) {
        repository.save(Node.create(d))
    }

    override fun delete(id: String) {
        repository.deleteById(id)
    }

    override fun getByNodeClassId(nodeClassId: String): List<INode> = repository.findNodesByNodeClassId(nodeClassId)
    override fun getByBranchNodeId(branchNodeId: String): List<INode> {
        return template.find(Query.query(Criteria.where("path").all(branchNodeId)), Node::class.java)
    }
}


@Component
class FieldValueDao : IFieldValueDao {
    @Autowired
    lateinit var template: MongoTemplate
    @Autowired
    lateinit var repository: FieldValueRepository
    @Autowired
    lateinit var fieldValueHistoryRepository: FieldValueHistoryRepository

    override fun getAll(): List<IFieldValue> = repository.findAll()
    override fun getById(id: String): IFieldValue? =
            repository.findById(id).let { if (it.isPresent) it.get() else null }

    override fun save(d: IFieldValue) {
        repository.save(FieldValue.create(d)).apply {
            insertHistory(this)
        }
    }

    override fun delete(id: String) {
        repository.deleteById(id)
    }

    override fun getByFieldId(fieldId: String): List<IFieldValue> = repository.findFieldValuesByFieldId(fieldId)

    override fun getByNodeId(nodeId: String): List<IFieldValue> = repository.findFieldValuesByNodeId(nodeId)

    override fun getSince(id: String, since: Long): List<IFieldValue> =
            fieldValueHistoryRepository.findFieldValueHistoryById(id)?.let { fieldValueHistory ->
                fieldValueHistory.records.filter { it.updateTime > since }.map {
                    FieldValue(
                            id = fieldValueHistory.id,
                            nodeId = fieldValueHistory.nodeId,
                            fieldId = fieldValueHistory.fieldId,
                            value = it.value,
                            session = it.session,
                            updateTime = it.updateTime
                    )
                }
            } ?: emptyList()

    private fun insertHistory(data: FieldValue) {
        template.upsert(
                Query.query(
                        Criteria
                                .where("id").`is`(data.id)
                                .and("nodeId").`is`(data.nodeId)
                                .and("fieldId").`is`(data.fieldId)
                ),
                Update().push("records")
                        .slice(-FIELD_VALUE_RECORD_HISTORY_CAPACITY)
                        .each(FieldValueRecord(
                                value = data.value,
                                session = data.session,
                                updateTime = data.updateTime
                        )),
                FieldValueHistory::class.java)
    }

}