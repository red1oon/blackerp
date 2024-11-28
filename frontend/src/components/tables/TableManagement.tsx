import React from 'react';
import { useQuery } from '@tanstack/react-query';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table';

interface TableData {
  id: string;
  name: string;
  displayName: string;
  description?: string;
  accessLevel: string;
}

interface TablesResponse {
  tables: TableData[];
}

export default function TableManagement() {
  const { data, isLoading, error } = useQuery<TablesResponse>({
    queryKey: ['tables'],
    queryFn: async () => {
      const response = await fetch('http://localhost:8080/api/tables');
      if (!response.ok) throw new Error('Failed to fetch tables');
      return response.json();
    }
  });

  if (isLoading) {
    return <div>Loading tables...</div>;
  }

  if (error) {
    return <div>Error: {error instanceof Error ? error.message : 'Unknown error'}</div>;
  }

  return (
    <Card>
      <CardHeader>
        <CardTitle>Tables</CardTitle>
      </CardHeader>
      <CardContent>
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>Name</TableHead>
              <TableHead>Display Name</TableHead>
              <TableHead>Description</TableHead>
              <TableHead>Access Level</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {data?.tables.map((table) => (
              <TableRow key={table.id}>
                <TableCell>{table.name}</TableCell>
                <TableCell>{table.displayName}</TableCell>
                <TableCell>{table.description}</TableCell>
                <TableCell>{table.accessLevel}</TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </CardContent>
    </Card>
  );
}