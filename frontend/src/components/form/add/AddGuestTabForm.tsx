import React from "react";
import { cn } from "@/lib/utils";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Button } from "@/components/ui/button";
import {useForm} from "react-hook-form";
import {GuestTabRegisterFormData} from "@/model/FormData";
import {zodResolver} from "@hookform/resolvers/zod";
import {guestTabRegisterSchema} from "@/utils/authValidation";
import {useMutation} from "react-query";
import {toast} from "sonner";
import {AxiosError} from "axios";
import {registerGuestTab} from "@/services/guestTabService";
import {Form, FormControl, FormField, FormItem} from "@/components/ui/form";
import {icons} from "lucide-react";
import {Icons} from "@/public/icons";
import {useRouter} from "next/navigation";



export function AddGuestTabForm({ className, onSubmit, localTableId }: { className?: string; onSubmit?: (data: any) => void; localTableId: string }) {
    const router = useRouter();
    const [guestName, setGuestName] = React.useState("");


    const form = useForm<GuestTabRegisterFormData>({
        resolver: zodResolver(guestTabRegisterSchema),
        defaultValues: {
            guestName: "",
        },
        mode: 'onBlur',
    })

    const mutation = useMutation((data: any) => {
        console.log('Mutation called', data);
        return registerGuestTab(data);
    }, {
        onSuccess: (data) => {
            window.location.reload()
            toast.success("Comanda cadastrada com sucesso!", {
                description: `Cliente: ${data.guestName}!`,
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

    const handleFormSubmit = (data: GuestTabRegisterFormData) => {
        console.log('Form submitted',data)
        const payload = {
            localTableId: localTableId,
            guestName: data.guestName,
        }
        if (onSubmit) {
            onSubmit(payload);
        } else {
            mutation.mutate(payload);
        }
    }

    return (
        <Form {...form}>
            <form onSubmit={form.handleSubmit(handleFormSubmit)}>
                <div className="grid gap-4 sm:gap-6">
                    <FormField
                        control = {form.control}
                        name="guestName"
                        render={({ field }) => (
                            <FormItem>
                                <Label>Nome do cliente</Label>
                                <FormControl>
                                    <Input placeholder="Ex.: Jacaré, Seu José..." {...field}/>
                                </FormControl>
                            </FormItem>
                        )}
                    />
                </div>
                <div className="flex justify-center">
                    <Button
                        className="w-full justify-center touch-manipulation mt-4"
                        type="submit"
                        disabled={mutation.isLoading}
                    >
                        {mutation.isLoading ? <Icons.spinner className="animate-spin"/> : 'Cadastrar comanda'}
                    </Button>
                </div>
            </form>
        </Form>
    );
}