import {
    Dialog, DialogClose,
    DialogContent,
    DialogDescription, DialogFooter,
    DialogHeader,
    DialogTitle,
    DialogTrigger
} from "@/components/ui/dialog";
import {DisplayGuestTabItem, DisplayOrderItem} from "@/model/Interfaces";
import React, {useState} from "react";
import {Button} from "@/components/ui/button";
import {Label} from "@/components/ui/label";
import {Input} from "@/components/ui/input";
import {Separator} from "@/components/ui/separator";
import {toast} from "sonner";
import {useMutation, useQueryClient} from "react-query";
import {closeGuestTab} from "@/services/guestTabService";
import {AxiosError} from "axios";

interface CloseGuestTabDialogProps {
    guestTab: DisplayGuestTabItem;
}

const formatCurrency = (value: number) => {
    return new Intl.NumberFormat("pt-BR", {
        style: "currency",
        currency: "BRL",
    }).format(value);
};

const renderOrderItems = (items: DisplayOrderItem[], isAdditional = false) => (
    <ul className={`pl-4 ${isAdditional ? 'mt-2 border-l border-dashed' : ''}`}>
        {items.map(item => (
            <li key={item.id} className="text-sm mb-2">
                <div className="flex justify-between">
                    <span>{item.amount}x {item.productName}</span>
                    <span>{formatCurrency(item.productUnitPrice * item.amount)}</span>
                </div>
                {item.observation && <p className="text-xs text-muted-foreground pl-2">- {item.observation}</p>}
                {item.additionalOrders && item.additionalOrders.length > 0 && (
                    <div className="pt-1">
                        <p className="text-xs font-semibold pl-2">Adicionais:</p>
                        {renderOrderItems(item.additionalOrders, true)}
                    </div>
                )}
            </li>
        ))}
    </ul>
);

export const CloseGuestTabDialog: React.FC<CloseGuestTabDialogProps> = ({guestTab}) => {
    const queryClient = useQueryClient();
    const [step, setStep] = useState(1);
    const [payers, setPayers] = useState(1);
    const [isDialogOpen, setIsDialogOpen] = useState(false);

    const closeTabMutation = useMutation(closeGuestTab, {
        onSuccess: () => {
            toast.success(`Comanda #${guestTab.id} fechada com sucesso!`);
            queryClient.invalidateQueries(['guestTabs']);
        },
        onError: (error: unknown) => {
            if (error instanceof AxiosError) {
                toast.error("Erro ao fechar a comanda", {
                    description: error.response?.data?.message || 'Ocorreu um erro inesperado.',
                })
            } else {
                toast.error("Erro ao fechar a comanda", {
                    description: 'Ocorreu um erro inesperado.',
                })
            }
        },
        onSettled: () => {
            setIsDialogOpen(false);
            setTimeout(() => {
                setStep(1);
                setPayers(1);
            }, 300);
        }
    });

    const handleConfirmPayers = () => {
        setStep(2);
    };

    const handleFinalizeAndClose = () => {
        closeTabMutation.mutate({
            guestTabId: guestTab.id,
            numberOfPayers: payers,
        });
    };

    return (
        <Dialog open={isDialogOpen} onOpenChange={setIsDialogOpen}>
            <DialogTrigger asChild>
                <Button variant="outline" size="sm">Fechar Comanda</Button>
            </DialogTrigger>

            {/* Dialog 1: Amount of Payers */}
            {step === 1 && (
                <DialogContent className="sm:max-w-md">
                    <DialogHeader>
                        <DialogTitle>Fechar Comanda #{guestTab.id}</DialogTitle>
                        <DialogDescription>
                            Informe a quantidade de pessoas que irão dividir o valor da comanda.
                        </DialogDescription>
                    </DialogHeader>
                    <div className="flex items-center space-x-2">
                        <div className="grid flex-1 gap-2">
                            <Label htmlFor="payers" className="sr-only">
                                Pagantes
                            </Label>
                            <Input
                                id="payers"
                                type="number"
                                defaultValue={payers}
                                onChange={(e) => setPayers(Number(e.target.value) || 1)}
                                min="1"
                            />
                        </div>
                    </div>
                    <DialogFooter className="sm:justify-end">
                        <DialogClose asChild>
                            <Button type="button" variant="secondary">Cancelar</Button>
                        </DialogClose>
                        <Button type="button" onClick={handleConfirmPayers}>
                            Confirmar
                        </Button>
                    </DialogFooter>
                </DialogContent>
            )}

            {/* Diálog 2: GuestTab Summary (Fiscal Note) */}
            {step === 2 && (
                <DialogContent className="sm:max-w-lg">
                    <DialogHeader>
                        <DialogTitle>Resumo da Comanda #{guestTab.id}</DialogTitle>
                        <DialogDescription>
                            Cliente: {guestTab.guestName || 'Não informado'}
                        </DialogDescription>
                    </DialogHeader>
                    <div className="max-h-[60vh] overflow-y-auto pr-4">
                        {guestTab.orderGroups.map((group, index) => (
                            <div key={index} className="mb-4">
                                {renderOrderItems(group.orders)}
                            </div>
                        ))}
                        {guestTab.orderGroups.length === 0 && (
                            <p className="text-center text-muted-foreground py-4">Nenhum item nesta comanda.</p>
                        )}
                    </div>
                    <Separator/>
                    <div className="space-y-2">
                        <div className="flex justify-between font-semibold">
                            <span>Total</span>
                            <span>{formatCurrency(guestTab.totalPrice)}</span>
                        </div>
                        <div className="flex justify-between text-sm text-muted-foreground">
                            <span>Divisão por {payers} pagante(s)</span>
                            <span>{formatCurrency(guestTab.totalPrice / payers)} por pessoa</span>
                        </div>
                    </div>
                    <DialogFooter>
                        <Button type="button" variant="secondary" onClick={() => alert('Imprimindo...')}>
                            Imprimir
                        </Button>
                        <Button type="button" onClick={handleFinalizeAndClose}>
                            Fechar
                        </Button>
                    </DialogFooter>
                </DialogContent>
            )}
        </Dialog>
    );
};
