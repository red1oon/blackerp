import { useState } from 'react';
import { useTableOperations } from '@/hooks/useTableOperations';
import {
  Card,
  CardHeader,
  CardTitle,
  CardDescription,
  CardContent
} from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Alert, AlertDescription } from '@/components/ui/alert';
import { Plus, Minus, Loader2 } from 'lucide-react';
import { CreateTableRequest } from '@/types/table';

interface Props {
  onCancel: () => void;
}

const CreateTableForm = ({ onCancel }: Props) => {
  const { createTable, isCreating } = useTableOperations();
  const [columns, setColumns] = useState([{
    name: '',
    displayName: '',
    type: 'STRING',
    mandatory: false,
    length: null,
    precision: null,
    scale: null
  }]);

  const [formData, setFormData] = useState({
    name: '',
    displayName: '',
    description: '',
    accessLevel: 'ORGANIZATION'
  });

  const [error, setError] = useState('');

  const handleAddColumn = () => {
    setColumns([...columns, {
      name: '',
      displayName: '',
      type: 'STRING',
      mandatory: false,
      length: null,
      precision: null,
      scale: null
    }]);
  };

  const handleRemoveColumn = (index: number) => {
    setColumns(columns.filter((_, i) => i !== index));
  };

  const handleColumnChange = (index: number, field: string, value: any) => {
    const updatedColumns = [...columns];
    updatedColumns[index] = { ...updatedColumns[index], [field]: value };
    setColumns(updatedColumns);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');

    if (!formData.name || !formData.displayName) {
      setError('Name and Display Name are required');
      return;
    }

    if (columns.length === 0) {
      setError('At least one column is required');
      return;
    }

    const columnErrors = columns.reduce((errors: string[], col, index) => {
      if (!col.name || !col.displayName) {
        errors.push(`Column ${index + 1}: Name and Display Name are required`);
      }
      if (col.type === 'STRING' && !col.length) {
        errors.push(`Column ${index + 1}: Length is required for String type`);
      }
      if (col.type === 'DECIMAL' && (!col.precision || !col.scale)) {
        errors.push(`Column ${index + 1}: Precision and Scale are required for Decimal type`);
      }
      return errors;
    }, []);

    if (columnErrors.length > 0) {
      setError(columnErrors.join('\n'));
      return;
    }

    try {
      const tableData: CreateTableRequest = {
        ...formData,
        columns: columns.map(col => ({
          ...col,
          length: col.type === 'STRING' ? col.length : undefined,
          precision: col.type === 'DECIMAL' ? col.precision : undefined,
          scale: col.type === 'DECIMAL' ? col.scale : undefined,
        }))
      };

      await createTable(tableData);
      onCancel();
    } catch (err) {
      console.error('Create table error:', err);
      setError(err instanceof Error ? err.message : 'Failed to create table');
    }
  };

  return (
    <Card className="w-full max-w-4xl mx-auto">
      <CardHeader>
        <CardTitle>Create New Table</CardTitle>
        <CardDescription>Define your table structure and columns</CardDescription>
      </CardHeader>
      <CardContent>
        <form onSubmit={handleSubmit} className="space-y-6">
          {error && (
            <Alert variant="destructive">
              <AlertDescription>
                {error.split('\n').map((line, i) => (
                  <div key={i}>{line}</div>
                ))}
              </AlertDescription>
            </Alert>
          )}

          <div className="space-y-4">
            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-2">
                <label className="text-sm font-medium">Name</label>
                <input
                  type="text"
                  className="w-full p-2 border rounded"
                  value={formData.name}
                  onChange={e => setFormData({ ...formData, name: e.target.value.toLowerCase() })}
                  placeholder="table_name"
                  pattern="^[a-z][a-z0-9_]*$"
                  title="Must start with lowercase letter, followed by lowercase letters, numbers, or underscores"
                />
              </div>
              <div className="space-y-2">
                <label className="text-sm font-medium">Display Name</label>
                <input
                  type="text"
                  className="w-full p-2 border rounded"
                  value={formData.displayName}
                  onChange={e => setFormData({ ...formData, displayName: e.target.value })}
                  placeholder="Table Name"
                />
              </div>
            </div>

            <div className="space-y-2">
              <label className="text-sm font-medium">Description</label>
              <textarea
                className="w-full p-2 border rounded"
                value={formData.description}
                onChange={e => setFormData({ ...formData, description: e.target.value })}
                rows={3}
              />
            </div>

            <div className="space-y-2">
              <label className="text-sm font-medium">Access Level</label>
              <select
                className="w-full p-2 border rounded"
                value={formData.accessLevel}
                onChange={e => setFormData({ ...formData, accessLevel: e.target.value })}
              >
                <option value="SYSTEM">System</option>
                <option value="CLIENT">Client</option>
                <option value="ORGANIZATION">Organization</option>
                <option value="CLIENT_ORGANIZATION">Client Organization</option>
              </select>
            </div>
          </div>

          <div className="space-y-4">
            <div className="flex justify-between items-center">
              <h3 className="text-lg font-medium">Columns</h3>
              <Button
                type="button"
                onClick={handleAddColumn}
                variant="outline"
                size="sm"
              >
                <Plus className="h-4 w-4 mr-2" />
                Add Column
              </Button>
            </div>

            {columns.map((column, index) => (
              <div key={index} className="p-4 border rounded space-y-4">
                <div className="flex justify-between">
                  <h4 className="font-medium">Column {index + 1}</h4>
                  {columns.length > 1 && (
                    <Button
                      type="button"
                      variant="destructive"
                      size="sm"
                      onClick={() => handleRemoveColumn(index)}
                    >
                      <Minus className="h-4 w-4" />
                    </Button>
                  )}
                </div>

                <div className="grid grid-cols-2 gap-4">
                  <div className="space-y-2">
                    <label className="text-sm font-medium">Name</label>
                    <input
                      type="text"
                      className="w-full p-2 border rounded"
                      value={column.name}
                      onChange={e => handleColumnChange(index, 'name', e.target.value.toLowerCase())}
                      placeholder="column_name"
                      pattern="^[a-z][a-z0-9_]*$"
                      title="Must start with lowercase letter, followed by lowercase letters, numbers, or underscores"
                    />
                  </div>
                  <div className="space-y-2">
                    <label className="text-sm font-medium">Display Name</label>
                    <input
                      type="text"
                      className="w-full p-2 border rounded"
                      value={column.displayName}
                      onChange={e => handleColumnChange(index, 'displayName', e.target.value)}
                      placeholder="Column Name"
                    />
                  </div>
                </div>

                <div className="grid grid-cols-2 gap-4">
                  <div className="space-y-2">
                    <label className="text-sm font-medium">Type</label>
                    <select
                      className="w-full p-2 border rounded"
                      value={column.type}
                      onChange={e => handleColumnChange(index, 'type', e.target.value)}
                    >
                      <option value="STRING">String</option>
                      <option value="INTEGER">Integer</option>
                      <option value="DECIMAL">Decimal</option>
                      <option value="BOOLEAN">Boolean</option>
                      <option value="DATE">Date</option>
                      <option value="TIMESTAMP">Timestamp</option>
                      <option value="BINARY">Binary</option>
                    </select>
                  </div>
                  <div className="space-y-2">
                    <label className="text-sm font-medium">Mandatory</label>
                    <div className="pt-2">
                      <input
                        type="checkbox"
                        checked={column.mandatory}
                        onChange={e => handleColumnChange(index, 'mandatory', e.target.checked)}
                        className="mr-2"
                      />
                      <span className="text-sm">Required field</span>
                    </div>
                  </div>
                </div>

                {column.type === 'STRING' && (
                  <div className="space-y-2">
                    <label className="text-sm font-medium">Length</label>
                    <input
                      type="number"
                      className="w-full p-2 border rounded"
                      value={column.length || ''}
                      onChange={e => handleColumnChange(index, 'length', parseInt(e.target.value) || null)}
                      min="1"
                      max="4000"
                    />
                  </div>
                )}

                {column.type === 'DECIMAL' && (
                  <div className="grid grid-cols-2 gap-4">
                    <div className="space-y-2">
                      <label className="text-sm font-medium">Precision</label>
                      <input
                        type="number"
                        className="w-full p-2 border rounded"
                        value={column.precision || ''}
                        onChange={e => handleColumnChange(index, 'precision', parseInt(e.target.value) || null)}
                        min="1"
                        max="38"
                      />
                    </div>
                    <div className="space-y-2">
                      <label className="text-sm font-medium">Scale</label>
                      <input
                        type="number"
                        className="w-full p-2 border rounded"
                        value={column.scale || ''}
                        onChange={e => handleColumnChange(index, 'scale', parseInt(e.target.value) || null)}
                        min="0"
                        max="38"
                      />
                    </div>
                  </div>
                )}
              </div>
            ))}
          </div>

          <div className="flex justify-end gap-4 pt-4">
            <Button type="button" variant="outline" onClick={onCancel}>
              Cancel
            </Button>
            <Button type="submit" disabled={isCreating}>
              {isCreating && <Loader2 className="mr-2 h-4 w-4 animate-spin" />}
              Create Table
            </Button>
          </div>
        </form>
      </CardContent>
    </Card>
  );
};

export default CreateTableForm;