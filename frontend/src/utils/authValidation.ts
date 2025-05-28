import { z } from 'zod'

export const testSchema = z.object({
    msg: z.string().min(1, { message: "A mensagem é obrigatória!" }),
})
