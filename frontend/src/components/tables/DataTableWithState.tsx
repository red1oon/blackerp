import React, { useState } from 'react';
import { useTableOperations } from '@/hooks/useTableOperations';
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table';
import { Button } from '@/components/ui/button';
import { Alert, AlertDescription } from '@/components/ui/alert';
import { Loader2, Pencil, Trash2 } from "lucide-react";
import { DeleteTableDialog } from './DeleteTableDialog';
import type { Table as TableType } from '@/types/table';

export default function DataTableWithState() {
  const {
    tables,
    isLoading,
    isError,
    error,
    deleteTable,
    isDeleting
  } = useTableOperations();

  const [tableToDelete, setTableToDelete] = useState<TableType | null>(null);

  const handleDelete = async () => {
    if (tableToDelete) {
      try {
        await deleteTable(tableToDelete.id);
      } finally {
        setTableToDelete(null);
      }
    }
  };

  if (isLoading) {
    return (
      <div className="w-full h-48 flex items-center justify-center">
        <Loader2 className="h-8 w-8 animate-spin text-primary" />
      </div>
    );
  }

  if (isError) {
    return (
      <Alert variant="destructive" className="mb-4">
        <AlertDescription>
          {error instanceof Error ? error.message : 'Failed to load tables'}
        </AlertDescription>
      </Alert>
    );
  }

  return (
    <>
      <div className="rounded-md border">
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>Name</TableHead>
              <TableHead>Display Name</TableHead>
              <TableHead>Description</TableHead>
              <TableHead>Access Level</TableHead>
              <TableHead className="text-right">Actions</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {tables?.length === 0 ? (
              <TableRow>
                <TableCell colSpan={5} className="text-center text-muted-foreground">
                  No tables found
                </TableCell>
              </TableRow>
            ) : (
              tables?.map((table) => (
                <TableRow key={table.id}>
                  <TableCell className="font-medium">{table.name}</TableCell>
                  <TableCell>{table.displayName}</TableCell>
                  <TableCell>{table.description || '-'}</TableCell>
                  <TableCell>{table.accessLevel}</TableCell>
                  <TableCell className="text-right">
                    <div className="flex justify-end gap-2">
                      <Button size="sm" variant="outline">
                        <Pencil className="h-4 w-4 mr-1" />
                        Edit
                      </Button>
                      <Button 
                        size="sm" 
                        variant="destructive"
                        onClick={() => setTableToDelete(table)}
                      >
                        <Trash2 className="h-4 w-4 mr-1" />
                        Delete
                      </Button>
                    </div>
                  </TableCell>
                </TableRow>
              ))
            )}
          </TableBody>
        </Table>
      </div>

      <DeleteTableDialog
        table={tableToDelete}
        open={!!tableToDelete}
        onClose={() => setTableToDelete(null)}
        onConfirm={handleDelete}
        isDeleting={isDeleting}
      />
    </>
  );
}