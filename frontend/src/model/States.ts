export interface AuthState {
    username: string;
    email: string,
    token: string | null;
    role: string;
    isAuthenticated: boolean;
}
