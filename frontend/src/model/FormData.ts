export interface LoginFormData {
    email: string;
    password: string;
}

export interface RegisterFormData {
    username: string;
    email: string;
    password: string;
    name: string;
    role: string;
}

export interface WorkstationRegisterFormData {
    name: string;
    categoryIds: string[];
}

export interface LocalTableRegisterFormData {
    number: number;
}

export interface GuestTabRegisterFormData {
    localTableId: string;
    guestName: string;
}