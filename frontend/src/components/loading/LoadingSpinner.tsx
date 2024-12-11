import { Loader2 } from 'lucide-react';

interface Props {
  className?: string;
}

export function LoadingSpinner({ className = "h-4 w-4" }: Props) {
  return <Loader2 className={`animate-spin ${className}`} />;
}
