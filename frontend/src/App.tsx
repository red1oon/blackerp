import QueryProvider from './lib/providers/QueryProvider';
import { DataTableWithState } from './components/tables';

function App() {
  return (
    <QueryProvider>
      <div className="container mx-auto py-10">
        <h1 className="text-2xl font-bold mb-6">Tables</h1>
        <DataTableWithState />
      </div>
    </QueryProvider>
  );
}

export default App;
