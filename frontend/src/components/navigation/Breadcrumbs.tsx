import React from 'react';
import { Link, useLocation } from 'react-router-dom';
import { ChevronRight } from 'lucide-react';

export function Breadcrumbs() {
  const location = useLocation();

  const pathSegments = location.pathname.split('/')
    .filter(segment => segment !== '');

  const breadcrumbs = pathSegments.map((segment, index) => {
    const path = `/${pathSegments.slice(0, index + 1).join('/')}`;
    const label = segment.charAt(0).toUpperCase() + segment.slice(1);

    return { path, label };
  });

  return breadcrumbs.length > 0 ? (
    <div className="flex items-center space-x-2 text-sm text-gray-600">
      <Link to="/" className="hover:text-gray-900">
        Home
      </Link>
      {breadcrumbs.map((crumb, index) => (
        <React.Fragment key={crumb.path}>
          <ChevronRight className="h-4 w-4" />
          <Link
            to={crumb.path}
            className={index === breadcrumbs.length - 1
              ? "font-medium text-gray-900"
              : "hover:text-gray-900"
            }
          >
            {crumb.label}
          </Link>
        </React.Fragment>
      ))}
    </div>
  ) : null;
}
