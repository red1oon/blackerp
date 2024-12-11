import { Suspense, ReactNode } from 'react';
import { LoadingPage } from './LoadingPage';

interface Props {
  children: ReactNode;
  fallback?: ReactNode;
}

export function SuspenseWrapper({ children, fallback = <LoadingPage /> }: Props) {
  return <Suspense fallback={fallback}>{children}</Suspense>;
}
