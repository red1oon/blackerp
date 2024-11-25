package org.blackerp.domain.table.relationship

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.types.shouldBeTypeOf
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.assertions.arrow.core.shouldBeLeft

import kotlinx.coroutines.test.runTest
import org.blackerp.domain.table.TableError
import org.blackerp.infrastructure.persistence.store.InMemoryRelationshipOperations
import org.slf4j.LoggerFactory

class RelationshipOperationsTest : DescribeSpec({
    val logger = LoggerFactory.getLogger(RelationshipOperationsTest::class.java)
    
    lateinit var operations: RelationshipOperations

    beforeTest {
        operations = InMemoryRelationshipOperations()
    }

    describe("RelationshipOperations") {
        it("should save and retrieve relationship") {
            runTest {
                val relationship = RelationshipTestFactory.createValidRelationship()
                logger.debug("Created test relationship: ${relationship.name.value}")

                val saveResult = operations.save(relationship)
                val findResult = operations.findById(relationship.metadata.id)

                saveResult.shouldBeRight().also { saved ->
                    saved.metadata.id shouldBe relationship.metadata.id
                    saved.name shouldBe relationship.name
                }
                
                findResult.shouldBeRight().also { found ->
                    found shouldNotBe null
                    found?.metadata?.id shouldBe relationship.metadata.id
                    found?.name shouldBe relationship.name
                }
            }
        }

        it("should find relationships by table") {
            runTest {
                val relationship = RelationshipTestFactory.createValidRelationship()
                
                operations.save(relationship).shouldBeRight()

                val result = operations.findByTable(relationship.sourceTable)
                
                result.shouldBeRight().also { relationships ->
                    relationships.shouldNotBeEmpty()
                    relationships.size shouldBe 1
                    val foundRelationship = relationships[0]
                    foundRelationship.shouldBeTypeOf<TableRelationship>()
                    foundRelationship.sourceTable.value shouldBe relationship.sourceTable.value
                    foundRelationship.metadata.id shouldBe relationship.metadata.id
                }
            }
        }

        it("should prevent duplicate relationship names") {
            runTest {
                val first = RelationshipTestFactory.createValidRelationship()
                val second = RelationshipTestFactory.createValidRelationship()

                operations.save(first).shouldBeRight()
                operations.save(second).shouldBeLeft().also { error ->
                    error shouldBe TableError.DuplicateTable(second.name.value)
                }
            }
        }

        it("should delete relationship") {
            runTest {
                val relationship = RelationshipTestFactory.createValidRelationship()
                
                operations.save(relationship).shouldBeRight()
                
                operations.delete(relationship.metadata.id).shouldBeRight()
                operations.findById(relationship.metadata.id).shouldBeRight() shouldBe null
            }
        }
    }
})