package net.gridtech.repository.data

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository


@Repository
interface NodeClassRepository : MongoRepository<NodeClass, String> {
    fun findNodeClassesByTagsContains(tags: List<String>): List<NodeClass>
}

@Repository
interface FieldRepository : MongoRepository<Field, String> {
    fun findFieldsByNodeClassId(nodeClassId: String): List<Field>
    fun findFieldsByNodeClassIdIn(nodeClassIds: List<String>): List<Field>
}

@Repository
interface NodeRepository : MongoRepository<Node, String> {
    fun findNodesByNodeClassId(nodeClassId: String): List<Node>
    fun findNodesByNodeClassIdIn(nodeClassIds: List<String>): List<Node>
}

@Repository
interface FieldValueRepository : MongoRepository<FieldValue, String> {
    fun findFieldValuesByFieldId(fieldId: String): List<FieldValue>
    fun findFieldValuesByNodeId(nodeId: String): List<FieldValue>
}

@Repository
interface FieldValueHistoryRepository : MongoRepository<FieldValueHistory, String> {
    fun findFieldValueHistoryById(id: String): FieldValueHistory?
}