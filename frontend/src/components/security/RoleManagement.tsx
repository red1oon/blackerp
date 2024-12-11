import React, { useState, useEffect } from 'react';
import { SecurityRole } from '../../types/security/types';
import { securityApi } from '../../api/security/securityApi';
import { Alert, AlertDescription } from '../ui/alert';
import { Card, CardContent } from '../ui/card';
import { Button } from '../ui/button';
import { Plus, Shield, Trash2, Loader2 } from 'lucide-react';

export function RoleManagement() {
    const [roles, setRoles] = useState<SecurityRole[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        loadRoles();
    }, []);

    const loadRoles = async () => {
        try {
            const data = await securityApi.getRoles();
            setRoles(data);
        } catch (err) {
            setError(err instanceof Error ? err.message : 'Failed to load roles');
        } finally {
            setLoading(false);
        }
    };

    if (loading) {
        return (
            <div className="flex justify-center items-center h-48">
                <Loader2 className="h-8 w-8 animate-spin text-primary" />
            </div>
        );
    }

    if (error) {
        return (
            <Alert variant="destructive">
                <AlertDescription>{error}</AlertDescription>
            </Alert>
        );
    }

    return (
        <Card>
            <CardContent>
                <div className="relative w-full overflow-auto">
                    <table className="w-full caption-bottom text-sm">
                        <thead>
                            <tr className="border-b">
                                <th className="h-12 px-4 text-left">Name</th>
                                <th className="h-12 px-4 text-left">Display Name</th>
                                <th className="h-12 px-4 text-left">Access Level</th>
                                <th className="h-12 px-4 text-left">Status</th>
                                <th className="h-12 px-4 text-right">Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            {roles.map(role => (
                                <tr key={role.id} className="border-b">
                                    <td className="p-4">{role.name}</td>
                                    <td className="p-4">{role.displayName}</td>
                                    <td className="p-4">{role.accessLevel}</td>
                                    <td className="p-4">
                                        {role.isActive ? (
                                            <span className="text-green-600">Active</span>
                                        ) : (
                                            <span className="text-red-600">Inactive</span>
                                        )}
                                    </td>
                                    <td className="p-4 text-right">
                                        <div className="flex justify-end space-x-2">
                                            <Button
                                                variant="outline"
                                                size="sm"
                                                onClick={() => {/* Handle permissions */}}
                                            >
                                                <Shield className="h-4 w-4 mr-2" />
                                                Permissions
                                            </Button>
                                            <Button
                                                variant="destructive"
                                                size="sm"
                                                onClick={() => {/* Handle delete */}}
                                            >
                                                <Trash2 className="h-4 w-4 mr-2" />
                                                Delete
                                            </Button>
                                        </div>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            </CardContent>
        </Card>
    );
}
