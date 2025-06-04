import {Card, CardContent, CardDescription, CardHeader, CardTitle} from "@/components/ui/card";
import {Form, FormControl, FormField, FormItem, FormLabel, FormMessage} from "@/components/ui/form";
import {Input} from "@/components/ui/input";
import {Button} from "@/components/ui/button";
import {Icons} from "@/public/icons";
import {AxiosError} from "axios";
import {useMutation} from "react-query";
import {toast} from "sonner"
import {useForm} from "react-hook-form";
import {testSchema} from "@/utils/authValidation";
import {zodResolver} from "@hookform/resolvers/zod";
import {test} from "@/services/testService";


const TestForm = () => {
    const form = useForm<{ msg: string }>({
        resolver: zodResolver(testSchema),
        defaultValues: {
            msg: '',
        },
        mode: 'onBlur',
    })

    const mutation = useMutation(test, {
        onSuccess: (data) => {
            toast.success("Teste realizado com sucesso!", {
                description: `${data.message}!`,
            })
        },
        onError: (error: unknown) => {
            if (error instanceof AxiosError) {
                if (error.response?.status === 400) {
                    toast.error("Erro ao realizar o teste!", {
                        description: error.response?.data?.message || 'Ocorreu um erro inesperado.',
                    })
                } else {
                    toast.error("Erro ao realizar o teste!", {
                        description: error.response?.data?.message || 'Ocorreu um erro inesperado.',
                    })
                }
            } else {
                toast.error("Erro ao realizar o teste!", {
                    description: 'Ocorreu um erro inesperado.',
                })
            }
        },
    })

    const onSubmit = (data: { msg: string }) => {
        mutation.mutate(data.msg);
    }

    return (
        <div className="flex flex-col items-center mt-10 space-y-8 md:space-y-6">
            <Card className="w-full md:max-w-[700px] lg:max-w-[900px] mx-auto">
                <CardHeader className="space-y-1">
                    <CardTitle className="text-2xl">Teste</CardTitle>
                    <CardDescription>
                        Página de teste
                    </CardDescription>
                </CardHeader>
                <CardContent className="grid gap-4">
                    <Form {...form}>
                        <form onSubmit={form.handleSubmit(onSubmit)}>
                            <div className="grid gap-4 sm:gap-6">
                                <FormField
                                    control={form.control}
                                    name="msg"
                                    render={({field}) => (
                                        <FormItem>
                                            <FormLabel>Mensagem</FormLabel>
                                            <FormControl>
                                                <Input placeholder="mensagem de teste" {...field} />
                                            </FormControl>
                                            <FormMessage/>
                                        </FormItem>
                                    )}
                                />
                                <div className="flex justify-center">
                                    <Button className="w-full justify-center touch-manipulation"
                                            type="submit" disabled={mutation.isLoading}>
                                        {mutation.isLoading ? <Icons.spinner className="animate-spin"/> : 'Teste'}
                                    </Button>
                                </div>
                            </div>
                        </form>
                    </Form>
                </CardContent>
            </Card>
        </div>
    )
}

export default TestForm
