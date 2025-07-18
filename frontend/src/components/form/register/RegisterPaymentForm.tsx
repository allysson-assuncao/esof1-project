import {DisplayGuestTabItem, SimplePaymentMethod} from "@/model/Interfaces";
import {useMutation, useQuery, useQueryClient} from "react-query";
import {useMemo, useState} from "react";
import {fetchAllPaymentMethods} from "@/services/paymentMethodService";
import {useFieldArray, useForm} from "react-hook-form";
import {zodResolver} from "@hookform/resolvers/zod";
import {registerPaymentFormSchema} from "@/utils/paymentValidation";
import {registerPaymentRequest} from "@/services/paymentService";
import {toast} from "sonner";
import {Separator} from "@/components/ui/separator";
import {Form, FormControl, FormField, FormItem, FormLabel, FormMessage} from "@/components/ui/form";
import {Accordion, AccordionContent, AccordionItem, AccordionTrigger} from "@/components/ui/accordion";
import {Select, SelectContent, SelectItem, SelectTrigger, SelectValue} from "@/components/ui/select";
import {Input} from "@/components/ui/input";
import {Button} from "@/components/ui/button";
import {Icons} from "@/public/icons";
import {RegisterPaymentFormData} from "@/model/FormData";
import {AxiosError} from "axios";

interface RegisterPaymentFormProps {
    guestTab: DisplayGuestTabItem;
    onSuccess: () => void;
}

const formatCurrency = (value: number) => new Intl.NumberFormat("pt-BR", {
    style: "currency",
    currency: "BRL"
}).format(value);

export function RegisterPaymentForm({guestTab, onSuccess}: RegisterPaymentFormProps) {
    const queryClient = useQueryClient();
    const [activeAccordionItem, setActiveAccordionItem] = useState<string>("item-0");

    const paymentInfo = guestTab.payment;

    const {data: paymentMethods, isLoading: isLoadingMethods} = useQuery<SimplePaymentMethod[]>(
        'paymentMethods',
        fetchAllPaymentMethods
    );

    const form = useForm<RegisterPaymentFormData>({
        resolver: zodResolver(registerPaymentFormSchema),
        defaultValues: {
            items: [{paymentMethodId: '', amount: 0}],
        },
        mode: 'onBlur',
    });

    const {fields, append, remove} = useFieldArray({
        control: form.control,
        name: "items",
    });

    const watchedItems = form.watch('items');
    const totalPaid = useMemo(() =>
            watchedItems.reduce((acc, item) => acc + (Number(item.amount) || 0), 0),
        [watchedItems]
    );
    const remainingAmount = !paymentInfo ? 0 : paymentInfo.totalAmount - totalPaid;

    const mutation = useMutation(registerPaymentRequest, {
        onSuccess: () => {
            toast.success("Pagamento registrado com sucesso!");
            queryClient.invalidateQueries(['guestTabs']);
            onSuccess();
        },
        onError: (error: unknown) => {
            if (error instanceof AxiosError) {
                toast.error("Erro ao registrar pagamento", {
                    description: error.response?.data?.message || "Ocorreu um erro inesperado.",
                });
            } else {
                toast.error("Erro ao registrar pagamento", {
                    description: 'Ocorreu um erro inesperado.',
                })
            }
        },
    });

    if (!paymentInfo) {
        return <div>Erro: Informações de pagamento não encontradas para esta comanda.</div>;
    }

    const onSubmit = (data: RegisterPaymentFormData) => {
        if (totalPaid > paymentInfo.totalAmount) {
            toast.error("O valor total pago não pode ser maior que o valor da comanda.");
            return;
        }

        const requestData = {
            individualPayments: data.items.map(item => ({
                paymentMethodId: Number(item.paymentMethodId),
                amount: item.amount,
            })),
        };

        mutation.mutate({paymentId: paymentInfo.id, data: requestData});
    };

    const handleAddMore = () => {
        append({paymentMethodId: '', amount: 0 /*Math.max(0, remainingAmount)*/});
        setActiveAccordionItem(`item-${fields.length}`);
    };

    return (
        <>
            <div className="p-4 mb-4 border rounded-lg bg-muted/50">
                <h3 className="font-bold text-lg mb-2">Resumo da Comanda #{guestTab.id}</h3>
                <div className="space-y-1 text-sm">
                    <div className="flex justify-between"><span>Total da Comanda:</span> <span
                        className="font-semibold">{formatCurrency(paymentInfo.totalAmount)}</span></div>
                    <div className="flex justify-between text-green-600"><span>Total Pago:</span> <span
                        className="font-semibold">{formatCurrency(totalPaid)}</span></div>
                    <Separator className="my-2"/>
                    <div
                        className={`flex justify-between font-bold ${remainingAmount > 0 ? 'text-red-600' : 'text-blue-600'}`}>
                        <span>Restante:</span> <span>{formatCurrency(remainingAmount)}</span></div>
                </div>
            </div>

            <Form {...form}>
                <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-6">
                    <Accordion type="single" collapsible className="w-full" value={activeAccordionItem}
                               onValueChange={setActiveAccordionItem}>
                        {fields.map((field, index) => (
                            <AccordionItem value={`item-${index}`} key={field.id}>
                                <AccordionTrigger>Pagamento {index + 1}</AccordionTrigger>
                                <AccordionContent>
                                    <div className="grid gap-4 p-1">
                                        <FormField
                                            control={form.control}
                                            name={`items.${index}.paymentMethodId`}
                                            render={({field}) => (
                                                <FormItem>
                                                    <FormLabel>Método</FormLabel>
                                                    <Select onValueChange={field.onChange} defaultValue={field.value}>
                                                        <FormControl>
                                                            <SelectTrigger disabled={isLoadingMethods}>
                                                                <SelectValue
                                                                    placeholder={isLoadingMethods ? "Carregando..." : "Selecione um método"}/>
                                                            </SelectTrigger>
                                                        </FormControl>
                                                        <SelectContent>
                                                            {paymentMethods?.map(method => (
                                                                <SelectItem key={method.id}
                                                                            value={String(method.id)}>{method.name}</SelectItem>
                                                            ))}
                                                        </SelectContent>
                                                    </Select>
                                                    <FormMessage/>
                                                </FormItem>
                                            )}
                                        />
                                        <FormField
                                            control={form.control}
                                            name={`items.${index}.amount`}
                                            render={({field}) => (
                                                <FormItem>
                                                    <FormLabel>Valor</FormLabel>
                                                    <FormControl>
                                                        <Input type="number" step="0.01" {...field} />
                                                    </FormControl>
                                                    <FormMessage/>
                                                </FormItem>
                                            )}
                                        />
                                        {fields.length > 1 && (
                                            <Button type="button" variant="destructive" size="sm"
                                                    onClick={() => remove(index)}>
                                                Remover Pagamento {index + 1}
                                            </Button>
                                        )}
                                    </div>
                                </AccordionContent>
                            </AccordionItem>
                        ))}
                    </Accordion>

                    <div className="flex flex-col sm:flex-row gap-2">
                        <Button type="button" variant="outline" onClick={handleAddMore} className="w-full"
                                disabled={remainingAmount <= 0}>
                            Adicionar outro pagamento
                        </Button>
                        <Button type="submit" disabled={mutation.isLoading} className="w-full">
                            {mutation.isLoading ? <Icons.spinner className="animate-spin"/> : 'Confirmar Pagamento'}
                        </Button>
                    </div>
                </form>
            </Form>
        </>
    );
}
