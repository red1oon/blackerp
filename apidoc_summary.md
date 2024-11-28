### File: BlackErpApplication.kt
- **Package**: `org.blackerp`
- **Class**: `BlackErpApplication`
  - **Method**: `main(Array<String>: `args:`) -> Unit`

### File: DefaultEventPublisher.kt
- **Package**: `org.blackerp.infrastructure.event`
- **Class**: `DefaultEventPublisher`
  - **Method**: `publish(DomainEvent: `event:`) -> : Either<TableError`

### File: InMemoryCacheService.kt
- **Package**: `org.blackerp.infrastructure.cache`
- **Class**: `InMemoryCacheService`
  - **Method**: `delete(String: `key:`) -> : Either<DomainError`
  - **Method**: `clear() -> : Either<DomainError`

- **Class**: `CacheEntry`
  - **Method**: `delete(String: `key:`) -> : Either<DomainError`
  - **Method**: `clear() -> : Either<DomainError`

### File: PostgresTableOperations.kt
- **Package**: `org.blackerp.infrastructure.persistence.store`
- **Class**: `PostgresTableOperations`
  - **Method**: `findAll() -> : Either<TableError`
  - **Method**: `save(ADTable: `table:`) -> : Either<TableError`
  - **Method**: `findById(UUID: `id:`) -> : Either<TableError`
  - **Method**: `findByName(String: `name:`) -> : Either<TableError`
  - **Method**: `delete(UUID: `id:`) -> : Either<TableError`
  - **Method**: `mapRow(ResultSet: `rs:`, Int: `rowNum:`) -> : ADTable`

- **Class**: `TableRowMapper`
  - **Method**: `findAll() -> : Either<TableError`
  - **Method**: `save(ADTable: `table:`) -> : Either<TableError`
  - **Method**: `findById(UUID: `id:`) -> : Either<TableError`
  - **Method**: `findByName(String: `name:`) -> : Either<TableError`
  - **Method**: `delete(UUID: `id:`) -> : Either<TableError`
  - **Method**: `mapRow(ResultSet: `rs:`, Int: `rowNum:`) -> : ADTable`

### File: PostgresRelationshipOperations.kt
- **Package**: `org.blackerp.infrastructure.persistence.store`
- **Class**: `PostgresRelationshipOperations`
  - **Method**: `save(TableRelationship: `relationship:`) -> : Either<TableError`
  - **Method**: `findById(UUID: `id:`) -> : Either<TableError`
  - **Method**: `findByTable(TableName: `tableName:`) -> : Either<TableError`
  - **Method**: `delete(UUID: `id:`) -> : Either<TableError`
  - **Method**: `mapRow(ResultSet: `rs:`, Int: `rowNum:`) -> : TableRelationship`

- **Class**: `RelationshipRowMapper`
  - **Method**: `save(TableRelationship: `relationship:`) -> : Either<TableError`
  - **Method**: `findById(UUID: `id:`) -> : Either<TableError`
  - **Method**: `findByTable(TableName: `tableName:`) -> : Either<TableError`
  - **Method**: `delete(UUID: `id:`) -> : Either<TableError`
  - **Method**: `mapRow(ResultSet: `rs:`, Int: `rowNum:`) -> : TableRelationship`

### File: PostgresTabOperations.kt
- **Package**: `org.blackerp.infrastructure.persistence.store`
- **Class**: `PostgresTabOperations`
  - **Method**: `debug(String: `msg:`) -> Unit`
  - **Method**: `save(ADTab: `tab:`) -> : Either<TabError`
  - **Method**: `findById(UUID: `id:`) -> : Either<TabError`
  - **Method**: `findByTable(TableName: `tableName:`) -> : Either<TabError`
  - **Method**: `delete(UUID: `id:`) -> : Either<TabError`
  - **Method**: `mapRow(ResultSet: `rs:`, Int: `rowNum:`) -> : ADTab`

