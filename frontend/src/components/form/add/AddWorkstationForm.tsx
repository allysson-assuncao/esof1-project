import React, {useState} from "react";
import {cn} from "@/lib/utils";
import {Label} from "@/components/ui/label";
import {Input} from "@/components/ui/input";
import {Button} from "@/components/ui/button";
import {MultiSelect} from "@/components/ui/multi-select";
import {useMutation, useQuery} from "react-query";
import {SimpleCategory} from "@/model/Interfaces";
import {fetchSimpleCategories} from "@/services/categoryService";
import {useRouter} from "next/navigation";
import {useDispatch} from "react-redux";
import {useForm} from "react-hook-form";
import {zodResolver} from "@hookform/resolvers/zod";
import {registerSchema, workstationRegisterSchema} from "@/utils/authValidation";
import {Card, CardContent, CardDescription, CardHeader, CardTitle} from "@/components/ui/card";
import {toast} from "sonner";
import {AxiosError} from "axios";
import {Form, FormControl, FormField, FormItem, FormLabel, FormMessage} from "@/components/ui/form";
import {registerRequest} from "@/services/workstationService";
import {WorkstationRegisterFormData} from "@/model/FormData";
import {Icons} from "@/public/icons";

export function AddWorkstationForm({className, onSubmit}: { className?: string; onSubmit?: (data: unknown) => void }) {
    const router = useRouter();
    const dispatch = useDispatch();

    const form = useForm<WorkstationRegisterFormData>({
        resolver: zodResolver(workstationRegisterSchema),
        defaultValues: {
            name: '',
            categoryIds: []
        },
        mode: 'onBlur',
    })

    const {data: simpleCategories, isLoading, error} = useQuery<SimpleCategory[]>(
        ["simpleCategories"], fetchSimpleCategories
    );

    const mutation = useMutation(registerRequest, {
        onSuccess: (data) => {
            router.push('/dashboard/table/grid')

            toast.success("Estação cadastrada com sucesso!", {
                description: `${data.workstationName}!`,
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

    const handleFormSubmit = (data: WorkstationRegisterFormData) => {
        if (onSubmit) {
            onSubmit(data);
        } else {
            mutation.mutate(data);
        }
    }

    return(
        <div className="flex flex-col items-center mt-10 space-y-8 md:space-y-6">
            <Card className="w-full md:max-w-[700px] lg:max-w-[900px] mx-auto">
                <CardHeader className="space-y-1">
                    <CardTitle className="text-2xl">Nova estação de trabalho</CardTitle>
                    <CardDescription>
                        Informe o nome e as categorias pertencentes à estação
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
                                            <FormLabel>Nome da estação</FormLabel>
                                            <FormControl>
                                                <Input placeholder="Ex.: Cozinha, Bar..." {...field} />
                                            </FormControl>
                                            <FormMessage/>
                                        </FormItem>
                                    )}
                                />
                                
                                <FormField
                                    control={form.control}
                                    name="categoryIds"
                                    render={({field}) => (
                                        <FormItem>
                                            <FormLabel>Categorias</FormLabel>
                                            <FormControl>
                                                {isLoading ? (
                                                    <div className="flex items-center justify-center p-4 border rounded-md bg-muted/50">
                                                        <Icons.spinner className="mr-2 h-4 w-4 animate-spin" />
                                                        Carregando categorias...
                                                    </div>
                                                ) : error ? (
                                                    <div className="flex items-center justify-center p-4 border rounded-md text-destructive bg-destructive/10">
                                                        Erro ao carregar categorias
                                                    </div>
                                                ) : (
                                                    <MultiSelect
                                                        options={simpleCategories?.map((category) => ({
                                                            label: category.name,
                                                            value: category.id,
                                                        })) || []}
                                                        onValueChange={field.onChange}
                                                        defaultValue={field.value}
                                                        placeholder="Selecione as categorias..."
                                                        variant="default"
                                                        animation={0.2}
                                                        maxCount={3}
                                                    />
                                                )}
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
                                        {mutation.isLoading ? <Icons.spinner className="animate-spin"/> : 'Cadastrar Estação'}
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