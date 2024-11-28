# Project Structure

src/
    main/
        resources/
            application.yml
            db/
                migration/
                    V3__Create_tab_schema.sql
                    V2_create_relationship_schema.sql
                    V1__Create_table_schema.sql
        kotlin/
            org/
                blackerp/
                    BlackErpApplication.kt
                    infrastructure/
                        event/
                            DefaultEventPublisher.kt
                            EventPublisher.kt
                        cache/
                            CacheService.kt
                            InMemoryCacheService.kt
                        persistence/
                            store/
                                PostgresTableOperations.kt
                                PostgresRelationshipOperations.kt
                                PostgresTabOperations.kt
                        store/
                    plugin/
                        PluginError.kt
                        Plugin.kt
                        ExtensionRegistry.kt
                        PluginId.kt
                        Version.kt
                        Extension.kt
                        PluginMetadata.kt
                        discovery/
                            FileSystemPluginDiscovery.kt
                            PluginDiscovery.kt
                        registry/
                            PluginRegistry.kt
                            DefaultPluginRegistry.kt
                    domain/
                        DomainException.kt
                        DomainEntity.kt
                        DomainEvent.kt
                        EntityMetadata.kt
                        ad/
                            ADModule.kt
                            ADObject.kt
                            window/
                                value/
                                    WindowName.kt
                            tab/
                                TabOperations.kt
                                CreateTabParams.kt
                                OrderBySpec.kt
                                TabError.kt
                                ADTab.kt
                                value/
                                    TabName.kt
                            value/
                                ModuleName.kt
                            reference/
                                ADReference.kt
                                value/
                                    ReferenceName.kt
                        event/
                            EventMetadata.kt
                            DomainEvent.kt
                            TableCreated.kt
                        error/
                            DomainError.kt
                        tenant/
                            TenantFilter.kt
                            TenantContext.kt
                            TenantAware.kt
                        values/
                            DataType.kt
                            Precision.kt
                            AccessLevel.kt
                            Description.kt
                            Amount.kt
                            ColumnName.kt
                            Scale.kt
                            Length.kt
                            EventMetadata.kt
                            TableName.kt
                            Currency.kt
                            DisplayName.kt
                        transaction/
                            Transaction.kt
                            TransactionError.kt
                        query/
                            QueryResult.kt
                            QueryCriteria.kt
                        common/
                        table/
                            TableOperations.kt
                            ADTable.kt
                            CreateColumnParams.kt
                            TenantAwareTable.kt
                            TableCreated.kt
                            TableError.kt
                            TableId.kt
                            ColumnDefinition.kt
                            extension/
                                TableExtensionPoint.kt
                                TableExtension.kt
                            constraint/
                                TableConstraint.kt
                                UniqueConstraint.kt
                                NotNullConstraint.kt
                                ReferenceConstraint.kt
                            definition/
                                TableDefinition.kt
                            behavior/
                                TableBehavior.kt
                            relationship/
                                CreateRelationshipParams.kt
                                TableRelationship.kt
                                RelationshipConstraint.kt
                                RelationshipOperations.kt
                                TableRelationshipError.kt
                                event/
                                    RelationshipEvent.kt
                                constraint/
                                    IndexConstraint.kt
                                value/
                                    RelationshipName.kt
                                    RelationType.kt
                                    DeleteRule.kt
                                    UpdateRule.kt
                        security/
                            SecurityContext.kt
                    config/
                        WebConfig.kt
                        EventConfig.kt
                    api/
                        mappers/
                            TableMapper.kt
                        dto/
                            request/
                                CreateTableRequest.kt
                            response/
                                TableResponses.kt
                        error/
                            ErrorHandler.kt
                        controllers/
                            TableController.kt
                        validation/
                    application/
                        table/
                            CreateTableUseCase.kt
                            CreateTableCommand.kt
                    shared/
                        ValidationError.kt
                        TimeBasedId.kt
                        ReferenceValidation.kt
    test/
        resources/
            logback-test.xml
            application.yml
            application-test.properties
            application-test.yml
            application.yml.backup.20241127_051405
            db/
                h2-schema.sql
                schema.sql
                migration/
                    V4__create_test_tables.sql
        kotlin/
            org/
                blackerp/
                    infrastructure/
                        cache/
                            InMemoryCacheServiceTest.kt
                        persistence/
                            TestDatabaseConfig.kt
                            SimpleTest.kt
                            store/
                                InMemoryTabOperations.kt
                                InMemoryRelationshipOperations.kt
                                InMemoryTableOperations.kt
                                PostgresTabOperationsTest.kt
                    plugin/
                        TestPlugin.kt
                        InMemoryExtensionRegistry.kt
                        PluginIdTest.kt
                        VersionTest.kt
                        PluginTest.kt
                        PluginMetadataTest.kt
                        discovery/
                            FileSystemPluginDiscoveryTest.kt
                        integration/
                        registry/
                            DefaultPluginRegistryTest.kt
                    domain/
                        EntityMetadataTest.kt
                        ad/
                            ADModuleTest.kt
                            window/
                                value/
                                    WindowNameTest.kt
                            tab/
                                ADTabTest.kt
                                value/
                                    TabNameTest.kt
                            value/
                                ModuleNameTest.kt
                            reference/
                                ADReferenceTest.kt
                                value/
                                    ReferenceNameTest.kt
                        error/
                            DomainErrorTest.kt
                        tenant/
                            TenantAwareTableTest.kt
                            TenantContextTest.kt
                        values/
                            AmountTest.kt
                            DisplayNameTest.kt
                            TableNameTest.kt
                            CurrencyTest.kt
                            DataTypeTest.kt
                        transaction/
                            TransactionTest.kt
                        query/
                            QueryCriteriaTest.kt
                        table/
                            TableOperationsTest.kt
                            extension/
                                TableExtensionPointTest.kt
                            constraint/
                                ReferenceConstraintTest.kt
                                UniqueConstraintTest.kt
                                NotNullConstraintTest.kt
                            definition/
                                TableDefinitionTest.kt
                            behavior/
                            relationship/
                                TableRelationshipTest.kt
                                RelationshipOperationsTest.kt
                                RelationshipTestFactory.kt
                    config/
                        UnifiedTestConfig.kt
                        TestConfig.kt
                        KotestProjectConfig.kt
                        TestEventConfig.kt
                    api/
                        controllers/
                            TableControllerTest.kt
                    application/
                        table/
                            CreateTableUseCaseTest.kt
                    shared/
                        TimeBasedIdTest.kt
                        TestFactory.kt
                    integration/
                        IntegrationTestConfig.kt
                        plugin/
                            PluginLifecycleIntegrationTest.kt
                        api/
                            TableApiIntegrationTest.kt
                        db/
                            TableRepositoryIntegrationTest.kt