- **Class**: `TabRowMapper`
  - **Method**: `debug(String: `msg:`) -> Unit`
  - **Method**: `save(ADTab: `tab:`) -> : Either<TabError`
  - **Method**: `findById(UUID: `id:`) -> : Either<TabError`
  - **Method**: `findByTable(TableName: `tableName:`) -> : Either<TabError`
  - **Method**: `delete(UUID: `id:`) -> : Either<TabError`
  - **Method**: `mapRow(ResultSet: `rs:`, Int: `rowNum:`) -> : ADTab`

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
  - **Method**: `create(String: `value:`) -> : Either<ValidationError`

### File: Version.kt
- **Package**: `org.blackerp.plugin`
- **Class**: `Version`
  - **Method**: `compareTo(Version: `other:`) -> : Int`
  - **Method**: `create(String: `version:`) -> : Either<ValidationError`
  - **Method**: `toString() -> : String`

### File: PluginMetadata.kt
- **Package**: `org.blackerp.plugin`
- **Class**: `PluginMetadata`
  - **Method**: `create(PluginId: `id:`, Version: `version:`, String: `name:`, String: `description:`, String: `vendor:`) -> : Either<ValidationError`

### File: FileSystemPluginDiscovery.kt
- **Package**: `org.blackerp.plugin.discovery`
- **Class**: `FileSystemPluginDiscovery`
  - **Method**: `discoverPlugins(Path: `directory:`) -> : Either<PluginError`
  - **Method**: `loadPlugin(Path: `jarPath:`) -> : Either<PluginError`

### File: DefaultPluginRegistry.kt
- **Package**: `org.blackerp.plugin.registry`
- **Class**: `DefaultPluginRegistry`
  - **Method**: `register(Plugin: `plugin:`) -> : Either<PluginError`
  - **Method**: `unregister(PluginId: `pluginId:`) -> : Either<PluginError`
  - **Method**: `getPlugin(PluginId: `pluginId:`) -> : Either<PluginError`
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
  - **Method**: `create(CreateModuleParams: `params:`) -> : Either<ValidationError`

- **Class**: `CreateModuleParams`
  - **Method**: `create(CreateModuleParams: `params:`) -> : Either<ValidationError`

### File: WindowName.kt
- **Package**: `org.blackerp.domain.ad.window.value`
- **Class**: `WindowName`
  - **Method**: `create(String: `value:`) -> : Either<ValidationError`

### File: CreateTabParams.kt
- **Package**: `org.blackerp.domain.ad.tab`
- **Class**: `CreateTabParams`

### File: OrderBySpec.kt
- **Package**: `org.blackerp.domain.ad.tab`
- **Class**: `OrderBySpec`
  - **Method**: `fromString(String: `value:`) -> : SortDirection`

- **Class**: `SortDirection`
  - **Method**: `fromString(String: `value:`) -> : SortDirection`

### File: TabError.kt
- **Package**: `org.blackerp.domain.ad.tab`
- **Class**: `TabError`

- **Class**: `ValidationFailed`

- **Class**: `NotFound`

- **Class**: `DuplicateTab`

### File: ADTab.kt
- **Package**: `org.blackerp.domain.ad.tab`
- **Class**: `ADTab`
  - **Method**: `create(CreateTabParams: `params:`) -> : Either<TabError`

### File: TabName.kt
- **Package**: `org.blackerp.domain.ad.tab.value`
- **Class**: `TabName`
  - **Method**: `create(String: `value:`) -> : Either<ValidationError`

### File: ModuleName.kt
- **Package**: `org.blackerp.domain.ad.value`
- **Class**: `ModuleName`
  - **Method**: `create(String: `value:`) -> : Either<ValidationError`

### File: ADReference.kt
- **Package**: `org.blackerp.domain.ad.reference`
- **Class**: `ADReference`
  - **Method**: `create(CreateReferenceParams: `params:`) -> : Either<ReferenceError`

- **Class**: `ValidationRule`
  - **Method**: `create(CreateReferenceParams: `params:`) -> : Either<ReferenceError`

- **Class**: `Table`
  - **Method**: `create(CreateReferenceParams: `params:`) -> : Either<ReferenceError`

