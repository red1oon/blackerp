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
