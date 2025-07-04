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

export const productRegisterSchema = z.object({
    name: z.string()
        .min(3, {message: 'O nome é obrigatório e deve ter pelo menos 3 caracteres'}),
    description: z.string(),
    price: z.number()
        .min(0.01)
        .max(99999.99),
    idCategory: z.string().nonempty('Escolha uma categoria'),
})

export const localTableRegisterSchema = z.object({
    number: z.number()
        .min(1, {message: 'O número da mesa deve ser maior que 0'})
        .max(999, {message: 'O número da mesa deve ser menor que 1000'})
})

export const guestTabRegisterSchema = z.object({
    guestName: z.string()
        .min(1, {message: 'Nome do cliente é obrigatório'})
})

export const categoryRegisterSchema = z.object({
    name: z.string().min(1, "Nome é obrigatório"),
    isMultiple: z.boolean(),
    subcategories: z.array(z.string().min(1, "Subcategoria não pode ser vazia")),
    workstationId: z.string().uuid("ID da estação inválido"),
});

export type CategoryFormData = z.infer<typeof categoryRegisterSchema>;
