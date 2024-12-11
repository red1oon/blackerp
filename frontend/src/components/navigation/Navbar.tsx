import React from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button } from '../ui/button';
import { Shield, Database, LogOut } from 'lucide-react';
import { useAuth } from '../../lib/auth/AuthContext';

export function Navbar() {
  const location = useLocation();
  const navigate = useNavigate();
  const { logout } = useAuth();

  const isActive = (path: string) => location.pathname === path;

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <nav className="bg-white shadow">
      <div className="container mx-auto px-4">
        <div className="flex h-16 justify-between items-center">
          <div className="flex items-center">
            <Link to="/" className="text-xl font-bold text-gray-800">
              BlackERP
            </Link>
            <div className="ml-10 flex items-center space-x-4">
              <Link to="/tables">
                <Button
                  variant={isActive('/tables') ? 'default' : 'ghost'}
                  className="flex items-center"
                >
                  <Database className="h-4 w-4 mr-2" />
                  Tables
                </Button>
              </Link>
              <Link to="/security">
                <Button
                  variant={isActive('/security') ? 'default' : 'ghost'}
                  className="flex items-center"
                >
                  <Shield className="h-4 w-4 mr-2" />
                  Security
                </Button>
              </Link>
            </div>
          </div>
          <Button variant="ghost" onClick={handleLogout}>
            <LogOut className="h-4 w-4 mr-2" />
            Logout
          </Button>
        </div>
      </div>
    </nav>
  );
}
