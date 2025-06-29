import { z } from 'zod'
import {UserRoles} from "@/model/Interfaces";

export const loginSchema = z.object({
    email: z.string().email({ message: 'Formato de email inválido' }).min(1, { message: 'O email é obrigatório' }),
    password: z.string()
        .min(8, { message: 'A senha deve ter ao menos 8 caracteres' })
        .regex(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[-_#@$%^&+=]).{8,}$/, { message: 'A senha deve ter ao menos uma letra maiúscula, uma minúscula, um número, e um caractere especial (-, _, #, @, $, etc.)' }),
})

export const registerSchema = z.object({
    username: z.string()
        .min(4, { message: 'O nome de usuário deve ter ao menos 4 caracteres' }),
    email: z.string().email({ message: 'Formato de email inválido' })
        .min(1, { message: 'O email é obrigatório' }),
    password: z.string()
        .min(8, { message: 'A senha deve ter ao menos 8 caracteres' })
        .regex(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[-_#@$%^&+=]).{8,}$/, { message: 'A senha deve ter ao menos uma letra maiúscula, uma minúscula, um número, e um caractere especial (-, _, #, @, $, etc.)' }),
    name: z.string().min(1, { message: 'O nome é obrigatório' }),
    role: z.enum([UserRoles.COOK.value, UserRoles.CASHIER.value, UserRoles.ADMIN.value], { message: 'O cargo do usuário é obrigatório' }),
})

export const workstationRegisterSchema = z.object({
    name: z.string()
        .min(1, {message: 'O nome é obrigatório'}),
    categoryIds: z.array(z.string())
})

export const localTableRegisterSchema = z.object({
    number: z.number()
        .min(1, {message: 'O número da mesa deve ser maior que 0'})
        .max(999, {message: 'O número da mesa deve ser menor que 1000'})
})
