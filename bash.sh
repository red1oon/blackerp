#!/bin/bash

# reorganize-dtos.sh
# Run from project root directory

echo "Reorganizing DTOs into separate files..."

# Create directories
mkdir -p src/main/kotlin/org/blackerp/api/dto/request
mkdir -p src/main/kotlin/org/blackerp/api/dto/response

# Create request DTOs
cat > src/main/kotlin/org/blackerp/api/dto/request/TableRequests.kt << 'EOL'
package org.blackerp.api.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import jakarta.validation.Valid
import jakarta.validation.constraints.NotEmpty

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
    
    @field:NotBlank
    val dataType: String,
    
    val length: Int?,
    val precision: Int?,
    val scale: Int?
)
EOL

# Create response DTOs
cat > src/main/kotlin/org/blackerp/api/dto/response/TableResponses.kt << 'EOL'
package org.blackerp.api.dto.response

import java.util.UUID

data class TableResponse(
    val id: UUID,
    val name: String,
    val displayName: String,
    val description: String?,
    val accessLevel: String
)

data class TablesResponse(
    val tables: List<TableResponse>
)
EOL

# Update TableController to use new DTO packages
cat > src/main/kotlin/org/blackerp/api/controllers/TableController.kt << 'EOL'
package org.blackerp.api.controllers

import org.springframework.web.bind.annotation.*
import org.springframework.http.ResponseEntity
import org.blackerp.application.table.CreateTableUseCase
import org.blackerp.domain.table.TableOperations
import org.blackerp.api.dto.response.TableResponse
import org.blackerp.api.dto.response.TablesResponse
import org.slf4j.LoggerFactory
import java.util.UUID

@RestController
@RequestMapping("/api/tables")
class TableController(
    private val tableOperations: TableOperations,
    private val createTableUseCase: CreateTableUseCase
) {
    private val logger = LoggerFactory.getLogger(TableController::class.java)

    @GetMapping
    suspend fun getTables(): ResponseEntity<TablesResponse> {
        logger.debug("Fetching all tables")
        return ResponseEntity.ok(
            TablesResponse(
                tables = listOf(
                    TableResponse(
                        id = UUID.randomUUID(),
                        name = "example_table",
                        displayName = "Example Table",
                        description = "An example table for testing",
                        accessLevel = "SYSTEM"
                    )
                )
            )
        )
    }

    @GetMapping("/{id}")
    suspend fun getTable(@PathVariable id: UUID): ResponseEntity<TableResponse> {
        logger.debug("Fetching table with id: $id")
        // TODO: Implement actual table fetching
        return ResponseEntity.ok(
            TableResponse(
                id = id,
                name = "example_table",
                displayName = "Example Table",
                description = "An example table for testing",
                accessLevel = "SYSTEM"
            )
        )
    }
}
EOL

# Remove any old DTO files to prevent conflicts
rm -f src/main/kotlin/org/blackerp/api/dto/TableResponse.kt
rm -f src/main/kotlin/org/blackerp/api/dto/TableResponses.kt
rm -f src/main/kotlin/org/blackerp/api/dto/CreateTableRequest.kt

# Update build.gradle.kts to ensure validation dependency
cat >> build.gradle.kts << 'EOL'

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-validation")
}
EOL

echo "DTO reorganization complete!"
echo "DTOs are now organized in:"
echo "- request/TableRequests.kt"
echo "- response/TableResponses.kt"
echo "Make sure to run './gradlew build' to verify changes"

chmod +x reorganize-dtos.sh

The changes:
1. Separated DTOs into request and response packages
2. Added validation annotations to request DTOs
3. Updated imports in TableController
4. Removed old conflicting files
5. Added validation dependency

To use:
1. Save as `reorganize-dtos.sh`
2. Make executable: `chmod +x reorganize-dtos.sh`
3. Run: `./reorganize-dtos.sh`
4. Run `./gradlew build` to verify changes

Would you like me to:
1. Add more validations to the DTOs?
2. Add error handling for validation failures?
3. Add more endpoints to the controller?