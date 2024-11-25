package org.blackerp.api.dto

import jakarta.validation.constraints.*
import jakarta.validation.Valid

data class CreateTableRequest(
    @field:NotBlank
    @field:Pattern(regexp = "^[a-z][a-z0-9_]*$")
    @field:Size(min = 1, max = 60)
    val name: String,
    
    @field:NotBlank
    @field:Size(min = 1, max = 60)
    val displayName: String,
    
    val description: String?,
    
    @field:NotBlank
    val accessLevel: String,
    
    @field:Valid
    @field:NotEmpty
    val columns: List<CreateColumnRequest>
)

data class CreateColumnRequest(
    @field:NotBlank
    @field:Pattern(regexp = "^[a-z][a-z0-9_]*$")
    val name: String,
    
    @field:NotBlank
    val displayName: String,
    
    val description: String?,
    
    @field:NotNull
    val dataType: String,
    
    val length: Int?,
    val precision: Int?,
    val scale: Int?
)