# API and Class Summary

### File: BlackErpApplication.kt
- **Package**: `org.blackerp`
- **Class**: `BlackErpApplication`
  - **Method**: `main(args: Array<String>) -> Unit`

### File: DefaultEventPublisher.kt
- **Package**: `org.blackerp.infrastructure.event`
- **Class**: `DefaultEventPublisher`
  - **Method**: `publish(event: DomainEvent) -> : Either<TableError`

### File: InMemoryCacheService.kt
- **Package**: `org.blackerp.infrastructure.cache`
- **Class**: `InMemoryCacheService`
  - **Method**: `delete(key: String) -> : Either<DomainError`
  - **Method**: `clear() -> : Either<DomainError`

- **Class**: `CacheEntry`
  - **Method**: `delete(key: String) -> : Either<DomainError`
  - **Method**: `clear() -> : Either<DomainError`

### File: PostgresTableOperations.kt
- **Package**: `org.blackerp.infrastructure.persistence.store`
- **Class**: `PostgresTableOperations`
  - **Method**: `findAll() -> : Either<TableError`
  - **Method**: `save(table: ADTable) -> : Either<TableError`
  - **Method**: `findById(id: UUID) -> : Either<TableError`
  - **Method**: `findByName(name: String) -> : Either<TableError`
  - **Method**: `delete(id: UUID) -> : Either<TableError`
  - **Method**: `mapRow(rs: ResultSet, rowNum: Int) -> : ADTable`

- **Class**: `TableRowMapper`
  - **Method**: `findAll() -> : Either<TableError`
  - **Method**: `save(table: ADTable) -> : Either<TableError`
  - **Method**: `findById(id: UUID) -> : Either<TableError`
  - **Method**: `findByName(name: String) -> : Either<TableError`
  - **Method**: `delete(id: UUID) -> : Either<TableError`
  - **Method**: `mapRow(rs: ResultSet, rowNum: Int) -> : ADTable`

### File: PostgresRelationshipOperations.kt
- **Package**: `org.blackerp.infrastructure.persistence.store`
- **Class**: `PostgresRelationshipOperations`
  - **Method**: `save(relationship: TableRelationship) -> : Either<TableError`
  - **Method**: `findById(id: UUID) -> : Either<TableError`
  - **Method**: `findByTable(tableName: TableName) -> : Either<TableError`
  - **Method**: `delete(id: UUID) -> : Either<TableError`
  - **Method**: `mapRow(rs: ResultSet, rowNum: Int) -> : TableRelationship`

- **Class**: `RelationshipRowMapper`
  - **Method**: `save(relationship: TableRelationship) -> : Either<TableError`
  - **Method**: `findById(id: UUID) -> : Either<TableError`
  - **Method**: `findByTable(tableName: TableName) -> : Either<TableError`
  - **Method**: `delete(id: UUID) -> : Either<TableError`
  - **Method**: `mapRow(rs: ResultSet, rowNum: Int) -> : TableRelationship`

### File: PostgresTabOperations.kt
- **Package**: `org.blackerp.infrastructure.persistence.store`
- **Class**: `PostgresTabOperations`
  - **Method**: `debug(msg: String) -> Unit`
  - **Method**: `save(tab: ADTab) -> : Either<TabError`
  - **Method**: `findById(id: UUID) -> : Either<TabError`
  - **Method**: `findByTable(tableName: TableName) -> : Either<TabError`
  - **Method**: `delete(id: UUID) -> : Either<TabError`
  - **Method**: `mapRow(rs: ResultSet, rowNum: Int) -> : ADTab`

