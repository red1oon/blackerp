import { Table } from '../types/table';

const BASE_URL = 'http://localhost:8080/api';

export const tableApi = {
  async getTables(): Promise<Table[]> {
    const response = await fetch(`${BASE_URL}/tables`);
    if (!response.ok) throw new Error('Failed to fetch tables');
    return response.json();
  },

  async getTableById(id: string): Promise<Table> {
    const response = await fetch(`${BASE_URL}/tables/${id}`);
    if (!response.ok) throw new Error('Failed to fetch table');
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

  async updateTable(id: string, table: Partial<Table>): Promise<Table> {
    const response = await fetch(`${BASE_URL}/tables/${id}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(table),
    });
    if (!response.ok) throw new Error('Failed to update table');
    return response.json();
  },

  async deleteTable(id: string): Promise<void> {
    const response = await fetch(`${BASE_URL}/tables/${id}`, {
      method: 'DELETE',
    });
    if (!response.ok) throw new Error('Failed to delete table');
  }
};