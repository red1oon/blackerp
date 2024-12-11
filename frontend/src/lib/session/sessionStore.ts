import { create } from '@/lib/utils/store';

interface SessionStore {
  token: string | null;
  setToken: (token: string) => void;
  clearToken: () => void;
}

export const useSessionStore = create<SessionStore>((set) => ({
  token: localStorage.getItem('token'),
  setToken: (token: string) => {
    localStorage.setItem('token', token);
    set({ token });
  },
  clearToken: () => {
    localStorage.removeItem('token');
    set({ token: null });
  },
}));
