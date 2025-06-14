import { z } from 'zod'

export const loginSchema = z.object({
    email: z.string().email({ message: 'Formato de email inválido' }).min(1, { message: 'O email é obrigatório' }),
    password: z.string()
        .min(8, { message: 'A senha deve ter ao menos 8 caracteres' })
        .regex(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[-_#@$%^&+=]).{8,}$/, { message: 'A senha deve ter ao menos uma letra maiúscula, uma minúscula, um número, e um caractere especial (-, _, #, @, $, etc.)' }),
})
