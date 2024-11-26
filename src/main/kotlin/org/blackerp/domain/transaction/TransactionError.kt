package org.blackerp.domain.transaction

import org.blackerp.shared.ValidationError

sealed class TransactionError(message: String) {
    data class ValidationFailed(val errors: List<ValidationError>) : 
        TransactionError("Validation failed: ${errors.joinToString { it.message }}")
    
    data class InvalidAmount(val amount: String) : 
        TransactionError("Invalid amount: $amount")
    
    data class InvalidCurrency(val currency: String) : 
        TransactionError("Invalid currency: $currency")
}
