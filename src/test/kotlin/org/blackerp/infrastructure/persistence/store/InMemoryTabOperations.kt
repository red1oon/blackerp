
package org.blackerp.infrastructure.persistence.store

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import org.blackerp.domain.ad.tab.*
import org.blackerp.domain.values.TableName
import org.blackerp.shared.ValidationError
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class InMemoryTabOperations : TabOperations {
    private val tabs = ConcurrentHashMap<UUID, ADTab>()
    private val tabsByTable = ConcurrentHashMap<String, MutableSet<UUID>>()

    override suspend fun save(tab: ADTab): Either<TabError, ADTab> =
        Either.catch {
            // Check for duplicate name
            val existingTab = tabs.values.find { 
                it.name == tab.name && it.metadata.id != tab.metadata.id 
            }
            if (existingTab != null) {
                return TabError.DuplicateTab(tab.name.value).left()
            }

            tabs[tab.metadata.id] = tab
            tabsByTable.computeIfAbsent(tab.table.name.value) { mutableSetOf() }
                .add(tab.metadata.id)
            tab
        }.mapLeft { e -> 
            TabError.ValidationFailed(
                listOf(ValidationError.InvalidValue(e.message ?: "Unknown error"))
            )
        }

    override suspend fun findById(id: UUID): Either<TabError, ADTab?> =
        Either.catch {
            tabs[id]
        }.mapLeft { e -> 
            TabError.ValidationFailed(
                listOf(ValidationError.InvalidValue(e.message ?: "Unknown error"))
            )
        }

    override suspend fun findByTable(tableName: TableName): Either<TabError, List<ADTab>> =
        Either.catch {
            tabsByTable[tableName.value]?.mapNotNull { tabs[it] } ?: emptyList()
        }.mapLeft { e -> 
            TabError.ValidationFailed(
                listOf(ValidationError.InvalidValue(e.message ?: "Unknown error"))
            )
        }

    override suspend fun delete(id: UUID): Either<TabError, Unit> =
        Either.catch {
            tabs[id]?.let { tab ->
                tabs.remove(id)
                tabsByTable[tab.table.name.value]?.remove(id)
                Unit
            } ?: throw IllegalStateException("Tab not found: $id")
        }.mapLeft { e -> 
            TabError.ValidationFailed(
                listOf(ValidationError.InvalidValue(e.message ?: "Unknown error"))
            )
        }
}
