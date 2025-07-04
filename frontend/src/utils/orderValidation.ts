import {z} from "zod";

export const registerOrderItemSchema = z.object({
  productId: z.string().uuid({ message: "Selecione um produto válido." }),
  amount: z.number().min(1, { message: "A quantidade deve ser no mínimo 1." }),
  observation: z.string().optional(),
});

export const registerOrdersFormSchema = z.object({
  orders: z.array(registerOrderItemSchema)
    .min(1, { message: "É necessário adicionar pelo menos um pedido." }),
});
