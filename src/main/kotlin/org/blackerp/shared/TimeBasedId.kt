package org.blackerp.shared

import com.fasterxml.uuid.Generators
import java.util.UUID

object TimeBasedId {
    private val timeBasedGenerator = Generators.timeBasedGenerator()
    
    fun generate(): UUID = timeBasedGenerator.generate()
}