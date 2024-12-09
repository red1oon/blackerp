package org.blackerp.application.services

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.blackerp.domain.core.ad.table.TableOperations
import org.blackerp.domain.core.ad.table.ADTable
import org.blackerp.domain.core.error.TableError
import arrow.core.Either
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Service
class TableService(
   private val tableOperations: TableOperations
) {
   @Transactional(readOnly = true)
   suspend fun findAll(): Either<TableError, List<ADTable>> = 
       withContext(Dispatchers.IO) {
           tableOperations.findAll()
       }

   @Transactional
   suspend fun save(table: ADTable): Either<TableError, ADTable> =
       withContext(Dispatchers.IO) {
           tableOperations.save(table)
       }

   suspend fun findById(id: UUID): Either<TableError, ADTable?> =
       withContext(Dispatchers.IO) {
           tableOperations.findById(id)
       }

   @Transactional
   suspend fun delete(id: UUID): Either<TableError, Unit> =
       withContext(Dispatchers.IO) {
           tableOperations.delete(id)
       }
}
