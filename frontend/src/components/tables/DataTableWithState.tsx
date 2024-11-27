// frontend/src/components/tables/DataTableWithState.tsx
import React from 'react';
import { useQuery } from '@tanstack/react-query';
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "../../components/ui/table";  // Changed from @/components/ui/table
import { Alert, AlertTitle, AlertDescription } from "../../components/ui/alert";  // Changed from @/components/ui/alert
import { Loader2 } from "lucide-react";

export interface Table {
  id: string;
  name: string;
  displayName: string;
  accessLevel: string;
}

export default function DataTableWithState() {
  const {
    data: tables,
    isLoading,
    isError,
    error
  } = useQuery<Table[]>({
    queryKey: ['tables'],
    queryFn: async () => {
      const response = await fetch('/api/tables');
      if (!response.ok) {
        throw new Error('Failed to fetch tables');
      }
      return response.json();
    }
  });

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
        <AlertTitle>Error</AlertTitle>
        <AlertDescription>
          {error instanceof Error ? error.message : 'Failed to load tables'}
        </AlertDescription>
      </Alert>
    );
  }

  return (
    <div className="rounded-md border">
      <Table>
        <TableHeader>
          <TableRow>
            <TableHead>Name</TableHead>
            <TableHead>Display Name</TableHead>
            <TableHead>Access Level</TableHead>
            <TableHead>Actions</TableHead>
          </TableRow>
        </TableHeader>
        <TableBody>
          {tables?.length === 0 ? (
            <TableRow>
              <TableCell colSpan={4} className="text-center text-muted-foreground">
                No tables found
              </TableCell>
            </TableRow>
          ) : (
            tables?.map((table) => (
              <TableRow key={table.id}>
                <TableCell>{table.name}</TableCell>
                <TableCell>{table.displayName}</TableCell>
                <TableCell>{table.accessLevel}</TableCell>
                <TableCell>
                  {/* Actions will be added later */}
                </TableCell>
              </TableRow>
            ))
          )}
        </TableBody>
      </Table>
    </div>
  );
}
