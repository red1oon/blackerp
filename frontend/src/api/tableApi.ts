import { Table } from '../types/table';

const BASE_URL = '/api';

export const tableApi = {
  async getTables(): Promise<Table[]> {
    const response = await fetch(`${BASE_URL}/tables`);
    if (!response.ok) throw new Error('Failed to fetch tables');
    return response.json();
  },

  async createTable(table: Omit<Table, 'id'>): Promise<Table> {
    const response = await fetch(`${BASE_URL}/tables`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(table),
    });
    if (!response.ok) throw new Error('Failed to create table');
    return response.json();
  },
};
