import React from 'react';
import { Button } from '../ui/button';
import {
    Dialog,
    DialogContent,
    DialogHeader,
    DialogTitle,
    DialogFooter,
} from '../ui/dialog';
import { CreateRoleRequest } from '../../types/security/types';

interface Props {
    open: boolean;
    onClose: () => void;
}

export function CreateRoleDialog({ open, onClose }: Props) {
    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        // Implementation
        onClose();
    };

    return (
        <Dialog open={open} onOpenChange={onClose}>
            <DialogContent>
                <DialogHeader>
                    <DialogTitle>Create New Role</DialogTitle>
                </DialogHeader>
                <form onSubmit={handleSubmit}>
                    {/* Form fields */}
                    <DialogFooter>
                        <Button type="button" variant="outline" onClick={onClose}>
                            Cancel
                        </Button>
                        <Button type="submit">Create</Button>
                    </DialogFooter>
                </form>
            </DialogContent>
        </Dialog>
    );
}