- **Class**: `TabRowMapper`
  - **Method**: `debug(msg: String) -> Unit`
  - **Method**: `save(tab: ADTab) -> : Either<TabError`
  - **Method**: `findById(id: UUID) -> : Either<TabError`
  - **Method**: `findByTable(tableName: TableName) -> : Either<TabError`
  - **Method**: `delete(id: UUID) -> : Either<TabError`
  - **Method**: `mapRow(rs: ResultSet, rowNum: Int) -> : ADTab`

### File: PluginError.kt
- **Package**: `org.blackerp.plugin`
- **Class**: `NotFound`

- **Class**: `ValidationFailed`

- **Class**: `DuplicatePlugin`

- **Class**: `IncompatibleVersion`

- **Class**: `InitializationFailed`

- **Class**: `DiscoveryFailed`

- **Class**: `LoadFailed`

### File: PluginId.kt
- **Package**: `org.blackerp.plugin`
- **Class**: `PluginId`
  - **Method**: `constructor(val value: String) -> Unit`
  - **Method**: `create(value: String) -> : Either<ValidationError`

### File: Version.kt
- **Package**: `org.blackerp.plugin`
- **Class**: `Version`
  - **Method**: `constructor(val major: Int, val minor: Int, val patch: Int) -> : Comparable<Version>`
  - **Method**: `compareTo(other: Version) -> : Int`
  - **Method**: `create(version: String) -> : Either<ValidationError`
  - **Method**: `toString() -> : String`

### File: PluginMetadata.kt
- **Package**: `org.blackerp.plugin`
- **Class**: `PluginMetadata`
  - **Method**: `constructor(val id: PluginId, val version: Version, val name: String, val description: String, val vendor: String) -> Unit`
  - **Method**: `create(id: PluginId, version: Version, name: String, description: String, vendor: String) -> : Either<ValidationError`

### File: FileSystemPluginDiscovery.kt
- **Package**: `org.blackerp.plugin.discovery`
- **Class**: `FileSystemPluginDiscovery`
  - **Method**: `discoverPlugins(directory: Path) -> : Either<PluginError`
  - **Method**: `loadPlugin(jarPath: Path) -> : Either<PluginError`

### File: DefaultPluginRegistry.kt
- **Package**: `org.blackerp.plugin.registry`
- **Class**: `DefaultPluginRegistry`
  - **Method**: `register(plugin: Plugin) -> : Either<PluginError`
  - **Method**: `unregister(pluginId: PluginId) -> : Either<PluginError`
  - **Method**: `getPlugin(pluginId: PluginId) -> : Either<PluginError`
  - **Method**: `getPlugins() -> : List<Plugin>`

### File: DomainException.kt
- **Package**: `org.blackerp.domain`
- **Class**: `DomainException`

### File: EntityMetadata.kt
- **Package**: `org.blackerp.domain`
- **Class**: `EntityMetadata`

### File: ADModule.kt
- **Package**: `org.blackerp.domain.ad`
- **Class**: `ADModule`
  - **Method**: `create(params: CreateModuleParams) -> : Either<ValidationError`

- **Class**: `CreateModuleParams`
  - **Method**: `create(params: CreateModuleParams) -> : Either<ValidationError`

### File: WindowName.kt
- **Package**: `org.blackerp.domain.ad.window.value`
- **Class**: `WindowName`
  - **Method**: `constructor(val value: String) -> Unit`
  - **Method**: `create(value: String) -> : Either<ValidationError`

### File: CreateTabParams.kt
- **Package**: `org.blackerp.domain.ad.tab`
- **Class**: `CreateTabParams`

### File: OrderBySpec.kt
- **Package**: `org.blackerp.domain.ad.tab`
- **Class**: `OrderBySpec`
  - **Method**: `fromString(value: String) -> : SortDirection`

- **Class**: `SortDirection`
  - **Method**: `fromString(value: String) -> : SortDirection`

### File: TabError.kt
- **Package**: `org.blackerp.domain.ad.tab`
- **Class**: `TabError`

- **Class**: `ValidationFailed`

- **Class**: `NotFound`

- **Class**: `DuplicateTab`

### File: ADTab.kt
- **Package**: `org.blackerp.domain.ad.tab`
- **Class**: `ADTab`
  - **Method**: `create(params: CreateTabParams) -> : Either<TabError`

### File: TabName.kt
- **Package**: `org.blackerp.domain.ad.tab.value`
- **Class**: `TabName`
  - **Method**: `constructor(val value: String) -> Unit`
  - **Method**: `create(value: String) -> : Either<ValidationError`

### File: ModuleName.kt
- **Package**: `org.blackerp.domain.ad.value`
- **Class**: `ModuleName`
  - **Method**: `constructor(val value: String) -> Unit`
  - **Method**: `create(value: String) -> : Either<ValidationError`

### File: ADReference.kt
- **Package**: `org.blackerp.domain.ad.reference`
- **Class**: `ADReference`
  - **Method**: `create(params: CreateReferenceParams) -> : Either<ReferenceError`

- **Class**: `ValidationRule`
  - **Method**: `create(params: CreateReferenceParams) -> : Either<ReferenceError`

