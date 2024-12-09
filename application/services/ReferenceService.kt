package org.blackerp.application.services

import org.springframework.stereotype.Service
import org.blackerp.domain.core.ad.reference.*
import arrow.core.Either
import arrow.core.right
import arrow.core.left
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Service
class ReferenceService(
   private val referenceRepository: ReferenceRepository,
   private val referenceCache: ReferenceCache,
   private val metadataService: ADMetadataService
) : ReferenceOperations {
   override suspend fun save(reference: ADReference): Either<ReferenceError, ADReference> =
       referenceRepository.save(reference)

   override suspend fun findById(id: UUID): Either<ReferenceError, ADReference?> =
       referenceRepository.findById(id)

   override suspend fun findByName(refName: String): Either<ReferenceError, ADReference?> =
       referenceRepository.findByName(refName)
       
   // Other implementations...
   override suspend fun search(query: String, pageSize: Int, page: Int): Flow<ADReference> =
       referenceRepository.search(query, pageSize, page)

   override suspend fun getValues(referenceId: UUID, searchText: String?, pageSize: Int, page: Int): Either<ReferenceError, List<ReferenceValue<*>>> = TODO()

   override suspend fun validateValue(referenceId: UUID, value: Any): Either<ReferenceError, Boolean> = TODO()

   override suspend fun getHierarchy(rootId: UUID?): Either<ReferenceError, List<ADReference>> = TODO()

   override suspend fun delete(id: UUID): Either<ReferenceError, Unit> = TODO()
}
