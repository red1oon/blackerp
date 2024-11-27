// src/components/tables/TableManagement.tsx
import React, { useState, useEffect } from 'react';
import { Plus, RefreshCw, Edit, Trash2 } from 'lucide-react';
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@/components/ui/card';
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table';
import { Button } from '@/components/ui/button';
import { useTableOperations } from '@/hooks/useTableOperations';
import { Table as TableType } from '@/types/table';

const TableManagement: React.FC = () => {
  const { tables, loading, error, fetchTables } = useTableOperations();

  useEffect(() => {
    fetchTables();
  }, []);

  if (loading) {
    return <div>Loading tables...</div>;
  }

  if (error) {
    return <div>Error: {error}</div>;
  }

  return (
    <div className="container mx-auto p-4 space-y-4">
      <div className="flex justify-between items-center">
        <h1 className="text-2xl font-bold">Table Management</h1>
        <div className="space-x-2">
          <Button size="sm" variant="outline" onClick={fetchTables}>
            <RefreshCw className="h-4 w-4 mr-2" />
            Refresh
          </Button>
          <Button size="sm">
            <Plus className="h-4 w-4 mr-2" />
            Create Table
          </Button>
        </div>
      </div>

      <div className="grid grid-cols-1 gap-4">
        {tables.map(table => (
          <Card key={table.id}>
            <CardHeader>
              <div className="flex justify-between items-start">
                <div>
                  <CardTitle>{table.displayName}</CardTitle>
                  <CardDescription>{table.name}</CardDescription>
                </div>
                <div className="space-x-2">
                  <Button size="sm" variant="outline">
                    <Edit className="h-4 w-4 mr-2" />
                    Edit
                  </Button>
                  <Button size="sm" variant="destructive">
                    <Trash2 className="h-4 w-4 mr-2" />
                    Delete
                  </Button>
                </div>
              </div>
            </CardHeader>
            <CardContent>
              <div className="text-sm text-gray-600 mb-4">
                {table.description}
              </div>
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead>Column Name</TableHead>
                    <TableHead>Type</TableHead>
                    <TableHead>Mandatory</TableHead>
                    <TableHead>Properties</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {table.columns.map((column, idx) => (
                    <TableRow key={idx}>
                      <TableCell>{column.name}</TableCell>
                      <TableCell>{column.type}</TableCell>
                      <TableCell>
                        {column.mandatory ? 'Yes' : 'No'}
                      </TableCell>
                      <TableCell>
                        {column.length && `Length: ${column.length}`}
                        {column.precision && `Precision: ${column.precision}`}
                        {column.scale && `, Scale: ${column.scale}`}
                        {column.defaultValue && `, Default: ${column.defaultValue}`}
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </CardContent>
          </Card>
        ))}
      </div>
    </div>
  );
};

export default TableManagement;