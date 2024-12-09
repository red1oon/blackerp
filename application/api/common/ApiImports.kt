package org.blackerp.application.api.common

import org.springframework.web.bind.annotation.*
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import jakarta.validation.Valid
import org.blackerp.domain.core.error.DomainError
import org.blackerp.domain.core.ad.table.ADTable
import org.blackerp.domain.core.metadata.*
import org.blackerp.domain.core.values.*
import java.util.*
import arrow.core.Either
import kotlinx.coroutines.flow.Flow
