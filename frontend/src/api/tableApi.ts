import { Table, CreateTableRequest } from '../types/table';

const BASE_URL = '/api';

export const tableApi = {
  async getTables(): Promise<Table[]> {
    try {
      const response = await fetch(`${BASE_URL}/tables`);
      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.message || 'Failed to fetch tables');
      }
      const data = await response.json();
      return data.tables || [];
    } catch (error) {
      console.error('Error fetching tables:', error);
      throw error;
    }
  },

  async createTable(table: CreateTableRequest): Promise<Table> {
    try {
      const response = await fetch(`${BASE_URL}/tables`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(table),
      });

      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.message || 'Failed to create table');
      }

      return await response.json();
    } catch (error) {
      console.error('Error creating table:', error);
      throw error;
    }
  },

  async updateTable({ id, table }: { id: string; table: Partial<Table> }): Promise<Table> {
    try {
      const response = await fetch(`${BASE_URL}/tables/${id}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(table),
      });

      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.message || 'Failed to update table');
      }

      return await response.json();
    } catch (error) {
      console.error('Error updating table:', error);
      throw error;
    }
  },

  async deleteTable(id: string): Promise<void> {
    try {
      const response = await fetch(`${BASE_URL}/tables/${id}`, {
        method: 'DELETE',
      });

      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.message || 'Failed to delete table');
      }
    } catch (error) {
      console.error('Error deleting table:', error);
      throw error;
    }
  }
};