- **Class**: `Table`
  - **Method**: `create(params: CreateReferenceParams) -> : Either<ReferenceError`

- **Class**: `Custom`
  - **Method**: `create(params: CreateReferenceParams) -> : Either<ReferenceError`

- **Class**: `CreateReferenceParams`
  - **Method**: `create(params: CreateReferenceParams) -> : Either<ReferenceError`

- **Class**: `ReferenceError`
  - **Method**: `create(params: CreateReferenceParams) -> : Either<ReferenceError`

- **Class**: `ValidationFailed`
  - **Method**: `create(params: CreateReferenceParams) -> : Either<ReferenceError`

- **Class**: `DuplicateReference`
  - **Method**: `create(params: CreateReferenceParams) -> : Either<ReferenceError`

- **Class**: `ReferenceNotFound`
  - **Method**: `create(params: CreateReferenceParams) -> : Either<ReferenceError`

### File: ReferenceName.kt
- **Package**: `org.blackerp.domain.ad.reference.value`
- **Class**: `ReferenceName`
  - **Method**: `constructor(val value: String) -> Unit`
  - **Method**: `create(value: String) -> : Either<ValidationError`

### File: EventMetadata.kt
- **Package**: `org.blackerp.domain.event`
- **Class**: `EventMetadata`

### File: TableCreated.kt
- **Package**: `org.blackerp.domain.event`
- **Class**: `TableCreated`

### File: DomainError.kt
- **Package**: `org.blackerp.domain.error`
- **Class**: `ValidationError`

- **Class**: `NotFoundError`

- **Class**: `SecurityError`

- **Class**: `CacheError`

- **Class**: `QueryError`

- **Class**: `SystemError`

### File: TenantFilter.kt
- **Package**: `org.blackerp.domain.tenant`
- **Class**: `TenantFilter`
  - **Method**: `create(tenantId: String?) -> : Either<ValidationError`

### File: DataType.kt
- **Package**: `org.blackerp.domain.values`
- **Class**: `DataType`
  - **Method**: `fromString(value: String) -> : DataType`

### File: Precision.kt
- **Package**: `org.blackerp.domain.values`
- **Class**: `Precision`
  - **Method**: `constructor(val value: Int) -> Unit`
  - **Method**: `create(value: Int) -> : Either<ValidationError`

### File: AccessLevel.kt
- **Package**: `org.blackerp.domain.values`
- **Class**: `AccessLevel`
  - **Method**: `fromString(value: String) -> : AccessLevel`

### File: Description.kt
- **Package**: `org.blackerp.domain.values`
- **Class**: `Description`
  - **Method**: `constructor(val value: String) -> Unit`
  - **Method**: `create(value: String) -> : Either<ValidationError`

### File: Amount.kt
- **Package**: `org.blackerp.domain.values`
- **Class**: `Amount`
  - **Method**: `constructor(val value: BigDecimal) -> Unit`
  - **Method**: `create(value: BigDecimal) -> : Either<ValidationError`

### File: ColumnName.kt
- **Package**: `org.blackerp.domain.values`
- **Class**: `ColumnName`
  - **Method**: `constructor(val value: String) -> Unit`
  - **Method**: `create(value: String) -> : Either<ValidationError`

### File: Scale.kt
- **Package**: `org.blackerp.domain.values`
- **Class**: `Scale`
  - **Method**: `constructor(val value: Int) -> Unit`
  - **Method**: `create(value: Int) -> : Either<ValidationError`

### File: Length.kt
- **Package**: `org.blackerp.domain.values`
- **Class**: `Length`
  - **Method**: `constructor(val value: Int) -> Unit`
  - **Method**: `create(value: Int) -> : Either<ValidationError`

### File: EventMetadata.kt
- **Package**: `org.blackerp.domain.values`
- **Class**: `EventMetadata`

### File: TableName.kt
- **Package**: `org.blackerp.domain.values`
- **Class**: `TableName`
  - **Method**: `constructor(val value: String) -> Unit`
  - **Method**: `create(value: String) -> : Either<ValidationError`

### File: Currency.kt
- **Package**: `org.blackerp.domain.values`
- **Class**: `Currency`
  - **Method**: `constructor(val code: String) -> Unit`
  - **Method**: `create(code: String) -> : Either<ValidationError`

### File: DisplayName.kt
- **Package**: `org.blackerp.domain.values`
- **Class**: `DisplayName`
  - **Method**: `constructor(val value: String) -> Unit`
  - **Method**: `create(value: String) -> : Either<ValidationError`

### File: Transaction.kt
- **Package**: `org.blackerp.domain.transaction`
- **Class**: `Transaction`
  - **Method**: `create(params: CreateTransactionParams) -> : Either<TransactionError`

- **Class**: `CreateTransactionParams`
  - **Method**: `create(params: CreateTransactionParams) -> : Either<TransactionError`

### File: TransactionError.kt
- **Package**: `org.blackerp.domain.transaction`
- **Class**: `TransactionError`

- **Class**: `ValidationFailed`

- **Class**: `InvalidAmount`

- **Class**: `InvalidCurrency`

