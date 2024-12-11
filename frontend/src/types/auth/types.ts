export interface LoginCredentials {
    username: string;
    password: string;
    clientId: string;
}

export interface LoginResponse {
    token: string;
    user: {
        id: string;
        username: string;
        email: string;
        roles: string[];
    };
}

export interface AuthError {
    message: string;
    code?: string;
}
