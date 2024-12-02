package org.blackerp.application.api.controllers

import org.springframework.web.bind.annotation.*
import org.springframework.http.ResponseEntity
import org.blackerp.domain.ad.reference.*
import org.blackerp.application.services.ReferenceService
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import java.util.UUID

@RestController
@RequestMapping("/api/references")
class ReferenceController(
    private val referenceService: ReferenceService
) {
    private val logger = LoggerFactory.getLogger(ReferenceController::class.java)

    @PostMapping
    suspend fun createReference(
        @Valid @RequestBody request: CreateReferenceRequest
    ): ResponseEntity<ADReference> {
        logger.debug("Creating reference: ${request.name}")
        return referenceService.save(request.toDomain()).fold(
            { error -> ResponseEntity.badRequest().build() },
            { reference -> ResponseEntity.ok(reference) }
        )
    }

    @GetMapping("/{id}")
    suspend fun getReference(@PathVariable id: UUID): ResponseEntity<ADReference> {
        return referenceService.findById(id).fold(
            { error -> ResponseEntity.notFound().build() },
            { reference -> 
                reference?.let { ResponseEntity.ok(it) } 
                    ?: ResponseEntity.notFound().build()
            }
        )
    }

    @GetMapping("/{id}/values")
    suspend fun getReferenceValues(
        @PathVariable id: UUID,
        @RequestParam(required = false) search: String?,
        @RequestParam(defaultValue = "20") pageSize: Int,
        @RequestParam(defaultValue = "0") page: Int
    ): ResponseEntity<List<ReferenceValue<*>>> {
        return referenceService.getValues(id, search, pageSize, page).fold(
            { error -> ResponseEntity.badRequest().build() },
            { values -> ResponseEntity.ok(values) }
        )
    }

    @GetMapping("/{id}/hierarchy")
    suspend fun getReferenceHierarchy(
        @PathVariable id: UUID
    ): ResponseEntity<List<ADReference>> {
        return referenceService.getHierarchy(id).fold(
            { error -> ResponseEntity.badRequest().build() },
            { hierarchy -> ResponseEntity.ok(hierarchy) }
        )
    }

    @DeleteMapping("/{id}")
    suspend fun deleteReference(@PathVariable id: UUID): ResponseEntity<Unit> {
        return referenceService.delete(id).fold(
            { error -> ResponseEntity.badRequest().build() },
            { ResponseEntity.noContent().build() }
        )
    }

    data class CreateReferenceRequest(
        val name: String,
        val displayName: String,
        val description: String?,
        val type: ReferenceTypeRequest,
        val validationRule: ValidationRuleRequest?,
        val parentId: UUID?,
        val sortOrder: Int?,
        val cacheStrategy: String?
    ) {
        fun toDomain(): ADReference {
            // Implement conversion to domain object
            TODO("Implement conversion")
        }
    }

    data class ReferenceTypeRequest(
        val type: String,
        val config: Map<String, String>
    )

    data class ValidationRuleRequest(
        val expression: String,
        val errorMessage: String,
        val parameters: Map<String, String>
    )
}