### File: QueryResult.kt
- **Package**: `org.blackerp.domain.query`
- **Class**: `QueryResult`
  - **Method**: `execute(criteria: QueryCriteria, page: Int, pageSize: Int) -> : Either<DomainError`

### File: QueryCriteria.kt
- **Package**: `org.blackerp.domain.query`
- **Class**: `Equals`

- **Class**: `Like`

- **Class**: `Between`

- **Class**: `In`

- **Class**: `And`

- **Class**: `Or`

### File: ADTable.kt
- **Package**: `org.blackerp.domain.table`
- **Class**: `ADTable`
  - **Method**: `create(params: CreateTableParams) -> : Either<TableError`

- **Class**: `CreateTableParams`
  - **Method**: `create(params: CreateTableParams) -> : Either<TableError`

### File: CreateColumnParams.kt
- **Package**: `org.blackerp.domain.table`
- **Class**: `CreateColumnParams`

### File: TenantAwareTable.kt
- **Package**: `org.blackerp.domain.table`
- **Class**: `TenantAwareTable`
  - **Method**: `from(table: ADTable, tenantId: UUID) -> : TenantAwareTable`

### File: TableCreated.kt
- **Package**: `org.blackerp.domain.table`
- **Class**: `TableCreated`

### File: TableError.kt
- **Package**: `org.blackerp.domain.table`
- **Class**: `TableError`

- **Class**: `ValidationFailed`

- **Class**: `StorageError`

- **Class**: `DuplicateTable`

- **Class**: `NotFound`

- **Class**: `ConcurrentModification`

### File: TableId.kt
- **Package**: `org.blackerp.domain.table`
- **Class**: `TableId`

### File: ColumnDefinition.kt
- **Package**: `org.blackerp.domain.table`
- **Class**: `ColumnDefinition`
  - **Method**: `create(params: CreateColumnParams) -> : Either<ColumnError`

- **Class**: `ValidationFailed`
  - **Method**: `create(params: CreateColumnParams) -> : Either<ColumnError`

- **Class**: `NotFound`
  - **Method**: `create(params: CreateColumnParams) -> : Either<ColumnError`

### File: UniqueConstraint.kt
- **Package**: `org.blackerp.domain.table.constraint`
- **Class**: `UniqueConstraint`
  - **Method**: `validate(table: TableDefinition) -> : Either<ValidationError`

### File: NotNullConstraint.kt
- **Package**: `org.blackerp.domain.table.constraint`
- **Class**: `NotNullConstraint`
  - **Method**: `validate(table: TableDefinition) -> : Either<ValidationError`

### File: ReferenceConstraint.kt
- **Package**: `org.blackerp.domain.table.constraint`
- **Class**: `ReferenceConstraint`
  - **Method**: `validate(table: TableDefinition) -> : Either<ValidationError`
  - **Method**: `toTableDefinition(table: ADTable) -> : TableDefinition`

### File: TableDefinition.kt
- **Package**: `org.blackerp.domain.table.definition`
- **Class**: `TableDefinition`
  - **Method**: `create(params: CreateTableParams) -> : Either<TableError`
  - **Method**: `validate() -> : Either<TableError`

- **Class**: `CreateTableParams`
  - **Method**: `create(params: CreateTableParams) -> : Either<TableError`
  - **Method**: `validate() -> : Either<TableError`

### File: CreateRelationshipParams.kt
- **Package**: `org.blackerp.domain.table.relationship`
- **Class**: `CreateRelationshipParams`

### File: TableRelationship.kt
- **Package**: `org.blackerp.domain.table.relationship`
- **Class**: `TableRelationship`
  - **Method**: `create(params: CreateRelationshipParams) -> : Either<TableError`
  - **Method**: `validate() -> : Either<TableError`

### File: TableRelationshipError.kt
- **Package**: `org.blackerp.domain.table.relationship`
- **Class**: `ValidationFailed`

- **Class**: `DuplicateRelationship`

- **Class**: `InvalidRelationType`

- **Class**: `CircularReference`

### File: RelationshipEvent.kt
- **Package**: `org.blackerp.domain.table.relationship.event`
- **Class**: `RelationshipCreated`

- **Class**: `RelationshipDeleted`

### File: IndexConstraint.kt
- **Package**: `org.blackerp.domain.table.relationship.constraint`
- **Class**: `IndexConstraint`
  - **Method**: `validate(relationship: TableRelationship) -> : Either<ValidationError`

### File: RelationshipName.kt
- **Package**: `org.blackerp.domain.table.relationship.value`
- **Class**: `RelationshipName`
  - **Method**: `constructor(val value: String) -> Unit`
  - **Method**: `create(value: String) -> : Either<ValidationError`

### File: RelationType.kt
- **Package**: `org.blackerp.domain.table.relationship.value`
- **Class**: `RelationType`
  - **Method**: `fromString(value: String) -> : RelationType`

### File: DeleteRule.kt
- **Package**: `org.blackerp.domain.table.relationship.value`
- **Class**: `DeleteRule`
  - **Method**: `fromString(value: String) -> : DeleteRule`

### File: UpdateRule.kt
- **Package**: `org.blackerp.domain.table.relationship.value`
- **Class**: `UpdateRule`
  - **Method**: `fromString(value: String) -> : UpdateRule`

