import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { Table } from '../types/table';
import { tableApi } from '../api/tableApi';

export function useTableOperations() {
  const queryClient = useQueryClient();

  const {
    data: tables,
    isLoading,
    error,
  } = useQuery<Table[]>({
    queryKey: ['tables'],
    queryFn: () => tableApi.getTables(),
  });

  const createTableMutation = useMutation({
    mutationFn: (newTable: Omit<Table, 'id'>) => tableApi.createTable(newTable),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['tables'] });
    },
  });

  const updateTableMutation = useMutation({
    mutationFn: ({ id, table }: { id: string; table: Partial<Table> }) =>
      tableApi.updateTable(id, table),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['tables'] });
    },
  });

  const deleteTableMutation = useMutation({
    mutationFn: (id: string) => tableApi.deleteTable(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['tables'] });
    },
  });

  return {
    tables,
    isLoading,
    error,
    createTable: createTableMutation.mutate,
    updateTable: updateTableMutation.mutate,
    deleteTable: deleteTableMutation.mutate,
    isCreating: createTableMutation.isPending,
    isUpdating: updateTableMutation.isPending,
    isDeleting: deleteTableMutation.isPending,
  };
}