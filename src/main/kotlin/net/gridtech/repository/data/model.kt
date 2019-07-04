package net.gridtech.repository.data

import net.gridtech.core.data.IField
import net.gridtech.core.data.IFieldValue
import net.gridtech.core.data.INode
import net.gridtech.core.data.INodeClass
import net.gridtech.core.util.currentTime
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document


@Document
data class NodeClass(
        @Id
        override var id: String,
        override var name: String,
        override var alias: String,
        override var description: String,
        override var connectable: Boolean,
        override var tags: List<String>,
        override var updateTime: Long
) : INodeClass {
    companion object {
        fun create(nodeClass: INodeClass) = NodeClass(
                id = nodeClass.id,
                name = nodeClass.name,
                alias = nodeClass.alias,
                description = nodeClass.description,
                connectable = nodeClass.connectable,
                tags = nodeClass.tags,
                updateTime = nodeClass.updateTime
        )

        fun create(id: String, name: String, alias: String, description: String, connectable: Boolean, tags: List<String>): NodeClass =
                NodeClass(
                        id = id,
                        name = name,
                        alias = alias,
                        description = description,
                        connectable = connectable,
                        tags = tags,
                        updateTime = currentTime()
                )

        fun update(original: INodeClass, name: String, alias: String, description: String) =
                NodeClass(
                        id = original.id,
                        name = name,
                        alias = alias,
                        description = description,
                        connectable = original.connectable,
                        tags = original.tags,
                        updateTime = original.updateTime
                )
    }


}

@Document
data class Field(
        @Id
        override var id: String,
        override var name: String,
        override var alias: String,
        override var description: String,
        override var tags: List<String>,
        @Indexed
        override var nodeClassId: String,
        override var through: Boolean,
        override var updateTime: Long
) : IField {
    companion object {
        fun create(field: IField) = Field(
                id = field.id,
                name = field.name,
                alias = field.alias,
                description = field.description,
                nodeClassId = field.nodeClassId,
                tags = field.tags,
                through = field.through,
                updateTime = field.updateTime
        )

        fun create(id: String, nodeClass: INodeClass, name: String, alias: String, description: String, tags: List<String>, through: Boolean): Field = Field(
                id = id,
                name = name,
                alias = alias,
                description = description,
                nodeClassId = nodeClass.id,
                through = through,
                tags = tags,
                updateTime = currentTime()
        )

        fun update(original: IField, name: String, alias: String, description: String) = Field(
                id = original.id,
                name = name,
                alias = alias,
                description = description,
                nodeClassId = original.nodeClassId,
                tags = original.tags,
                through = original.through,
                updateTime = original.updateTime
        )
    }

}

@Document
data class Node(
        @Id
        override var id: String,
        override var name: String,
        override var alias: String,
        override var description: String,
        override var tags: List<String>,
        @Indexed
        override var nodeClassId: String,
        override var path: List<String>,
        override var externalNodeIdScope: List<String>,
        override var externalNodeClassTagScope: List<String>,
        override var updateTime: Long
) : INode {
    companion object {
        fun create(node: INode) = Node(
                id = node.id,
                name = node.name,
                alias = node.alias,
                description = node.description,
                nodeClassId = node.nodeClassId,
                tags = node.tags,
                path = node.path,
                externalNodeIdScope = node.externalNodeIdScope,
                externalNodeClassTagScope = node.externalNodeClassTagScope,
                updateTime = node.updateTime
        )

        fun create(id: String,
                   nodeClass: INodeClass,
                   name: String,
                   alias: String,
                   description: String,
                   tags: List<String>,
                   parent: INode?,
                   externalNodeIdScope: List<String>,
                   externalNodeClassTagScope: List<String>): Node {
            return Node(
                    id = id,
                    name = name,
                    alias = alias,
                    description = description,
                    tags = tags,
                    nodeClassId = nodeClass.id,
                    path = parent?.path?.toMutableList()?.apply { add(parent.id) } ?: emptyList(),
                    externalNodeIdScope = externalNodeIdScope,
                    externalNodeClassTagScope = externalNodeClassTagScope,
                    updateTime = currentTime()
            )
        }

        fun update(original: INode, name: String, alias: String, description: String) =
                Node(
                        id = original.id,
                        name = name,
                        alias = alias,
                        description = description,
                        nodeClassId = original.nodeClassId,
                        tags = original.tags,
                        path = original.path,
                        externalNodeIdScope = original.externalNodeIdScope,
                        externalNodeClassTagScope = original.externalNodeClassTagScope,
                        updateTime = original.updateTime
                )
    }
}


@Document
data class FieldValue(
        @Id
        override var id: String,
        @Indexed
        override var nodeId: String,
        @Indexed
        override var fieldId: String,
        override var value: String,
        override var updateTime: Long,
        override var session: String
) : IFieldValue {
    companion object {
        fun create(fieldValue: IFieldValue) = FieldValue(
                id = fieldValue.id,
                nodeId = fieldValue.nodeId,
                fieldId = fieldValue.fieldId,
                value = fieldValue.value,
                session = fieldValue.session,
                updateTime = fieldValue.updateTime
        )

        fun empty() = FieldValue(
                id = "",
                nodeId = "",
                fieldId = "",
                value = "",
                session = "",
                updateTime = -1
        )
    }
}


@Document
data class FieldValueHistory(
        @Id
        var id: String,
        @Indexed
        var nodeId: String,
        @Indexed
        var fieldId: String,
        var records: List<FieldValueRecord>
)

data class FieldValueRecord(
        var value: String,
        var session: String,
        var updateTime: Long
)