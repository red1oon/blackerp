package org.blackerp.domain.core.ad.metadata.services

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.blackerp.domain.core.ad.metadata.entities.*
import org.blackerp.domain.core.ad.metadata.repositories.MetadataRepository
import org.blackerp.domain.events.EventMetadata
import org.blackerp.infrastructure.events.publishers.DomainEventPublisher
import arrow.core.Either
import kotlinx.coroutines.flow.*
import java.util.UUID
import org.slf4j.LoggerFactory

@Service
class MetadataServiceImpl(
    private val metadataRepository: MetadataRepository,
    private val eventPublisher: DomainEventPublisher
) : MetadataService {
    
    private val logger = LoggerFactory.getLogger(MetadataServiceImpl::class.java)
    
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
