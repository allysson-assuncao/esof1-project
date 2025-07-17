import {z} from "zod";

export const registerPaymentFormSchema = z.object({
    items: z.array(z.object({
        paymentMethodId: z.string().nonempty("Selecione um método"),
        amount: z.coerce.number().min(0.01, "O valor deve ser positivo."),
    })).min(1, "Adicione ao menos um pagamento."),
});