### File: SecurityContext.kt
- **Package**: `org.blackerp.domain.security`
- **Class**: `User`
  - **Method**: `getCurrentUser() -> : Either<DomainError`
  - **Method**: `hasPermission(permission: Permission) -> : Either<DomainError`
  - **Method**: `authenticate(credentials: Credentials) -> : Either<DomainError`

- **Class**: `Role`
  - **Method**: `getCurrentUser() -> : Either<DomainError`
  - **Method**: `hasPermission(permission: Permission) -> : Either<DomainError`
  - **Method**: `authenticate(credentials: Credentials) -> : Either<DomainError`

- **Class**: `Permission`
  - **Method**: `getCurrentUser() -> : Either<DomainError`
  - **Method**: `hasPermission(permission: Permission) -> : Either<DomainError`
  - **Method**: `authenticate(credentials: Credentials) -> : Either<DomainError`

- **Class**: `Basic`
  - **Method**: `getCurrentUser() -> : Either<DomainError`
  - **Method**: `hasPermission(permission: Permission) -> : Either<DomainError`
  - **Method**: `authenticate(credentials: Credentials) -> : Either<DomainError`

- **Class**: `Token`
  - **Method**: `getCurrentUser() -> : Either<DomainError`
  - **Method**: `hasPermission(permission: Permission) -> : Either<DomainError`
  - **Method**: `authenticate(credentials: Credentials) -> : Either<DomainError`

### File: WebConfig.kt
- **Package**: `org.blackerp.config`
- **Class**: `WebConfig`
  - **Method**: `addCorsMappings(registry: CorsRegistry) -> Unit`

### File: EventConfig.kt
- **Package**: `org.blackerp.config`
- **Class**: `EventConfig`
  - **Method**: `eventPublisher() -> : EventPublisher`

### File: TableMapper.kt
- **Package**: `org.blackerp.api.mappers`
- **Class**: `TableMapper`
  - **Method**: `toCommand(request: CreateTableRequest) -> : CreateTableCommand`
  - **Method**: `toColumnCommand(request: CreateColumnRequest) -> : CreateColumnCommand`
  - **Method**: `toResponse(table: ADTable) -> : TableResponse`

### File: CreateTableRequest.kt
- **Package**: `org.blackerp.api.dto.request`
- **Class**: `CreateTableRequest`

- **Class**: `CreateColumnRequest`

### File: TableResponses.kt
- **Package**: `org.blackerp.api.dto.response`
- **Class**: `TableResponse`

- **Class**: `TablesResponse`

### File: ErrorHandler.kt
- **Package**: `org.blackerp.api.error`
- **Class**: `ErrorHandler`
  - **Method**: `handleDomainException(ex: DomainException) -> : ResponseEntity<ErrorResponse>`
  - **Method**: `handleValidationExceptions(ex: MethodArgumentNotValidException) -> : ResponseEntity<ErrorResponse>`

- **Class**: `ErrorResponse`
  - **Method**: `handleDomainException(ex: DomainException) -> : ResponseEntity<ErrorResponse>`
  - **Method**: `handleValidationExceptions(ex: MethodArgumentNotValidException) -> : ResponseEntity<ErrorResponse>`

### File: TableController.kt
- **Package**: `org.blackerp.api.controllers`
- **Class**: `TableController`
  - **Method**: `getTables() -> : ResponseEntity<TablesResponse>`
  - **Method**: `createTable(@Valid @RequestBody request: CreateTableRequest) -> : ResponseEntity<TableResponse>`

### File: CreateTableUseCase.kt
- **Package**: `org.blackerp.application.table`
- **Class**: `CreateTableUseCase`
  - **Method**: `execute(command: CreateTableCommand) -> : Either<TableError`

### File: CreateTableCommand.kt
- **Package**: `org.blackerp.application.table`
- **Class**: `CreateTableCommand`

- **Class**: `CreateColumnCommand`

### File: ValidationError.kt
- **Package**: `org.blackerp.shared`
- **Class**: `ValidationError`

- **Class**: `InvalidFormat`

- **Class**: `Required`

- **Class**: `InvalidLength`

- **Class**: `InvalidValue`

### File: ReferenceValidation.kt
- **Package**: `org.blackerp.shared`
- **Class**: `ColumnNotFound`

- **Class**: `ReferenceTableNotFound`

- **Class**: `ReferenceColumnNotFound`

- **Class**: `IncompatibleTypes`

### File: InMemoryCacheServiceTest.kt
- **Package**: `org.blackerp.infrastructure.cache`
- **Class**: `InMemoryCacheServiceTest`

### File: TestDatabaseConfig.kt
- **Package**: `org.blackerp.infrastructure.persistence`
- **Class**: `TestDatabaseConfig`
  - **Method**: `dataSource() -> : DataSource`
  - **Method**: `jdbcTemplate(dataSource: DataSource) -> : JdbcTemplate`

### File: SimpleTest.kt
- **Package**: `org.blackerp.infrastructure.persistence`
- **Class**: `SimpleTest`

