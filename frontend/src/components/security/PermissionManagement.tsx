import React, { useState } from 'react';
import { SecurityPermission } from '../../types/security/types';
import { Card, CardContent, CardHeader, CardTitle } from '../ui/card';
import { Button } from '../ui/button';
import { Shield, Plus } from 'lucide-react';

export function PermissionManagement({ roleId }: { roleId: string }) {
    const [permissions, setPermissions] = useState<SecurityPermission[]>([]);

    return (
        <div className="space-y-6">
            <div className="flex justify-between items-center">
                <h2 className="text-xl font-semibold">Role Permissions</h2>
                <Button onClick={() => {/* Handle add permission */}}>
                    <Plus className="h-4 w-4 mr-2" />
                    Add Permission
                </Button>
            </div>

            <div className="grid grid-cols-2 gap-4">
                <Card>
                    <CardHeader>
                        <CardTitle>Entity Permissions</CardTitle>
                    </CardHeader>
                    <CardContent>
                        <div className="relative w-full overflow-auto">
                            <table className="w-full caption-bottom text-sm">
                                <thead>
                                    <tr className="border-b">
                                        <th className="h-12 px-4 text-left">Entity</th>
                                        <th className="h-12 px-4 text-left">Type</th>
                                        <th className="h-12 px-4 text-left">Access</th>
                                        <th className="h-12 px-4 text-right">Actions</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {permissions.map((permission, index) => (
                                        <tr key={index} className="border-b">
                                            <td className="p-4">{permission.entityType}</td>
                                            <td className="p-4">{permission.entityId}</td>
                                            <td className="p-4">{permission.permissionType}</td>
                                            <td className="p-4 text-right">
                                                <Button
                                                    variant="ghost"
                                                    size="sm"
                                                    onClick={() => {/* Handle edit */}}
                                                >
                                                    Edit
                                                </Button>
                                            </td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                        </div>
                    </CardContent>
                </Card>

                <Card>
                    <CardHeader>
                        <CardTitle>Access Rules</CardTitle>
                    </CardHeader>
                    <CardContent>
                        <div className="relative w-full overflow-auto">
                            <table className="w-full caption-bottom text-sm">
                                <thead>
                                    <tr className="border-b">
                                        <th className="h-12 px-4 text-left">Rule</th>
                                        <th className="h-12 px-4 text-left">Status</th>
                                        <th className="h-12 px-4 text-right">Actions</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {/* Access rules will be implemented next */}
                                </tbody>
                            </table>
                        </div>
                    </CardContent>
                </Card>
            </div>
        </div>
    );
}
