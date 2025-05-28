import {createSlice, PayloadAction} from "@reduxjs/toolkit";
import {AuthState} from "@/model/States";

const tokenFromStorage = typeof window !== "undefined" ? localStorage.getItem('token') : null;
const roleFromStorage = typeof window !== "undefined" ? localStorage.getItem('role') ?? '' : '';

const initialState: AuthState = {
    username: '',
    email: '',
    token: tokenFromStorage,
    role: roleFromStorage,
    isAuthenticated: !!tokenFromStorage,
};

export const authSlice = createSlice({
    name: 'auth',
    initialState,
    reducers: {
        login: (state, action: PayloadAction<{ username: string; token: string; role: string }>) => {
            state.username = action.payload.username;
            state.token = action.payload.token;
            state.role = action.payload.role;
            state.isAuthenticated = true;

            localStorage.setItem('token', action.payload.token);
            localStorage.setItem('role', action.payload.role);
        },
        signup(state, action: PayloadAction<{ username: string, token: string, role: string }>) {
            state.username = action.payload.username;
            state.token = action.payload.token;
            state.role = action.payload.role;
            state.isAuthenticated = true;

            localStorage.setItem('token', action.payload.token);
            localStorage.setItem('role', action.payload.role);
        },
        logout: (state) => {
            state.username = '';
            state.token = null;
            state.role = '';
            state.isAuthenticated = false;

            localStorage.removeItem('token');
            localStorage.removeItem('role');
            localStorage.removeItem("profile");
        },
    },
});

export const { login, signup, logout } = authSlice.actions;
