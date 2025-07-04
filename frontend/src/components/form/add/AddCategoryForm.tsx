import React from "react";
import {useForm, useFieldArray} from "react-hook-form";
import {zodResolver} from "@hookform/resolvers/zod";
import {useMutation, useQuery, useQueryClient} from "react-query";
import {useRouter} from "next/navigation";
import {toast} from "sonner";
import {AxiosError} from "axios";

import {categoryRegisterSchema} from "@/utils/authValidation";
import {CategoryFormData} from "@/model/FormData";
import {fetchRootCategories, fetchSimpleCategories, registerCategoryService} from "@/services/categoryService";
import {
    Card, CardHeader, CardTitle, CardDescription, CardContent
} from "@/components/ui/card";
import {
    Form, FormField, FormItem, FormLabel, FormControl, FormMessage
} from "@/components/ui/form";
import {Input} from "@/components/ui/input";
import {Button} from "@/components/ui/button";
import {Switch} from "@/components/ui/switch";
import {Icons} from "@/public/icons";
import {Select, SelectContent, SelectItem, SelectTrigger, SelectValue} from "@/components/ui/select";
import {fetchWorkstations} from "@/services/workstationService";

export function AddCategoryForm({className, onSubmit}: {className?: string, onSubmit?: (data: unknown) => void}) {
    const router = useRouter();
    const queryClient = useQueryClient();

    const form = useForm<CategoryFormData>({
        resolver: zodResolver(categoryRegisterSchema),
        defaultValues: {
            name: "",
            isMultiple: false,
            subcategories: [""],
            workstationId: "",
        },
        mode: "onBlur",
    });

    const {fields, append, remove} = useFieldArray({
        control: form.control,
        name: "subcategories"
    });

    const mutation = useMutation(registerCategoryService, {
        onSuccess: (data) => {
            queryClient.invalidateQueries(["categories"]);
            // router.push("/dashboard/category/grid"); // remova ou comente essa linha
            form.reset(); // limpa o formulário
            toast.success("Categoria cadastrada com sucesso!", {
                description: `Categoria: ${data.name}`,
            });
        },
        onError: (error: unknown) => {
            if (error instanceof AxiosError) {
                toast.error("Erro ao cadastrar", {
                    description: error.response?.data?.message || "Erro inesperado.",
                });
            } else {
                toast.error("Erro ao cadastrar", {
                    description: "Erro inesperado.",
                });
            }
        },
    });


    const handleFormSubmit = (data: CategoryFormData) => {
        if (onSubmit) onSubmit(data);
        else mutation.mutate(data);
    };

    const { data: rootCategories = [] } = useQuery("root-categories", fetchRootCategories);
    const { data: workstations = [] } = useQuery("workstations", fetchWorkstations);

    return (
        <div className="flex flex-col items-center mt-10 space-y-8 md:space-y-6">
            <Card className="w-full md:max-w-[700px] lg:max-w-[900px] mx-auto">
                <CardHeader>
                    <CardTitle className="text-2xl">Nova categoria</CardTitle>
                    <CardDescription>Preencha os dados da categoria</CardDescription>
                </CardHeader>
                <CardContent className="grid gap-4">
                    <Form {...form}>
                        <form onSubmit={form.handleSubmit(handleFormSubmit)}>
                            <div className="grid gap-4 sm:gap-6">

                                {/* Nome + isMultiple lado a lado centralizados verticalmente */}
                                <div className="flex items-center gap-6">
                                    {/* Nome */}
                                    <div className="flex-1">
                                        <FormField
                                            control={form.control}
                                            name="name"
                                            render={({ field }) => (
                                                <FormItem>
                                                    <FormLabel>Nome</FormLabel>
                                                    <FormControl>
                                                        <Input placeholder="Ex: Bebidas" {...field} />
                                                    </FormControl>
                                                    <FormMessage />
                                                </FormItem>
                                            )}
                                        />
                                    </div>

                                    {/* isMultiple */}
                                    <FormField
                                        control={form.control}
                                        name="isMultiple"
                                        render={({ field }) => (
                                            <FormItem className="flex items-center gap-2 mt-6">
                                                <FormLabel className="text-base m-0">Múltipla seleção</FormLabel>
                                                <FormControl className="p-0 m-0">
                                                    <div className="m-0 p-0">
                                                        <Switch
                                                            checked={field.value}
                                                            onCheckedChange={field.onChange}
                                                            className="m-0 p-0"
                                                        />
                                                    </div>
                                                </FormControl>
                                            </FormItem>
                                        )}
                                    />
                                </div>

                                {/* workstationId */}
                                <FormField
                                    control={form.control}
                                    name="workstationId"
                                    render={({ field }) => (
                                        <FormItem>
                                            <FormLabel>Estação de trabalho</FormLabel>
                                            <FormControl>
                                                <Select
                                                    value={field.value || ""}
                                                    onValueChange={field.onChange}
                                                    className="w-full max-w-[300px]"
                                                >
                                                    <SelectTrigger>
                                                        <SelectValue placeholder="Selecione uma estação" />
                                                    </SelectTrigger>
                                                    <SelectContent>
                                                        {workstations.map((ws) => (
                                                            <SelectItem key={ws.id} value={ws.id}>
                                                                {ws.name}
                                                            </SelectItem>
                                                        ))}
                                                    </SelectContent>
                                                </Select>
                                            </FormControl>
                                            <FormMessage />
                                        </FormItem>
                                    )}
                                />

                                {/* Subcategorias */}
                                <div className="space-y-4">
                                    <FormLabel>Subcategorias</FormLabel>

                                    {fields.map((field, index) => {
                                        const inputName = `subcategories.${index}`;

                                        return (
                                            <FormField
                                                key={field.id}
                                                control={form.control}
                                                name={inputName}
                                                render={({ field }) => (
                                                    <FormItem className="flex items-end gap-3">
                                                        <FormControl className="flex-shrink-0 w-[250px]">
                                                            <Input
                                                                placeholder={`Ex.: Drink`}
                                                                {...field}
                                                                onChange={(e) => field.onChange(e.target.value)}
                                                                value={field.value || ""}
                                                            />
                                                        </FormControl>

                                                        <Select
                                                            value=""
                                                            onValueChange={(val) => form.setValue(inputName, val)}
                                                        >
                                                            <SelectTrigger className="w-[40px]" />
                                                            <SelectContent>
                                                                {rootCategories.map((cat) => (
                                                                    <SelectItem key={cat.id} value={cat.name}>
                                                                        {cat.name}
                                                                    </SelectItem>
                                                                ))}
                                                            </SelectContent>
                                                        </Select>

                                                        <Button
                                                            type="button"
                                                            variant="destructive"
                                                            size="icon"
                                                            onClick={() => remove(index)}
                                                            className="ml-auto"
                                                        >
                                                            ✕
                                                        </Button>

                                                        <FormMessage />
                                                    </FormItem>
                                                )}
                                            />
                                        );
                                    })}

                                    <div className="mt-2">
                                        <Button
                                            type="button"
                                            variant="outline"
                                            onClick={() => append("")}
                                        >
                                            + Adicionar Subcategoria
                                        </Button>
                                    </div>
                                </div>

                                {/* Botão de envio */}
                                <div className="flex justify-center">
                                    <Button
                                        type="submit"
                                        className="w-full"
                                        disabled={mutation.isLoading}
                                    >
                                        {mutation.isLoading
                                            ? <Icons.spinner className="animate-spin" />
                                            : "Cadastrar Categoria"}
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
