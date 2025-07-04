import React, {useEffect, useState} from "react";
import {Input} from "@/components/ui/input";
import {Button} from "@/components/ui/button";
import {useMutation, useQuery, useQueryClient} from "react-query";
import {useSelector} from "react-redux";
import {RootState} from "@/store";
import {AddOrderFormProps} from "@/model/Props";
import {SimpleProduct} from "@/model/Interfaces";
import {RegisterOrdersFormData} from "@/model/FormData";
import {useFieldArray, useForm} from "react-hook-form";
import {zodResolver} from "@hookform/resolvers/zod";
import {registerOrdersFormSchema} from "@/utils/orderValidation";
import {registerOrdersRequest} from "@/services/orderService";
import {toast} from "sonner";
import {AxiosError} from "axios";
import {Form, FormControl, FormField, FormItem, FormLabel, FormMessage} from "@/components/ui/form";
import {Accordion, AccordionContent, AccordionItem, AccordionTrigger} from "@/components/ui/accordion";
import {Select, SelectContent, SelectItem, SelectTrigger, SelectValue} from "@/components/ui/select";
import {Icons} from "@/public/icons";
import {Textarea} from "@/components/ui/textarea";
import {fetchSimpleProducts} from "@/services/productService";

export function AddOrderForm({guestTabId, parentOrderId, onSuccess}: AddOrderFormProps) {
    const queryClient = useQueryClient();
    const waiterEmail = useSelector((state: RootState) => state.auth.email)
    const [activeAccordionItem, setActiveAccordionItem] = useState<string>("item-0");

    const {data: products, isLoading: isLoadingProducts} = useQuery<SimpleProduct[]>(
        ['simpleProducts'],
        fetchSimpleProducts
    );

    const form = useForm<RegisterOrdersFormData>({
        resolver: zodResolver(registerOrdersFormSchema),
        defaultValues: {
            orders: [{productId: '', amount: 1, observation: ''}],
        },
        mode: 'onBlur',
    });

    const {fields, append, remove} = useFieldArray({
        control: form.control,
        name: "orders",
    });

    const mutation = useMutation({
        mutationFn: registerOrdersRequest,
        onSuccess: () => {
            toast.success("Pedidos adicionados com sucesso!");
            queryClient.invalidateQueries(['guestTabs']);
            onSuccess(); // close pop-up
        },
        onError: (error: unknown) => {
            if (error instanceof AxiosError) {
                toast.error("Erro ao adicionar pedidos", {
                    description: error.response?.data?.message || "Ocorreu um erro inesperado.",
                });
            } else {
                toast.error("Erro ao adicionar pedidos", {
                    description: "Ocorreu um erro inesperado.",
                });
            }
        },
    });

    const onSubmit = (data: RegisterOrdersFormData) => {
        if (!waiterEmail) {
            console.log(waiterEmail)
            toast.error("Não foi possível identificar o garçom. Faça login novamente.");
            return;
        }

        const requestData: RegisterOrdersFormData = {
            guestTabId,
            parentOrderId,
            waiterEmail: waiterEmail,
            orders: data.orders.map(order => ({
                productId: order.productId,
                amount: order.amount,
                observation: order.observation,
            })),
        };
        mutation.mutate(requestData);
    };

    const handleAddMore = async () => {
        // Check first item before adding the next
        const lastIndex = fields.length - 1;
        const result = await form.trigger(`orders.${lastIndex}`);
        if (result) {
            append({productId: '', amount: 1, observation: ''});
            setActiveAccordionItem(`item-${lastIndex + 1}`);
        } else {
            toast.warning("Preencha o pedido atual antes de adicionar um novo.");
        }
    };

    useEffect(() => {
        form.watch(() => {
            // Additional form change logic
        });
    }, [form, form.watch]);

    return (
        <Form {...form}>
            <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-6">
                <Accordion
                    type="single"
                    collapsible
                    className="w-full"
                    value={activeAccordionItem}
                    onValueChange={setActiveAccordionItem}
                >
                    {fields.map((field, index) => {
                        const productValue = form.watch(`orders.${index}.productId`);
                        const selectedProduct = products?.find(p => p.id === productValue);
                        const triggerText = selectedProduct
                            ? `Pedido ${index + 1}: ${selectedProduct.name}`
                            : `Pedido ${index + 1}`;

                        return (
                            <AccordionItem value={`item-${index}`} key={field.id}>
                                <AccordionTrigger>{triggerText}</AccordionTrigger>
                                <AccordionContent>
                                    <div className="grid gap-4 p-1">
                                        <FormField
                                            control={form.control}
                                            name={`orders.${index}.productId`}
                                            render={({field}) => (
                                                <FormItem>
                                                    <FormLabel>Produto</FormLabel>
                                                    <Select onValueChange={field.onChange} defaultValue={field.value}>
                                                        <FormControl>
                                                            <SelectTrigger disabled={isLoadingProducts}>
                                                                <SelectValue
                                                                    placeholder={isLoadingProducts ? "Carregando..." : "Selecione um produto"}/>
                                                            </SelectTrigger>
                                                        </FormControl>
                                                        <SelectContent>
                                                            {products?.map(product => (
                                                                <SelectItem key={product.id}
                                                                            value={product.id}>{product.name}</SelectItem>
                                                            ))}
                                                        </SelectContent>
                                                    </Select>
                                                    <FormMessage/>
                                                </FormItem>
                                            )}
                                        />
                                        <FormField
                                            control={form.control}
                                            name={`orders.${index}.amount`}
                                            render={({field}) => (
                                                <FormItem>
                                                    <FormLabel>Quantidade</FormLabel>
                                                    <FormControl>
                                                        <Input
                                                            type="number"
                                                            {...field}
                                                            onChange={e => field.onChange(parseInt(e.target.value, 10) || 1)}
                                                        />
                                                    </FormControl>
                                                    <FormMessage/>
                                                </FormItem>
                                            )}
                                        />
                                        <FormField
                                            control={form.control}
                                            name={`orders.${index}.observation`}
                                            render={({field}) => (
                                                <FormItem>
                                                    <FormLabel>Observação</FormLabel>
                                                    <FormControl>
                                                        <Textarea
                                                            placeholder="Ex: Sem cebola, ponto da carne, etc." {...field} />
                                                    </FormControl>
                                                    <FormMessage/>
                                                </FormItem>
                                            )}
                                        />
                                        {fields.length > 1 && (
                                            <Button type="button" variant="destructive" size="sm"
                                                    onClick={() => remove(index)}>
                                                Remover Pedido {index + 1}
                                            </Button>
                                        )}
                                    </div>
                                </AccordionContent>
                            </AccordionItem>
                        );
                    })}
                </Accordion>

                <div className="flex flex-col sm:flex-row gap-2">
                    <Button type="button" variant="outline" onClick={handleAddMore} className="w-full">
                        Adicionar mais um pedido
                    </Button>
                    <Button type="submit" disabled={mutation.isLoading} className="w-full">
                        {mutation.isLoading ? <Icons.spinner className="animate-spin"/> : 'Salvar Pedidos'}
                    </Button>
                </div>
            </form>
        </Form>
    );
}
