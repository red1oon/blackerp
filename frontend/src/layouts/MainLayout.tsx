import React from 'react';
import { Outlet, Navigate } from 'react-router-dom';
import { Navbar } from '../components/navigation/Navbar';
import { Breadcrumbs } from '../components/navigation/Breadcrumbs';
import { useAuth } from '../lib/auth/AuthContext';

export default function MainLayout() {
  const { isAuthenticated } = useAuth();

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <Navbar />
      <main className="container mx-auto py-6 px-4">
        <Breadcrumbs />
        <div className="mt-4">
          <Outlet />
        </div>
      </main>
    </div>
  );
}
