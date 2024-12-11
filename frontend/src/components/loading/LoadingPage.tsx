import { LoadingSpinner } from './LoadingSpinner';

export function LoadingPage() {
  return (
    <div className="min-h-screen flex items-center justify-center">
      <LoadingSpinner className="h-8 w-8" />
    </div>
  );
}