### File: InMemoryTabOperations.kt
- **Package**: `org.blackerp.infrastructure.persistence.store`
- **Class**: `InMemoryTabOperations`
  - **Method**: `save(tab: ADTab) -> : Either<TabError`
  - **Method**: `findById(id: UUID) -> : Either<TabError`
  - **Method**: `findByTable(tableName: TableName) -> : Either<TabError`
  - **Method**: `delete(id: UUID) -> : Either<TabError`

### File: InMemoryRelationshipOperations.kt
- **Package**: `org.blackerp.infrastructure.persistence.store`
- **Class**: `InMemoryRelationshipOperations`
  - **Method**: `save(relationship: TableRelationship) -> : Either<TableError`
  - **Method**: `findById(id: UUID) -> : Either<TableError`
  - **Method**: `findByTable(tableName: TableName) -> : Either<TableError`
  - **Method**: `delete(id: UUID) -> : Either<TableError`

### File: InMemoryTableOperations.kt
- **Package**: `org.blackerp.infrastructure.persistence.store`
- **Class**: `InMemoryTableOperations`
  - **Method**: `findAll() -> : Either<TableError`
  - **Method**: `save(table: ADTable) -> : Either<TableError`
  - **Method**: `findById(id: UUID) -> : Either<TableError`
  - **Method**: `findByName(name: String) -> : Either<TableError`
  - **Method**: `delete(id: UUID) -> : Either<TableError`

### File: PostgresTabOperationsTest.kt
- **Package**: `org.blackerp.infrastructure.persistence.store`
- **Class**: `PostgresTabOperationsTest`

### File: TestPlugin.kt
- **Package**: `org.blackerp.plugin`
- **Class**: `TestPlugin`
  - **Method**: `initialize() -> : Either<PluginError`
  - **Method**: `registerExtensions(registry: ExtensionRegistry) -> : Either<PluginError`
  - **Method**: `shutdown() -> : Either<PluginError`

### File: InMemoryExtensionRegistry.kt
- **Package**: `org.blackerp.plugin`
- **Class**: `InMemoryExtensionRegistry`

### File: PluginIdTest.kt
- **Package**: `org.blackerp.plugin`
- **Class**: `PluginIdTest`

### File: VersionTest.kt
- **Package**: `org.blackerp.plugin`
- **Class**: `VersionTest`

### File: PluginTest.kt
- **Package**: `org.blackerp.plugin`
- **Class**: `PluginTest`

### File: PluginMetadataTest.kt
- **Package**: `org.blackerp.plugin`
- **Class**: `PluginMetadataTest`

### File: FileSystemPluginDiscoveryTest.kt
- **Package**: `org.blackerp.plugin.discovery`
- **Class**: `FileSystemPluginDiscoveryTest`

### File: DefaultPluginRegistryTest.kt
- **Package**: `org.blackerp.plugin.registry`
- **Class**: `DefaultPluginRegistryTest`

### File: EntityMetadataTest.kt
- **Package**: `org.blackerp.domain`
- **Class**: `EntityMetadataTest`

### File: ADModuleTest.kt
- **Package**: `org.blackerp.domain.ad`
- **Class**: `ADModuleTest`

### File: WindowNameTest.kt
- **Package**: `org.blackerp.domain.ad.window.value`
- **Class**: `WindowNameTest`

### File: ADTabTest.kt
- **Package**: `org.blackerp.domain.ad.tab`
- **Class**: `ADTabTest`

### File: TabNameTest.kt
- **Package**: `org.blackerp.domain.ad.tab.value`
- **Class**: `TabNameTest`

### File: ModuleNameTest.kt
- **Package**: `org.blackerp.domain.ad.value`
- **Class**: `ModuleNameTest`

### File: ADReferenceTest.kt
- **Package**: `org.blackerp.domain.ad.reference`
- **Class**: `ADReferenceTest`

### File: ReferenceNameTest.kt
- **Package**: `org.blackerp.domain.ad.reference.value`
- **Class**: `ReferenceNameTest`

### File: DomainErrorTest.kt
- **Package**: `org.blackerp.domain.error`
- **Class**: `DomainErrorTest`

### File: TenantAwareTableTest.kt
- **Package**: `org.blackerp.domain.tenant`
- **Class**: `TenantAwareTableTest`

### File: TenantContextTest.kt
- **Package**: `org.blackerp.domain.tenant`
- **Class**: `TenantContextTest`

### File: AmountTest.kt
- **Package**: `org.blackerp.domain.values`
- **Class**: `AmountTest`

### File: DisplayNameTest.kt
- **Package**: `org.blackerp.domain.values`
- **Class**: `DisplayNameTest`

### File: TableNameTest.kt
- **Package**: `org.blackerp.domain.values`
- **Class**: `TableNameTest`

### File: CurrencyTest.kt
- **Package**: `org.blackerp.domain.values`
- **Class**: `CurrencyTest`

### File: DataTypeTest.kt
- **Package**: `org.blackerp.domain.values`
- **Class**: `DataTypeTest`

### File: TransactionTest.kt
- **Package**: `org.blackerp.domain.transaction`
- **Class**: `TransactionTest`

### File: QueryCriteriaTest.kt
- **Package**: `org.blackerp.domain.query`
- **Class**: `QueryCriteriaTest`

