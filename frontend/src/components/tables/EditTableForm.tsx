import React, { useState } from 'react';
import { useTableOperations } from '@/hooks/useTableOperations';
import {
  Card,
  CardHeader,
  CardTitle, 
  CardDescription,
  CardContent,
} from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Alert, AlertDescription } from '@/components/ui/alert';
import { Loader2 } from 'lucide-react';
import { Table } from '@/types/table';

interface Props {
  table: Table;
  onCancel: () => void;
  onSuccess: () => void;
}

const EditTableForm = ({ table, onCancel, onSuccess }: Props) => {
  const { updateTable, isUpdating } = useTableOperations();
  const [error, setError] = useState('');
  const [formData, setFormData] = useState({
    displayName: table.displayName,
    description: table.description || '',
    accessLevel: table.accessLevel
  });

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');

    if (!formData.displayName) {
      setError('Display Name is required');
      return;
    }

    try {
      await updateTable({
        id: table.id,
        table: formData
      });
      onSuccess();
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to update table');
    }
  };

  return (
    <Card className="w-full max-w-2xl mx-auto">
      <CardHeader>
        <CardTitle>Edit Table: {table.name}</CardTitle>
        <CardDescription>Update table properties</CardDescription>
      </CardHeader>
      <CardContent>
        <form onSubmit={handleSubmit} className="space-y-6">
          {error && (
            <Alert variant="destructive">
              <AlertDescription>{error}</AlertDescription>
            </Alert>
          )}
          
          <div className="space-y-4">
            <div className="space-y-2">
              <label className="text-sm font-medium">Display Name</label>
              <input
                type="text"
                className="w-full p-2 border rounded"
                value={formData.displayName}
                onChange={e => setFormData({...formData, displayName: e.target.value})}
              />
            </div>

            <div className="space-y-2">
              <label className="text-sm font-medium">Description</label>
              <textarea
                className="w-full p-2 border rounded"
                value={formData.description}
                onChange={e => setFormData({...formData, description: e.target.value})}
                rows={3}
              />
            </div>

            <div className="space-y-2">
              <label className="text-sm font-medium">Access Level</label>
              <select
                className="w-full p-2 border rounded"
                value={formData.accessLevel}
                onChange={e => setFormData({...formData, accessLevel: e.target.value})}
              >
                <option value="SYSTEM">System</option>
                <option value="CLIENT">Client</option>
                <option value="ORGANIZATION">Organization</option>
                <option value="CLIENT_ORGANIZATION">Client Organization</option>
              </select>
            </div>
          </div>

          <div className="flex justify-end gap-4 pt-4">
            <Button type="button" variant="outline" onClick={onCancel}>
              Cancel
            </Button>
            <Button type="submit" disabled={isUpdating}>
              {isUpdating && <Loader2 className="mr-2 h-4 w-4 animate-spin" />}
              Update Table
            </Button>
          </div>
        </form>
      </CardContent>
    </Card>
  );
};

export default EditTableForm;