package org.blackerp.domain.transaction

import arrow.core.Either
import arrow.core.right
import org.blackerp.domain.DomainEntity
import org.blackerp.domain.EntityMetadata
import org.blackerp.domain.values.Amount
import org.blackerp.domain.values.Currency
import java.time.Instant

data class Transaction(
    override val metadata: EntityMetadata,
    val amount: Amount,
    val currency: Currency,
    val timestamp: Instant,
    val description: String?
) : DomainEntity {
    companion object {
        fun create(params: CreateTransactionParams): Either<TransactionError, Transaction> =
            Transaction(
                metadata = params.metadata,
                amount = params.amount,
                currency = params.currency,
                timestamp = params.timestamp,
                description = params.description
            ).right()
    }
}

data class CreateTransactionParams(
    val metadata: EntityMetadata,
    val amount: Amount,
    val currency: Currency,
    val timestamp: Instant = Instant.now(),
    val description: String?
)
