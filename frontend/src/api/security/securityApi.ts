import { SecurityRole, CreateRoleRequest, SecurityAuditLog } from '../types/security/types';

export const securityApi = {
    async getRoles(): Promise<SecurityRole[]> {
        try {
            const response = await fetch(`/api/security/roles`);
            if (!response.ok) throw new Error('Failed to fetch roles');
            return response.json();
        } catch (error) {
            console.error('Error fetching roles:', error);
            return [];
        }
    },

    async createRole(role: CreateRoleRequest): Promise<SecurityRole> {
        const response = await fetch(`/api/security/roles`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(role),
        });

        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.message || 'Failed to create role');
        }

        return response.json();
    },

    async deleteRole(id: string): Promise<void> {
        const response = await fetch(`/api/security/roles/${id}`, {
            method: 'DELETE',
        });

        if (!response.ok) throw new Error('Failed to delete role');
    }
};