### File: TableOperationsTest.kt
- **Package**: `org.blackerp.domain.table`
- **Class**: `TableOperationsTest`

### File: TableExtensionPointTest.kt
- **Package**: `org.blackerp.domain.table.extension`
- **Class**: `TableExtensionPointTest`
  - **Method**: `beforeCreate(table: ADTable) -> Unit`
  - **Method**: `afterCreate(table: ADTable) -> Unit`
  - **Method**: `beforeUpdate(table: ADTable) -> Unit`
  - **Method**: `afterUpdate(table: ADTable) -> Unit`
  - **Method**: `beforeDelete(table: ADTable) -> Unit`
  - **Method**: `afterDelete(table: ADTable) -> Unit`

### File: ReferenceConstraintTest.kt
- **Package**: `org.blackerp.domain.table.constraint`
- **Class**: `ReferenceConstraintTest`

### File: UniqueConstraintTest.kt
- **Package**: `org.blackerp.domain.table.constraint`
- **Class**: `UniqueConstraintTest`

### File: NotNullConstraintTest.kt
- **Package**: `org.blackerp.domain.table.constraint`
- **Class**: `NotNullConstraintTest`

### File: TableDefinitionTest.kt
- **Package**: `org.blackerp.domain.table.definition`
- **Class**: `TableDefinitionTest`

### File: TableRelationshipTest.kt
- **Package**: `org.blackerp.domain.table.relationship`
- **Class**: `TableRelationshipTest`

### File: RelationshipOperationsTest.kt
- **Package**: `org.blackerp.domain.table.relationship`
- **Class**: `RelationshipOperationsTest`

### File: UnifiedTestConfig.kt
- **Package**: `org.blackerp.config`
- **Class**: `UnifiedTestConfig`
  - **Method**: `dataSource() -> : DataSource`
  - **Method**: `jdbcTemplate(dataSource: DataSource) -> : JdbcTemplate`
  - **Method**: `testRestTemplate() -> : TestRestTemplate`
  - **Method**: `eventPublisher() -> : EventPublisher`
  - **Method**: `tableOperations(jdbcTemplate: JdbcTemplate) -> : TableOperations`
  - **Method**: `postgresTabOperations(jdbcTemplate: JdbcTemplate, tableOperations: TableOperations) -> : PostgresTabOperations`
  - **Method**: `createTableUseCase(tableOperations: TableOperations, eventPublisher: EventPublisher) -> : CreateTableUseCase`
  - **Method**: `flywayMigrationStrategy() -> : FlywayMigrationStrategy`

### File: TestConfig.kt
- **Package**: `org.blackerp.config`
- **Class**: `TestConfig`
  - **Method**: `dataSource() -> : DataSource`
  - **Method**: `jdbcTemplate(dataSource: DataSource) -> : JdbcTemplate`
  - **Method**: `objectMapper() -> : ObjectMapper`
  - **Method**: `tableMapper() -> : TableMapper`
  - **Method**: `eventPublisher() -> : EventPublisher`
  - **Method**: `tableOperations() -> : TableOperations`
  - **Method**: `createTableUseCase(tableOperations: TableOperations, eventPublisher: EventPublisher) -> : CreateTableUseCase`

### File: KotestProjectConfig.kt
- **Package**: `org.blackerp.config`
- **Class**: `KotestProjectConfig`
  - **Method**: `extensions() -> Unit`

### File: TestEventConfig.kt
- **Package**: `org.blackerp.config`
- **Class**: `TestEventConfig`
  - **Method**: `testEventPublisher() -> : EventPublisher`

### File: TableControllerTest.kt
- **Package**: `org.blackerp.api.controllers`
- **Class**: `TableControllerTest`

### File: CreateTableUseCaseTest.kt
- **Package**: `org.blackerp.application.table`
- **Class**: `CreateTableUseCaseTest`

### File: TimeBasedIdTest.kt
- **Package**: `org.blackerp.shared`
- **Class**: `TimeBasedIdTest`

### File: IntegrationTestConfig.kt
- **Package**: `org.blackerp.integration`
- **Class**: `IntegrationTestConfig`
  - **Method**: `dataSource() -> : DataSource`
  - **Method**: `jdbcTemplate(dataSource: DataSource) -> : JdbcTemplate`
  - **Method**: `testRestTemplate() -> Unit`
  - **Method**: `eventPublisher() -> : EventPublisher`
  - **Method**: `tableOperations(jdbcTemplate: JdbcTemplate) -> : PostgresTableOperations`
  - **Method**: `createTableUseCase(tableOperations: PostgresTableOperations, eventPublisher: EventPublisher) -> : CreateTableUseCase`

### File: PluginLifecycleIntegrationTest.kt
- **Package**: `org.blackerp.integration.plugin`
- **Class**: `PluginLifecycleIntegrationTest`

### File: TableApiIntegrationTest.kt
- **Package**: `org.blackerp.integration.api`
- **Class**: `TableApiIntegrationTest`

### File: TableRepositoryIntegrationTest.kt
- **Package**: `org.blackerp.integration.db`
- **Class**: `TableRepositoryIntegrationTest`

