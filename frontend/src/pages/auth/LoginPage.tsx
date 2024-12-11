import React, { useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { Card, CardHeader, CardTitle, CardContent } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Alert, AlertDescription } from '@/components/ui/alert';
import { useAuth } from '@/lib/auth/AuthContext';
import { LoginCredentials } from '@/types/auth/types';
import { Loader2 } from 'lucide-react';

export default function LoginPage() {
    const navigate = useNavigate();
    const location = useLocation();
    const { login } = useAuth();
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    const from = location.state?.from?.pathname || '/';

    const [credentials, setCredentials] = useState<LoginCredentials>({
        username: '',
        password: '',
        clientId: '1' // Default client
    });

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setLoading(true);
        setError(null);

        try {
            const response = await fetch('/api/auth/login', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(credentials)
            });

            if (!response.ok) {
                const error = await response.json();
                throw new Error(error.message || 'Login failed');
            }

            const data = await response.json();
            login(data.token);
            navigate(from, { replace: true });
        } catch (err) {
            setError(err instanceof Error ? err.message : 'Login failed');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="min-h-screen flex items-center justify-center bg-gray-50 py-12 px-4 sm:px-6 lg:px-8">
            <Card className="w-full max-w-md">
                <CardHeader>
                    <CardTitle className="text-center text-2xl">
                        BlackERP Login
                    </CardTitle>
                </CardHeader>
                <CardContent>
                    {error && (
                        <Alert variant="destructive" className="mb-4">
                            <AlertDescription>{error}</AlertDescription>
                        </Alert>
                    )}

                    <form onSubmit={handleSubmit} className="space-y-6">
                        <div>
                            <label htmlFor="username" className="block text-sm font-medium text-gray-700">
                                Username
                            </label>
                            <input
                                id="username"
                                type="text"
                                required
                                className="mt-1 block w-full rounded-md border border-gray-300 px-3 py-2"
                                value={credentials.username}
                                onChange={e => setCredentials({
                                    ...credentials,
                                    username: e.target.value
                                })}
                            />
                        </div>

                        <div>
                            <label htmlFor="password" className="block text-sm font-medium text-gray-700">
                                Password
                            </label>
                            <input
                                id="password"
                                type="password"
                                required
                                className="mt-1 block w-full rounded-md border border-gray-300 px-3 py-2"
                                value={credentials.password}
                                onChange={e => setCredentials({
                                    ...credentials,
                                    password: e.target.value
                                })}
                            />
                        </div>

                        <div>
                            <label htmlFor="clientId" className="block text-sm font-medium text-gray-700">
                                Client
                            </label>
                            <select
                                id="clientId"
                                className="mt-1 block w-full rounded-md border border-gray-300 px-3 py-2"
                                value={credentials.clientId}
                                onChange={e => setCredentials({
                                    ...credentials,
                                    clientId: e.target.value
                                })}
                            >
                                <option value="1">System</option>
                                {/* Add more clients as needed */}
                            </select>
                        </div>

                        <Button
                            type="submit"
                            className="w-full"
                            disabled={loading}
                        >
                            {loading && <Loader2 className="mr-2 h-4 w-4 animate-spin" />}
                            Sign in
                        </Button>
                    </form>
                </CardContent>
            </Card>
        </div>
    );
}