- **Class**: `Custom`
  - **Method**: `create(CreateReferenceParams: `params:`) -> : Either<ReferenceError`

- **Class**: `CreateReferenceParams`
  - **Method**: `create(CreateReferenceParams: `params:`) -> : Either<ReferenceError`

- **Class**: `ReferenceError`
  - **Method**: `create(CreateReferenceParams: `params:`) -> : Either<ReferenceError`

- **Class**: `ValidationFailed`
  - **Method**: `create(CreateReferenceParams: `params:`) -> : Either<ReferenceError`

- **Class**: `DuplicateReference`
  - **Method**: `create(CreateReferenceParams: `params:`) -> : Either<ReferenceError`

- **Class**: `ReferenceNotFound`
  - **Method**: `create(CreateReferenceParams: `params:`) -> : Either<ReferenceError`

### File: ReferenceName.kt
- **Package**: `org.blackerp.domain.ad.reference.value`
- **Class**: `ReferenceName`
  - **Method**: `create(String: `value:`) -> : Either<ValidationError`

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
  - **Method**: `create(String?: `tenantId:`) -> : Either<ValidationError`

### File: DataType.kt
- **Package**: `org.blackerp.domain.values`
- **Class**: `DataType`
  - **Method**: `fromString(String: `value:`) -> : DataType`

### File: Precision.kt
- **Package**: `org.blackerp.domain.values`
- **Class**: `Precision`
  - **Method**: `create(Int: `value:`) -> : Either<ValidationError`

### File: AccessLevel.kt
- **Package**: `org.blackerp.domain.values`
- **Class**: `AccessLevel`
  - **Method**: `fromString(String: `value:`) -> : AccessLevel`

### File: Description.kt
- **Package**: `org.blackerp.domain.values`
- **Class**: `Description`
  - **Method**: `create(String: `value:`) -> : Either<ValidationError`

### File: Amount.kt
- **Package**: `org.blackerp.domain.values`
- **Class**: `Amount`
  - **Method**: `create(BigDecimal: `value:`) -> : Either<ValidationError`

### File: ColumnName.kt
- **Package**: `org.blackerp.domain.values`
- **Class**: `ColumnName`
  - **Method**: `create(String: `value:`) -> : Either<ValidationError`

### File: Scale.kt
- **Package**: `org.blackerp.domain.values`
- **Class**: `Scale`
  - **Method**: `create(Int: `value:`) -> : Either<ValidationError`

### File: Length.kt
- **Package**: `org.blackerp.domain.values`
- **Class**: `Length`
  - **Method**: `create(Int: `value:`) -> : Either<ValidationError`

### File: EventMetadata.kt
- **Package**: `org.blackerp.domain.values`
- **Class**: `EventMetadata`

### File: TableName.kt
- **Package**: `org.blackerp.domain.values`
- **Class**: `TableName`
  - **Method**: `create(String: `value:`) -> : Either<ValidationError`

### File: Currency.kt
- **Package**: `org.blackerp.domain.values`
- **Class**: `Currency`
  - **Method**: `create(String: `code:`) -> : Either<ValidationError`

### File: DisplayName.kt
- **Package**: `org.blackerp.domain.values`
- **Class**: `DisplayName`
  - **Method**: `create(String: `value:`) -> : Either<ValidationError`

### File: Transaction.kt
- **Package**: `org.blackerp.domain.transaction`
- **Class**: `Transaction`
  - **Method**: `create(CreateTransactionParams: `params:`) -> : Either<TransactionError`

- **Class**: `CreateTransactionParams`
  - **Method**: `create(CreateTransactionParams: `params:`) -> : Either<TransactionError`

### File: TransactionError.kt
- **Package**: `org.blackerp.domain.transaction`
- **Class**: `TransactionError`

- **Class**: `ValidationFailed`

- **Class**: `InvalidAmount`

- **Class**: `InvalidCurrency`

### File: QueryResult.kt
- **Package**: `org.blackerp.domain.query`
- **Class**: `QueryResult`
  - **Method**: `execute(QueryCriteria: `criteria:`, Int: `page:`, Int: `pageSize:`) -> : Either<DomainError`

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
  - **Method**: `create(CreateTableParams: `params:`) -> : Either<TableError`

