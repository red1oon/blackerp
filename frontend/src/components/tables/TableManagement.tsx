import React, { useState } from 'react';
import { Button } from '@/components/ui/button';
import { Plus } from 'lucide-react';
import { useTableOperations } from '@/hooks/useTableOperations';
import { CreateTableForm } from './';
import { DataTableWithState } from './';
import { Alert, AlertDescription } from '@/components/ui/alert';

export default function TableManagement() {
  const [showCreateForm, setShowCreateForm] = useState(false);
  const { error } = useTableOperations();

  return (
    <div className="space-y-6">
      {error && (
        <Alert variant="destructive">
          <AlertDescription>
            {error instanceof Error ? error.message : 'An error occurred'}
          </AlertDescription>
        </Alert>
      )}
      
      {!showCreateForm ? (
        <>
          <div className="flex justify-between items-center">
            <h1 className="text-2xl font-bold">Tables</h1>
            <Button onClick={() => setShowCreateForm(true)}>
              <Plus className="h-4 w-4 mr-2" />
              Create Table
            </Button>
          </div>
          <DataTableWithState />
        </>
      ) : (
        <CreateTableForm onCancel={() => setShowCreateForm(false)} />
      )}
    </div>
  );
}