package org.blackerp.domain.table

import org.blackerp.domain.EntityMetadata
import org.blackerp.domain.values.ColumnName
import org.blackerp.domain.values.DataType
import org.blackerp.domain.values.Length
import org.blackerp.domain.values.Precision
import org.blackerp.domain.values.Scale
import org.blackerp.domain.values.DisplayName
import org.blackerp.domain.values.Description

// Unified CreateColumnParams definition

data class CreateColumnParams(
    val metadata: EntityMetadata,
    val name: ColumnName,
    val displayName: DisplayName,
    val description: Description?,
    val dataType: DataType,
    val length: Length?,
    val precision: Precision?,
    val scale: Scale?,
    val mandatory: Boolean = false,
    val defaultValue: String? = null
)
