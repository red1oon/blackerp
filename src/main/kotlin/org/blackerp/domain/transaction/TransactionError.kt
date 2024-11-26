package org.blackerp.domain.transaction

import org.blackerp.shared.ValidationError

sealed class TransactionError {
    abstract val message: String

    data class ValidationFailed(
        override val message: String,
        val errors: List<ValidationError>
    ) : TransactionError()
    
    data class InvalidAmount(
        val amount: String,
        override val message: String = "Invalid amount: $amount"
    ) : TransactionError()
    
    data class InvalidCurrency(
        val currency: String,
        override val message: String = "Invalid currency: $currency"
    ) : TransactionError()
}