- **Class**: `CreateTableParams`
  - **Method**: `create(CreateTableParams: `params:`) -> : Either<TableError`

### File: CreateColumnParams.kt
- **Package**: `org.blackerp.domain.table`
- **Class**: `CreateColumnParams`

### File: TenantAwareTable.kt
- **Package**: `org.blackerp.domain.table`
- **Class**: `TenantAwareTable`
  - **Method**: `from(ADTable: `table:`, UUID: `tenantId:`) -> : TenantAwareTable`

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
  - **Method**: `create(CreateColumnParams: `params:`) -> : Either<ColumnError`

- **Class**: `ValidationFailed`
  - **Method**: `create(CreateColumnParams: `params:`) -> : Either<ColumnError`

- **Class**: `NotFound`
  - **Method**: `create(CreateColumnParams: `params:`) -> : Either<ColumnError`

### File: UniqueConstraint.kt
- **Package**: `org.blackerp.domain.table.constraint`
- **Class**: `UniqueConstraint`
  - **Method**: `validate(TableDefinition: `table:`) -> : Either<ValidationError`

### File: NotNullConstraint.kt
- **Package**: `org.blackerp.domain.table.constraint`
- **Class**: `NotNullConstraint`
  - **Method**: `validate(TableDefinition: `table:`) -> : Either<ValidationError`

### File: ReferenceConstraint.kt
- **Package**: `org.blackerp.domain.table.constraint`
- **Class**: `ReferenceConstraint`
  - **Method**: `validate(TableDefinition: `table:`) -> : Either<ValidationError`
  - **Method**: `toTableDefinition(ADTable: `table:`) -> : TableDefinition`

### File: TableDefinition.kt
- **Package**: `org.blackerp.domain.table.definition`
- **Class**: `TableDefinition`
  - **Method**: `create(CreateTableParams: `params:`) -> : Either<TableError`
  - **Method**: `validate() -> : Either<TableError`

- **Class**: `CreateTableParams`
  - **Method**: `create(CreateTableParams: `params:`) -> : Either<TableError`
  - **Method**: `validate() -> : Either<TableError`

### File: CreateRelationshipParams.kt
- **Package**: `org.blackerp.domain.table.relationship`
- **Class**: `CreateRelationshipParams`

### File: TableRelationship.kt
- **Package**: `org.blackerp.domain.table.relationship`
- **Class**: `TableRelationship`
  - **Method**: `create(CreateRelationshipParams: `params:`) -> : Either<TableError`
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
  - **Method**: `validate(TableRelationship: `relationship:`) -> : Either<ValidationError`

### File: RelationshipName.kt
- **Package**: `org.blackerp.domain.table.relationship.value`
- **Class**: `RelationshipName`
  - **Method**: `create(String: `value:`) -> : Either<ValidationError`

### File: RelationType.kt
- **Package**: `org.blackerp.domain.table.relationship.value`
- **Class**: `RelationType`
  - **Method**: `fromString(String: `value:`) -> : RelationType`

### File: DeleteRule.kt
- **Package**: `org.blackerp.domain.table.relationship.value`
- **Class**: `DeleteRule`
  - **Method**: `fromString(String: `value:`) -> : DeleteRule`

### File: UpdateRule.kt
- **Package**: `org.blackerp.domain.table.relationship.value`
- **Class**: `UpdateRule`
  - **Method**: `fromString(String: `value:`) -> : UpdateRule`

### File: SecurityContext.kt
- **Package**: `org.blackerp.domain.security`
- **Class**: `User`
  - **Method**: `getCurrentUser() -> : Either<DomainError`
  - **Method**: `hasPermission(Permission: `permission:`) -> : Either<DomainError`
  - **Method**: `authenticate(Credentials: `credentials:`) -> : Either<DomainError`

