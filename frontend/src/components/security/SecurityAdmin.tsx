import React, { useState } from 'react';
import { SecurityRole } from '../../types/security/types';
import { securityApi } from '../../api/securityApi';
import { Alert, AlertDescription } from '../ui/alert';
import { Button } from '../ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '../ui/card';
import { Loader2, Plus, Shield, Trash2, UserCog } from 'lucide-react';
import { CreateRoleDialog } from './CreateRoleDialog';
import { DeleteRoleDialog } from './DeleteRoleDialog';

export default function SecurityAdmin() {
    const [showCreateDialog, setShowCreateDialog] = useState(false);
    const [roleToDelete, setRoleToDelete] = useState<SecurityRole | null>(null);
    const [error, setError] = useState<string | null>(null);

    return (
        <div className="space-y-6">
            {error && (
                <Alert variant="destructive">
                    <AlertDescription>
                        {error}
                    </AlertDescription>
                </Alert>
            )}

            <div className="flex justify-between items-center">
                <h1 className="text-2xl font-bold">Security Administration</h1>
                <Button onClick={() => setShowCreateDialog(true)}>
                    <Plus className="h-4 w-4 mr-2" />
                    Create Role
                </Button>
            </div>

            <Card>
                <CardHeader>
                    <CardTitle>Role Management</CardTitle>
                </CardHeader>
                <CardContent>
                    {/* Role management content */}
                </CardContent>
            </Card>

            <CreateRoleDialog
                open={showCreateDialog}
                onClose={() => setShowCreateDialog(false)}
            />

            <DeleteRoleDialog
                role={roleToDelete}
                open={!!roleToDelete}
                onClose={() => setRoleToDelete(null)}
            />
        </div>
    );
}
