import React from "react";
import {useRouter} from "next/navigation";
import {useForm} from "react-hook-form";
import {LocalTableRegisterFormData} from "@/model/FormData";
import {zodResolver} from "@hookform/resolvers/zod";
import {localTableRegisterSchema} from "@/utils/authValidation";
import {useMutation} from "react-query";
import {toast} from "sonner";
import {AxiosError} from "axios";
import {registerLocalTable} from "@/services/localTableService";
import {Card, CardContent, CardDescription, CardHeader, CardTitle} from "@/components/ui/card";
import {Form, FormControl, FormField, FormItem, FormLabel, FormMessage} from "@/components/ui/form";
import {Input} from "@/components/ui/input";
import {Button} from "@/components/ui/button";
import {Icons} from "@/public/icons";

export function AddLocalTableForm({className, onSubmit}: {className?: string, onSubmit?: (data: unknown) => void}) {
    const router = useRouter()

    const form = useForm<LocalTableRegisterFormData>({
        resolver: zodResolver(localTableRegisterSchema),
        defaultValues: {
            number: 1,
        },
        mode: 'onBlur',
    })

    const mutation = useMutation(registerLocalTable, {
        onSuccess: (data) => {
            router.push('/dashboard/table/grid')

            toast.success("Mesa cadastrada com sucesso!", {
                description: `Mesa nº ${data.number}!`,
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

    const handleFormSubmit = (data: LocalTableRegisterFormData) => {
        if (onSubmit) {
            onSubmit(data);
        } else {
            mutation.mutate(data);
        }
    }

    return (
        <div className="flex flex-col items-center mt-10 space-y-8 md:space-y-6">
            <Card className="w-full md:max-w-[467px] lg:max-w-[600px] mx-auto">
                <CardHeader className="space-y-1">
                    <CardTitle className="text-2xl">Cadastrar nova mesa</CardTitle>
                    <CardDescription>
                        Informe o número da mesa que será cadastrada
                    </CardDescription>
                </CardHeader>
                <CardContent className="grid gap-4">
                    <Form {...form}>
                        <form onSubmit={form.handleSubmit(handleFormSubmit)}>
                            <div className="grid gap-4 sm:gap-6">
                                <FormField
                                    control={form.control}
                                    name="number"
                                    render={({field}) => (
                                        <FormItem>
                                            <FormLabel>Número da mesa</FormLabel>
                                            <FormControl>
                                                <Input 
                                                    type="number" 
                                                    placeholder="1" 
                                                    min="1"
                                                    max="999"
                                                    {...field}
                                                    onChange={(e) => field.onChange(parseInt(e.target.value) || 1)}
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
                                        {mutation.isLoading ? <Icons.spinner className="animate-spin"/> : 'Cadastrar Mesa'}
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