- **Class**: `Role`
  - **Method**: `getCurrentUser() -> : Either<DomainError`
  - **Method**: `hasPermission(Permission: `permission:`) -> : Either<DomainError`
  - **Method**: `authenticate(Credentials: `credentials:`) -> : Either<DomainError`

- **Class**: `Permission`
  - **Method**: `getCurrentUser() -> : Either<DomainError`
  - **Method**: `hasPermission(Permission: `permission:`) -> : Either<DomainError`
  - **Method**: `authenticate(Credentials: `credentials:`) -> : Either<DomainError`

- **Class**: `Basic`
  - **Method**: `getCurrentUser() -> : Either<DomainError`
  - **Method**: `hasPermission(Permission: `permission:`) -> : Either<DomainError`
  - **Method**: `authenticate(Credentials: `credentials:`) -> : Either<DomainError`

- **Class**: `Token`
  - **Method**: `getCurrentUser() -> : Either<DomainError`
  - **Method**: `hasPermission(Permission: `permission:`) -> : Either<DomainError`
  - **Method**: `authenticate(Credentials: `credentials:`) -> : Either<DomainError`

### File: WebConfig.kt
- **Package**: `org.blackerp.config`
- **Class**: `WebConfig`
  - **Method**: `addCorsMappings(CorsRegistry: `registry:`) -> Unit`

### File: EventConfig.kt
- **Package**: `org.blackerp.config`
- **Class**: `EventConfig`
  - **Method**: `eventPublisher() -> : EventPublisher`

### File: TableMapper.kt
- **Package**: `org.blackerp.api.mappers`
- **Class**: `TableMapper`
  - **Method**: `toCommand(CreateTableRequest: `request:`) -> : CreateTableCommand`
  - **Method**: `toColumnCommand(CreateColumnRequest: `request:`) -> : CreateColumnCommand`
  - **Method**: `toResponse(ADTable: `table:`) -> : TableResponse`

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
  - **Method**: `handleDomainException(DomainException: `ex:`) -> : ResponseEntity<ErrorResponse>`
  - **Method**: `handleValidationExceptions(MethodArgumentNotValidException: `ex:`) -> : ResponseEntity<ErrorResponse>`

- **Class**: `ErrorResponse`
  - **Method**: `handleDomainException(DomainException: `ex:`) -> : ResponseEntity<ErrorResponse>`
  - **Method**: `handleValidationExceptions(MethodArgumentNotValidException: `ex:`) -> : ResponseEntity<ErrorResponse>`

### File: TableController.kt
- **Package**: `org.blackerp.api.controllers`
- **Class**: `TableController`
  - **Method**: `getTables() -> : ResponseEntity<TablesResponse>`
  - **Method**: `createTable(CreateTableRequest: `@Valid @RequestBody request:`) -> : ResponseEntity<TableResponse>`

### File: CreateTableUseCase.kt
- **Package**: `org.blackerp.application.table`
- **Class**: `CreateTableUseCase`
  - **Method**: `execute(CreateTableCommand: `command:`) -> : Either<TableError`

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
  - **Method**: `jdbcTemplate(DataSource: `dataSource:`) -> : JdbcTemplate`

### File: SimpleTest.kt
- **Package**: `org.blackerp.infrastructure.persistence`
- **Class**: `SimpleTest`

### File: InMemoryTabOperations.kt
- **Package**: `org.blackerp.infrastructure.persistence.store`
- **Class**: `InMemoryTabOperations`
  - **Method**: `save(ADTab: `tab:`) -> : Either<TabError`
  - **Method**: `findById(UUID: `id:`) -> : Either<TabError`
  - **Method**: `findByTable(TableName: `tableName:`) -> : Either<TabError`
  - **Method**: `delete(UUID: `id:`) -> : Either<TabError`

### File: InMemoryRelationshipOperations.kt
- **Package**: `org.blackerp.infrastructure.persistence.store`
- **Class**: `InMemoryRelationshipOperations`
  - **Method**: `save(TableRelationship: `relationship:`) -> : Either<TableError`
  - **Method**: `findById(UUID: `id:`) -> : Either<TableError`
  - **Method**: `findByTable(TableName: `tableName:`) -> : Either<TableError`
  - **Method**: `delete(UUID: `id:`) -> : Either<TableError`

