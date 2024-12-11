export interface SecurityRole {
    id: string;
    name: string;
    displayName: string;
    description?: string;
    accessLevel: string;
    isActive: boolean;
}

export interface SecurityPermission {
    entityType: string;
    entityId: string;
    permissionType: string;
    validationRule?: string;
}

export interface SecurityAuditLog {
    timestamp: string;
    username: string;
    action: string;
    entityType: string;
    entityId: string;
    result: boolean;
}

export interface CreateRoleRequest {
    name: string;
    displayName: string;
    description?: string;
    accessLevel: string;
}
