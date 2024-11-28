import QueryProvider from './lib/providers/QueryProvider';
import TableManagement from './components/tables/TableManagement';

function App() {
  return (
    <QueryProvider>
      <div className="container mx-auto py-10">
        <TableManagement />
      </div>
    </QueryProvider>
  );
}

export default App;