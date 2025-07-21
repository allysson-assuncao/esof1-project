import React, {useState} from "react";
import {Input} from "@/components/ui/input";
import {Button} from "@/components/ui/button";
import {useMutation, useQuery} from "react-query";
import {SimpleCategory} from "@/model/Interfaces";
import {fetchSimpleCategories, fetchSimpleProductEligibleCategories} from "@/services/categoryService";
import {useForm} from "react-hook-form";
import {zodResolver} from "@hookform/resolvers/zod";
import {productRegisterSchema} from "@/utils/authValidation";
import {Card, CardContent, CardDescription, CardHeader, CardTitle} from "@/components/ui/card";
import {toast} from "sonner";
import {AxiosError} from "axios";
import {Form, FormControl, FormField, FormItem, FormLabel, FormMessage} from "@/components/ui/form";
import {registerProduct} from "@/services/productService";
import {ProductRegisterFormData} from "@/model/FormData";
import {Icons} from "@/public/icons";
import {Textarea} from "@/components/ui/textarea";
import {Select, SelectContent, SelectItem, SelectTrigger, SelectValue} from "@/components/ui/select";

export function AddProductForm({className, onSubmit}: { className?: string; onSubmit?: (data: unknown) => void }) {

    const [priceInput, setPriceInput] = useState('0,00');
    // Initialize priceInput from default value
    React.useEffect(() => {
        const price = form.getValues('price');
        if (typeof price === 'number' && !isNaN(price)) {
            const cents = Math.round(price * 100);
            const reais = Math.floor(cents / 100);
            const centavos = (cents % 100).toString().padStart(2, '0');
            setPriceInput(`${reais},${centavos}`);
        }
    }, []);

    const form = useForm<ProductRegisterFormData>({
        resolver: zodResolver(productRegisterSchema),
        defaultValues: {
            name: '',
            description: '',
            price: 0.00,
            idCategory: ''
        },
        mode: 'onBlur',
    })

    const {data: simpleCategories, isLoading, error} = useQuery<SimpleCategory[]>(
        ["simpleCategories"], fetchSimpleProductEligibleCategories
    );

    const mutation = useMutation((data: any) => {
        console.log('mutation called')
        return registerProduct(data);
    }, {
        onSuccess: (data) => {

            toast.success("Produto cadastrado com sucesso!", {
                /*description: `${data.productName}!`,*/
            })
        },

        onError: (error: unknown) => {
            if (error instanceof AxiosError) {
                toast.error("Erro ao fazer o cadastro", {
                    description: error.response?.data?.message || 'Ocorreu um erro inesperado.',
                })
            } else {
                toast.error("Erro ao fazer o cadastro", {
                    description: 'Ocorreu um erro inesperado.',
                })
            }
        },
    })

    const handleFormSubmit = (data: ProductRegisterFormData) => {
        // Convert priceInput (string) to float for backend
        const normalized = priceInput.replace(/\./g, '').replace(',', '.');
        const price = parseFloat(normalized);
        const submitData = {...data, price};
        if (onSubmit) {
            onSubmit(submitData);
        } else {
            mutation.mutate(submitData);
        }
    }

    return (
        <div className="flex flex-col items-center mt-10 space-y-8 md:space-y-6">
            <Card className="w-full md:max-w-[467px] lg:max-w-[600px] mx-auto">
                <CardHeader className="space-y-1">
                    <CardTitle className="text-2xl">Cadastro de novo produto</CardTitle>
                    <CardDescription>
                        Informe o nome, preço, descrição e categoria do produto
                    </CardDescription>
                </CardHeader>
                <CardContent className="grid gap-4">
                    <Form {...form}>
                        <form onSubmit={form.handleSubmit(handleFormSubmit)}>
                            <div className="grid gap-4 sm:gap-6">
                                <FormField
                                    control={form.control}
                                    name="name"
                                    render={({field}) => (
                                        <FormItem>
                                            <FormLabel>Nome</FormLabel>
                                            <FormControl>
                                                <Input placeholder="Ex.: Podrão, Superdrink..." {...field} />
                                            </FormControl>
                                            <FormMessage/>
                                        </FormItem>
                                    )}
                                />

                                <FormField
                                    control={form.control}
                                    name="idCategory"
                                    render={({field}) => (
                                        <FormItem>
                                            <FormLabel>Categoria</FormLabel>
                                            <FormControl>
                                                <Select
                                                    onValueChange={field.onChange}
                                                    defaultValue={field.value}
                                                    disabled={isLoading}
                                                >
                                                    <SelectTrigger>
                                                        <SelectValue
                                                            placeholder={isLoading ? "Carregando..." : "Selecione uma categoria..."}/>
                                                    </SelectTrigger>
                                                    <SelectContent>
                                                        {simpleCategories?.map((category) => (
                                                            <SelectItem key={category.id} value={category.id}>
                                                                {category.name}
                                                            </SelectItem>
                                                        ))}
                                                    </SelectContent>
                                                </Select>
                                            </FormControl>
                                            <FormMessage/>
                                        </FormItem>
                                    )}
                                />

                                <FormField
                                    control={form.control}
                                    name="price"
                                    render={({field}) => (
                                        <FormItem>
                                            <FormLabel>Preço</FormLabel>
                                            <FormControl>
                                                <>
                                                    <input
                                                        type="hidden"
                                                        {...form.register('price', {valueAsNumber: true})}
                                                        value={field.value}
                                                    />
                                                    <div className="flex items-center w-32">
                                                        <span
                                                            className="px-2 py-1 bg-muted border border-r-0 rounded-l-md text-gray-700">R$</span>
                                                        <Input
                                                            type="text"
                                                            inputMode="numeric"
                                                            pattern="[0-9,\.]*"
                                                            className="w-full border rounded-r-md p-2 focus:outline-none focus:ring-2 focus:ring-primary"
                                                            placeholder="0,00"
                                                            value={priceInput}
                                                            onChange={e => {
                                                                let v = e.target.value.replace(/\D/g, '');
                                                                if (v.length === 0) v = '000';
                                                                while (v.length < 3) v = '0' + v;
                                                                const reais = v.slice(0, -2);
                                                                const cents = v.slice(-2);
                                                                const formatted = `${parseInt(reais, 10).toString()},${cents}`;
                                                                setPriceInput(formatted);
                                                                // Update form value as number
                                                                const asNumber = parseFloat(`${parseInt(reais, 10)}.${cents}`);
                                                                form.setValue('price', asNumber, {shouldValidate: true});
                                                            }}
                                                        />
                                                    </div>
                                                </>
                                            </FormControl>
                                            <FormMessage/>
                                        </FormItem>
                                    )}
                                />

                                <FormField
                                    control={form.control}
                                    name="description"
                                    render={({field}) => (
                                        <FormItem>
                                            <FormLabel>Descrição</FormLabel>
                                            <FormControl>
                                                <Textarea
                                                    className="w-full border rounded-md p-2 min-h-[120px] resize-vertical"
                                                    placeholder="Descreva o produto..."
                                                    {...field}
                                                />
                                            </FormControl>
                                            <FormMessage/>
                                        </FormItem>
                                    )}
                                />

                                <div className="flex justify-center">
                                    <Button
                                        className="w-full justify-center touch-manipulation"
                                        type="submit"
                                        disabled={mutation.isLoading}
                                    >
                                        {mutation.isLoading ?
                                            <Icons.spinner className="animate-spin"/> : 'Cadastrar Produto'}
                                    </Button>
                                </div>
                            </div>
                        </form>
                    </Form>
                </CardContent>
            </Card>
        </div>
    );

}