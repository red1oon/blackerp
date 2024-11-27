// src/components/ui/table.tsx
import React from 'react';

export const Table: React.FC<{children: React.ReactNode}> = ({ children }) => (
  <div className="w-full overflow-auto">
    <table className="w-full text-sm">
      {children}
    </table>
  </div>
);

export const TableHeader: React.FC<{children: React.ReactNode}> = ({ children }) => (
  <thead className="border-b bg-slate-50">
    {children}
  </thead>
);

export const TableBody: React.FC<{children: React.ReactNode}> = ({ children }) => (
  <tbody>
    {children}
  </tbody>
);

export const TableRow: React.FC<{children: React.ReactNode}> = ({ children }) => (
  <tr className="border-b hover:bg-slate-50">
    {children}
  </tr>
);

export const TableHead: React.FC<{children: React.ReactNode}> = ({ children }) => (
  <th className="px-4 py-3 text-left font-medium text-slate-700">
    {children}
  </th>
);

export const TableCell: React.FC<{children: React.ReactNode}> = ({ children }) => (
  <td className="px-4 py-3 text-slate-900">
    {children}
  </td>
);