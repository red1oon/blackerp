package org.blackerp.infrastructure.core.logging

import org.slf4j.LoggerFactory

object DomainLogger {
    fun getLogger(name: String) = LoggerFactory.getLogger(name)
}
