package org.blackerp.domain.query

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.should
import io.kotest.matchers.types.beInstanceOf

class QueryCriteriaTest : DescribeSpec({
    describe("QueryCriteria") {
        it("should create complex criteria") {
            val criteria = QueryCriteria.And(
                listOf(
                    QueryCriteria.Equals("name", "test"),
                    QueryCriteria.Or(
                        listOf(
                            QueryCriteria.Like("description", "%test%"),
                            QueryCriteria.In("status", listOf("active", "pending"))
                        )
                    )
                )
            )
            
            criteria should beInstanceOf<QueryCriteria.And>()
        }
    }
})
