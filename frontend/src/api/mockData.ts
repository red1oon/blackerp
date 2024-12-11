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
