#!/bin/bash

# Create types directory and file
mkdir -p src/types
cat > src/types/table.ts << 'EOL'
export interface Column {
  name: string;
  type: string;
  mandatory?: boolean;
  length?: number;
  precision?: number;
  scale?: number;
  defaultValue?: string;
}

export interface Table {
  id: string;
  name: string;
  displayName: string;
  description?: string;
  accessLevel: string;
  columns: Column[];
}
EOL

# Create api directory and mock data file
mkdir -p src/api
cat > src/api/mockData.ts << 'EOL'
import { Table } from '../types/table';

export const mockTables: Table[] = [
  {
    id: '1',
    name: 'business_partner',
    displayName: 'Business Partner',
    description: 'Business partner master data including customers and vendors',
    accessLevel: 'ORGANIZATION',
    columns: [
      { name: 'id', type: 'UUID', mandatory: true },
      { name: 'name', type: 'STRING', length: 100, mandatory: true },
      { name: 'tax_id', type: 'STRING', length: 20 },
      { name: 'email', type: 'STRING', length: 100 },
      { name: 'phone', type: 'STRING', length: 20 },
      { name: 'active', type: 'BOOLEAN', defaultValue: 'true' }
    ]
  },
  {
    id: '2',
    name: 'product',
    displayName: 'Product',
    description: 'Product master data with inventory and pricing information',
    accessLevel: 'ORGANIZATION',
    columns: [
      { name: 'id', type: 'UUID', mandatory: true },
      { name: 'code', type: 'STRING', length: 40, mandatory: true },
      { name: 'name', type: 'STRING', length: 100, mandatory: true },
      { name: 'description', type: 'STRING', length: 255 },
      { name: 'price', type: 'DECIMAL', precision: 10, scale: 2, mandatory: true },
      { name: 'cost', type: 'DECIMAL', precision: 10, scale: 2 },
      { name: 'stock', type: 'INTEGER', defaultValue: '0' }
    ]
  },
  {
    id: '3',
    name: 'sales_order',
    displayName: 'Sales Order',
    description: 'Customer orders with line items and tracking',
    accessLevel: 'ORGANIZATION',
    columns: [
      { name: 'id', type: 'UUID', mandatory: true },
      { name: 'order_date', type: 'TIMESTAMP', mandatory: true },
      { name: 'customer_id', type: 'UUID', mandatory: true },
      { name: 'total_amount', type: 'DECIMAL', precision: 12, scale: 2 },
      { name: 'status', type: 'STRING', length: 20, defaultValue: 'DRAFT' },
      { name: 'notes', type: 'STRING', length: 500 }
    ]
  }
];

export const mockTableApi = {
  getTables: async (): Promise<Table[]> => {
    // Simulate network delay
    await new Promise(resolve => setTimeout(resolve, 1000));
    return mockTables;
  },

  getTableById: async (id: string): Promise<Table | undefined> => {
    await new Promise(resolve => setTimeout(resolve, 500));
    return mockTables.find(table => table.id === id);
  }
};
EOL

# Update the useTableOperations hook
cat > src/hooks/useTableOperations.ts << 'EOL'
import { useState, useCallback } from 'react';
import { Table } from '../types/table';
import { mockTableApi } from '../api/mockData';

export function useTableOperations() {
  const [tables, setTables] = useState<Table[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const fetchTables = useCallback(async () => {
    try {
      setLoading(true);
      setError(null);
      const fetchedTables = await mockTableApi.getTables();
      setTables(fetchedTables);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to fetch tables');
    } finally {
      setLoading(false);
    }
  }, []);

  const getTableById = useCallback(async (id: string) => {
    try {
      setLoading(true);
      setError(null);
      return await mockTableApi.getTableById(id);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to fetch table');
      return undefined;
    } finally {
      setLoading(false);
    }
  }, []);

  return {
    tables,
    loading,
    error,
    fetchTables,
    getTableById,
  };
}
EOL

echo "Mock data setup complete!"
echo "Created:"
echo "  - src/types/table.ts"
echo "  - src/api/mockData.ts"
echo "  - Updated src/hooks/useTableOperations.ts"