### File: InMemoryTableOperations.kt
- **Package**: `org.blackerp.infrastructure.persistence.store`
- **Class**: `InMemoryTableOperations`
  - **Method**: `findAll() -> : Either<TableError`
  - **Method**: `save(ADTable: `table:`) -> : Either<TableError`
  - **Method**: `findById(UUID: `id:`) -> : Either<TableError`
  - **Method**: `findByName(String: `name:`) -> : Either<TableError`
  - **Method**: `delete(UUID: `id:`) -> : Either<TableError`

### File: PostgresTabOperationsTest.kt
- **Package**: `org.blackerp.infrastructure.persistence.store`
- **Class**: `PostgresTabOperationsTest`

### File: TestPlugin.kt
- **Package**: `org.blackerp.plugin`
- **Class**: `TestPlugin`
  - **Method**: `initialize() -> : Either<PluginError`
  - **Method**: `registerExtensions(ExtensionRegistry: `registry:`) -> : Either<PluginError`
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
  - **Method**: `beforeCreate(ADTable: `table:`) -> Unit`
  - **Method**: `afterCreate(ADTable: `table:`) -> Unit`
  - **Method**: `beforeUpdate(ADTable: `table:`) -> Unit`
  - **Method**: `afterUpdate(ADTable: `table:`) -> Unit`
  - **Method**: `beforeDelete(ADTable: `table:`) -> Unit`
  - **Method**: `afterDelete(ADTable: `table:`) -> Unit`

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
  - **Method**: `jdbcTemplate(DataSource: `dataSource:`) -> : JdbcTemplate`
  - **Method**: `testRestTemplate() -> : TestRestTemplate`
  - **Method**: `eventPublisher() -> : EventPublisher`
  - **Method**: `tableOperations(JdbcTemplate: `jdbcTemplate:`) -> : TableOperations`
  - **Method**: `postgresTabOperations(JdbcTemplate: `jdbcTemplate:`, TableOperations: `tableOperations:`) -> : PostgresTabOperations`
  - **Method**: `createTableUseCase(TableOperations: `tableOperations:`, EventPublisher: `eventPublisher:`) -> : CreateTableUseCase`
  - **Method**: `flywayMigrationStrategy() -> : FlywayMigrationStrategy`

### File: TestConfig.kt
- **Package**: `org.blackerp.config`
- **Class**: `TestConfig`
  - **Method**: `dataSource() -> : DataSource`
  - **Method**: `jdbcTemplate(DataSource: `dataSource:`) -> : JdbcTemplate`
  - **Method**: `objectMapper() -> : ObjectMapper`
  - **Method**: `tableMapper() -> : TableMapper`
  - **Method**: `eventPublisher() -> : EventPublisher`
  - **Method**: `createTableUseCase(EventPublisher: `eventPublisher:`) -> : CreateTableUseCase`

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
  - **Method**: `debug(String: `msg:`) -> Unit`

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
  - **Method**: `jdbcTemplate(DataSource: `dataSource:`) -> : JdbcTemplate`
  - **Method**: `testRestTemplate() -> Unit`
  - **Method**: `eventPublisher() -> : EventPublisher`
  - **Method**: `tableOperations(JdbcTemplate: `jdbcTemplate:`) -> : PostgresTableOperations`
  - **Method**: `createTableUseCase(PostgresTableOperations: `tableOperations:`, EventPublisher: `eventPublisher:`) -> : CreateTableUseCase`

### File: PluginLifecycleIntegrationTest.kt
- **Package**: `org.blackerp.integration.plugin`
- **Class**: `PluginLifecycleIntegrationTest`

### File: TableApiIntegrationTest.kt
- **Package**: `org.blackerp.integration.api`
- **Class**: `TableApiIntegrationTest`

### File: TableRepositoryIntegrationTest.kt
- **Package**: `org.blackerp.integration.db`
- **Class**: `TableRepositoryIntegrationTest`

