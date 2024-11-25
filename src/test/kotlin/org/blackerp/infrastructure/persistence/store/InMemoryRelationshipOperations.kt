package org.blackerp.infrastructure.persistence.store

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import org.blackerp.domain.table.TableError
import org.blackerp.domain.values.TableName
import org.blackerp.domain.table.relationship.TableRelationship
import org.blackerp.domain.table.relationship.RelationshipOperations
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class InMemoryRelationshipOperations : RelationshipOperations {
    private val relationships = ConcurrentHashMap<UUID, TableRelationship>()
    private val relationshipsByTable = ConcurrentHashMap<String, MutableSet<UUID>>()

    override suspend fun save(relationship: TableRelationship): Either<TableError, TableRelationship> =
        Either.catch {
            // Check for duplicate name
            if (relationships.values.any { 
                it.name == relationship.name && it.metadata.id != relationship.metadata.id 
            }) {
                return TableError.DuplicateTable(relationship.name.value).left()
            }

            relationships[relationship.metadata.id] = relationship
            
            // Index by source table
            relationshipsByTable.computeIfAbsent(relationship.sourceTable.value) { 
                mutableSetOf() 
            }.add(relationship.metadata.id)
            
            // Index by target table
            relationshipsByTable.computeIfAbsent(relationship.targetTable.value) { 
                mutableSetOf() 
            }.add(relationship.metadata.id)
            
            relationship
        }.mapLeft { 
            TableError.StorageError(it) 
        }

    override suspend fun findById(id: UUID): Either<TableError, TableRelationship?> =
        Either.catch {
            relationships[id]
        }.mapLeft { 
            TableError.StorageError(it) 
        }

    override suspend fun findByTable(tableName: TableName): Either<TableError, List<TableRelationship>> =
        Either.catch {
            relationshipsByTable[tableName.value]?.mapNotNull { 
                relationships[it] 
            } ?: emptyList()
        }.mapLeft { 
            TableError.StorageError(it) 
        }

    override suspend fun delete(id: UUID): Either<TableError, Unit> =
        Either.catch {
            relationships[id]?.let { relationship ->
                relationships.remove(id)
                relationshipsByTable[relationship.sourceTable.value]?.remove(id)
                relationshipsByTable[relationship.targetTable.value]?.remove(id)
                Unit
            } ?: return TableError.NotFound(id.toString()).left()
        }.mapLeft {
            TableError.StorageError(it)
        }
}
