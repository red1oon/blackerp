import React, { useState } from 'react';
import {
    Dialog,
    DialogContent,
    DialogHeader,
    DialogTitle,
    DialogFooter,
} from '../ui/dialog';
import { Button } from '../ui/button';
import { Shield } from 'lucide-react';

interface Props {
    roleId: string;
    open: boolean;
    onClose: () => void;
}

export function RolePermissionDialog({ roleId, open, onClose }: Props) {
    const [selectedEntity, setSelectedEntity] = useState('');
    const [selectedPermission, setSelectedPermission] = useState('');

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        // Handle permission assignment
        onClose();
    };

    return (
        <Dialog open={open} onOpenChange={onClose}>
            <DialogContent>
                <DialogHeader>
                    <DialogTitle>Assign Permission</DialogTitle>
                </DialogHeader>
                <form onSubmit={handleSubmit} className="space-y-4">
                    <div className="space-y-2">
                        <label className="text-sm font-medium">Entity Type</label>
                        <select
                            className="w-full p-2 border rounded"
                            value={selectedEntity}
                            onChange={(e) => setSelectedEntity(e.target.value)}
                        >
                            <option value="">Select Entity Type</option>
                            <option value="TABLE">Table</option>
                            <option value="WINDOW">Window</option>
                            <option value="PROCESS">Process</option>
                        </select>
                    </div>

                    <div className="space-y-2">
                        <label className="text-sm font-medium">Permission</label>
                        <select
                            className="w-full p-2 border rounded"
                            value={selectedPermission}
                            onChange={(e) => setSelectedPermission(e.target.value)}
                        >
                            <option value="">Select Permission</option>
                            <option value="READ">Read</option>
                            <option value="WRITE">Write</option>
                            <option value="DELETE">Delete</option>
                            <option value="EXECUTE">Execute</option>
                        </select>
                    </div>

                    <DialogFooter>
                        <Button type="button" variant="outline" onClick={onClose}>
                            Cancel
                        </Button>
                        <Button type="submit">
                            <Shield className="h-4 w-4 mr-2" />
                            Assign Permission
                        </Button>
                    </DialogFooter>
                </form>
            </DialogContent>
        </Dialog>
    );
}
