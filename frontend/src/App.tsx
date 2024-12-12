import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './lib/auth/AuthContext';
import { ProtectedRoute } from './components/navigation/ProtectedRoute';
import MainLayout from './layouts/MainLayout';
import LoginPage from './pages/auth/LoginPage';
import TableManagement from './components/tables/TableManagement';
import WindowViewer from './components/windows/WindowViewer';
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
                      <Navigate to="/windows" replace />
                    </ProtectedRoute>
                  }
                />
                <Route
                  path="/windows"
                  element={
                    <ProtectedRoute>
                      <WindowViewer />
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
