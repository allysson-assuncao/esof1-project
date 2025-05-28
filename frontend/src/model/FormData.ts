export interface LoginFormData {
    email: string;
    password: string;
}

export interface RegisterFormData {
    username: string;
    email: string;
    password: string;
    role: string;
    name: string;
    enrollmentRegister?: string;
    institutionId: string;
    studentClassName: string,
}
