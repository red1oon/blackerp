package org.blackerp.domain.core.ad.docstatus

sealed class DocStatusError(message: String) : Exception(message) {
    data class InvalidStatus(val status: String) : DocStatusError("Invalid status: $status")
    data class StatusTransitionInvalid(val from: String, val to: String) : 
        DocStatusError("Invalid transition from $from to $to")
}
