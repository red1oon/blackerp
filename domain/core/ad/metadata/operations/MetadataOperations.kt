package org.blackerp.domain.core.ad.metadata.operations

import org.blackerp.domain.core.ad.metadata.entities.*
import org.blackerp.domain.core.ad.metadata.services.MetadataError
import arrow.core.Either
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface MetadataOperations {
    // Rule operations
    suspend fun saveRule(rule: ADRule): Either<MetadataError, ADRule>
    suspend fun findRuleById(id: UUID): Either<MetadataError, ADRule?>
    suspend fun findRulesByType(entityType: String): Flow<ADRule>
    suspend fun deleteRule(id: UUID): Either<MetadataError, Unit>
    
    // Validation operations  
    suspend fun saveValidation(rule: ADValidationRule): Either<MetadataError, ADValidationRule>
    suspend fun findValidationsByEntity(entityType: String): Flow<ADValidationRule>
    
    // Status operations
    suspend fun saveStatusLine(status: ADStatusLine): Either<MetadataError, ADStatusLine>
    suspend fun findStatusFlow(documentType: String): Flow<ADStatusLine>
}
