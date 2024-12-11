// src/components/ui/card.tsx
import React from 'react';

export const Card: React.FC<{children: React.ReactNode}> = ({ children }) => (
  <div className="bg-white rounded-lg shadow-md p-4">
    {children}
  </div>
);

export const CardHeader: React.FC<{children: React.ReactNode}> = ({ children }) => (
  <div className="mb-4">
    {children}
  </div>
);

export const CardTitle: React.FC<{children: React.ReactNode}> = ({ children }) => (
  <h2 className="text-xl font-bold text-gray-900">
    {children}
  </h2>
);

export const CardDescription: React.FC<{children: React.ReactNode}> = ({ children }) => (
  <p className="text-sm text-gray-600">
    {children}
  </p>
);

export const CardContent: React.FC<{children: React.ReactNode}> = ({ children }) => (
  <div>
    {children}
  </div>
);
