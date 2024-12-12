import { Button } from '@/components/ui/button';
import { useNavigate } from 'react-router-dom';
import {
  LayoutGrid,
  Table,
  Settings,
  Users
} from 'lucide-react';

const MainNavigation = () => {
  const navigate = useNavigate();

  return (
    <div className="p-4 space-y-2">
      <Button
        variant="ghost"
        className="w-full justify-start"
        onClick={() => navigate('/windows')}
      >
        <LayoutGrid className="w-4 h-4 mr-2" />
        Windows
      </Button>

      <Button
        variant="ghost"
        className="w-full justify-start"
        onClick={() => navigate('/tables')}
      >
        <Table className="w-4 h-4 mr-2" />
        Tables
      </Button>

      <Button
        variant="ghost"
        className="w-full justify-start"
        onClick={() => navigate('/security')}
      >
        <Users className="w-4 h-4 mr-2" />
        Security
      </Button>

      <Button
        variant="ghost"
        className="w-full justify-start"
        onClick={() => navigate('/settings')}
      >
        <Settings className="w-4 h-4 mr-2" />
        Settings
      </Button>
    </div>
  );
}

export default MainNavigation;
