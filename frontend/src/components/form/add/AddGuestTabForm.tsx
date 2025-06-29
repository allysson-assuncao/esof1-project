import React from "react";
import { cn } from "@/lib/utils";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Button } from "@/components/ui/button";
import {useForm} from "react-hook-form";
import {GuestTabRegisterFormData, LocalTableRegisterFormData} from "@/model/FormData";
import {zodResolver} from "@hookform/resolvers/zod";
import {guestTabRegisterSchema, localTableRegisterSchema} from "@/utils/authValidation";
import {useMutation} from "react-query";
import {registerLocalTable} from "@/services/localTableService";
import {toast} from "sonner";
import {AxiosError} from "axios";
import {registerGuestTab} from "@/services/guestTabService";

interface AddGuestTabFormProps {
    localTableId: string;
}

export function AddGuestTabForm({ className, onSubmit, localTableId }: { className?: string; onSubmit?: (data: any) => void; localTableId: string }) {
    const [clientName, setClientName] = React.useState("");

    const form = useForm<GuestTabRegisterFormData>({
        resolver: zodResolver(guestTabRegisterSchema),
        defaultValues: {
            localTableId: localTableId,
            guestName: "",
        },
        mode: 'onBlur',
    })

    const mutation = useMutation(registerGuestTab, {
        onSuccess: (data) => {

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
        if (onSubmit) {
            onSubmit(data);
        } else {
            mutation.mutate(data);
        }
    }

    return (
        <form className={cn("grid items-start gap-6", className)} onSubmit={handleFormSubmit}>
            <div className="grid gap-3">
                <Label htmlFor="clientName">Nome do Cliente</Label>
                <Input id="clientName" value={clientName} onChange={e => setClientName(e.target.value)}/>
            </div>
            <Button type="submit">Salvar Comanda</Button>
        </form>
    );
}