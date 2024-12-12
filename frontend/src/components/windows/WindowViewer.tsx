import { useState, useEffect } from 'react';
import { Card, CardHeader, CardTitle, CardContent } from '@/components/ui/card';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';
import { Table, TableHeader, TableBody, TableRow, TableCell } from '@/components/ui/table';
import { Loader } from 'lucide-react';

interface WindowField {
  id: string;
  displayName: string;
  name: string;
  fieldType: string;
  required: boolean;
  readOnly: boolean;
  defaultValue?: string;
}

interface WindowTab {
  id: string;
  displayName: string;
  name: string;
  fields: WindowField[];
  tableId: string;
}

interface Window {
  id: string;
  displayName: string;
  name: string;
  tabs: WindowTab[];
}

const WindowViewer = () => {
  const [windows, setWindows] = useState<Window[]>([]);
  const [selectedWindow, setSelectedWindow] = useState<Window | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchWindows();
  }, []);

  const fetchWindows = async () => {
    try {
      const response = await fetch('/api/metadata/windows');
      const data = await response.json();
      setWindows(data);
      setLoading(false);
    } catch (error) {
      console.error('Error fetching windows:', error);
      setLoading(false);
    }
  };

  const loadWindow = async (id: string) => {
    try {
      setLoading(true);
      const response = await fetch(`/api/metadata/windows/${id}`);
      const data = await response.json();
      setSelectedWindow(data);
      setLoading(false);
    } catch (error) {
      console.error('Error loading window:', error);
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center h-64">
        <Loader className="w-6 h-6 animate-spin" />
      </div>
    );
  }

  return (
    <div className="p-4 space-y-4">
      <Card>
        <CardHeader>
          <CardTitle>Available Windows</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="grid grid-cols-3 gap-4">
            {windows.map(window => (
              <Button
                key={window.id}
                variant="outline"
                onClick={() => loadWindow(window.id)}
              >
                {window.displayName}
              </Button>
            ))}
          </div>
        </CardContent>
      </Card>

      {selectedWindow && (
        <Card>
          <CardHeader>
            <CardTitle>{selectedWindow.displayName}</CardTitle>
          </CardHeader>
          <CardContent>
            <Tabs defaultValue={selectedWindow.tabs[0]?.id}>
              <TabsList>
                {selectedWindow.tabs.map(tab => (
                  <TabsTrigger key={tab.id} value={tab.id}>
                    {tab.displayName}
                  </TabsTrigger>
                ))}
              </TabsList>

              {selectedWindow.tabs.map(tab => (
                <TabsContent key={tab.id} value={tab.id}>
                  <Table>
                    <TableHeader>
                      <TableRow>
                        <TableCell>Field</TableCell>
                        <TableCell>Value</TableCell>
                      </TableRow>
                    </TableHeader>
                    <TableBody>
                      {tab.fields.map(field => (
                        <TableRow key={field.id}>
                          <TableCell className="font-medium">
                            {field.displayName}
                            {field.required && <span className="text-red-500 ml-1">*</span>}
                          </TableCell>
                          <TableCell>
                            <Input
                              type="text"
                              placeholder={field.displayName}
                              disabled={field.readOnly}
                              defaultValue={field.defaultValue || ''}
                            />
                          </TableCell>
                        </TableRow>
                      ))}
                    </TableBody>
                  </Table>
                </TabsContent>
              ))}
            </Tabs>
          </CardContent>
        </Card>
      )}
    </div>
  );
}

export default WindowViewer;
