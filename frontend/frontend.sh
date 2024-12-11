#!/bin/bash

# Update App.tsx with error boundary and suspense integration
cat > "./src/App.tsx" << 'EOF'
import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './lib/auth/AuthContext';
import { ProtectedRoute } from './components/navigation/ProtectedRoute';
import MainLayout from './layouts/MainLayout';
import LoginPage from './pages/auth/LoginPage';
import TableManagement from './components/tables/TableManagement';
import { SecurityAdmin } from './components/security';
import ErrorBoundary from './components/error/ErrorBoundary';
import { SuspenseWrapper } from './components/loading/SuspenseWrapper';

function App() {
  return (
    <ErrorBoundary>
      <AuthProvider>
        <SuspenseWrapper>
          <BrowserRouter>
            <Routes>
              <Route path="/login" element={<LoginPage />} />
              <Route element={<MainLayout />}>
                <Route
                  path="/"
                  element={
                    <ProtectedRoute>
                      <Navigate to="/tables" replace />
                    </ProtectedRoute>
                  }
                />
                <Route
                  path="/security"
                  element={
                    <ProtectedRoute>
                      <SecurityAdmin />
                    </ProtectedRoute>
                  }
                />
                <Route
                  path="/tables"
                  element={
                    <ProtectedRoute>
                      <TableManagement />
                    </ProtectedRoute>
                  }
                />
              </Route>
              <Route path="*" element={<div>404 Not Found</div>} />
            </Routes>
          </BrowserRouter>
        </SuspenseWrapper>
      </AuthProvider>
    </ErrorBoundary>
  );
}

export default App;
EOF

echo "App.tsx updated successfully with error boundary and suspense integration"