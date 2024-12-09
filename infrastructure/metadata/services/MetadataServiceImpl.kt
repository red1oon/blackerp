package org.blackerp.infrastructure.metadata.services

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.blackerp.domain.core.ad.metadata.services.MetadataService
import org.blackerp.domain.core.ad.metadata.services.MetadataError
import org.blackerp.domain.core.ad.metadata.entities.*
import arrow.core.Either
import kotlinx.coroutines.flow.*
import java.util.UUID

@Service
class MetadataServiceImpl(
    private val metadataRepository: MetadataRepository
) : MetadataService {

    @Transactional(readOnly = true)
    override suspend fun getRules(entityType: String): Flow<ADRule> =
        metadataRepository.findRulesByType(entityType)

    @Transactional(readOnly = true)
    override suspend fun getRule(id: UUID): Either<MetadataError, ADRule?> =
        metadataRepository.findById(id, ADRule::class.java)

    @Transactional(readOnly = true)
    override suspend fun getValidations(entityType: String): Flow<ADValidationRule> =
        metadataRepository.findValidationsByEntity(entityType)

    @Transactional(readOnly = true)
    override suspend fun getStatusFlow(documentType: String): Flow<ADStatusLine> =
        metadataRepository.findStatusLines(documentType)
}
