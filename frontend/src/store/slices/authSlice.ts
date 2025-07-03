import {createSlice, PayloadAction} from "@reduxjs/toolkit";
import {AuthState} from "@/model/States";

const tokenFromStorage = typeof window !== "undefined" ? localStorage.getItem('token') : null;
const roleFromStorage = typeof window !== "undefined" ? localStorage.getItem('role') ?? '' : '';
const emailFromStorage = typeof window !== "undefined" ? localStorage.getItem('email') ?? '' : '';

const initialState: AuthState = {
    username: '',
    email: emailFromStorage,
    token: tokenFromStorage,
    role: roleFromStorage,
    isAuthenticated: !!tokenFromStorage,
};

export const authSlice = createSlice({
    name: 'auth',
    initialState,
    reducers: {
        login: (state, action: PayloadAction<{ username: string; email: string; role: string; token: string; }>) => {
            state.username = action.payload.username;
            state.email = action.payload.email;
            state.token = action.payload.token;
            state.role = action.payload.role;
            state.isAuthenticated = true;

            localStorage.setItem('token', action.payload.token);
            localStorage.setItem('role', action.payload.role);
            localStorage.setItem('email', action.payload.email);
        },
        signup(state, action: PayloadAction<{ username: string; email: string; role: string; token: string; }>) {
            state.username = action.payload.username;
            state.email = action.payload.email;
            state.token = action.payload.token;
            state.role = action.payload.role;
            state.isAuthenticated = true;

            localStorage.setItem('token', action.payload.token);
            localStorage.setItem('role', action.payload.role);
            localStorage.setItem('email', action.payload.email);
        },
        logout: (state) => {
            state.username = '';
            state.token = null;
            state.role = '';
            state.isAuthenticated = false;

            localStorage.removeItem('token');
            localStorage.removeItem('role');
            localStorage.removeItem('email');
            localStorage.removeItem("profile");
        },
    },
});

export const { login, signup, logout } = authSlice.actions;
