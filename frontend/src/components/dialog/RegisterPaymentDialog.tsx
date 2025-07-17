import {
    Dialog,
    DialogContent,
    DialogDescription,
    DialogHeader,
    DialogTitle,
    DialogTrigger
} from "@/components/ui/dialog";
import {Button} from "@/components/ui/button";
import {Label} from "@/components/ui/label";
import {Input} from "@/components/ui/input";

interface RegisterPaymentDialogProps {
    guestTabId: number;
}

export const RegisterPaymentDialog: React.FC<RegisterPaymentDialogProps> = ({guestTabId}) => {
    return (
        <Dialog>
            <DialogTrigger asChild>
                <Button variant="default" size="sm">Registrar Pagamento</Button>
            </DialogTrigger>
            <DialogContent className="sm:max-w-md">
                <DialogHeader>
                    <DialogTitle>Registrar Pagamento - Comanda #{guestTabId}</DialogTitle>
                    <DialogDescription>
                        (Placeholder) O formulário de registro de pagamento será implementado aqui.
                    </DialogDescription>
                </DialogHeader>
                <div className="space-y-4 py-4">
                    <div className="space-y-2">
                        <Label htmlFor="paymentMethod">Método de Pagamento</Label>
                        <Input id="paymentMethod" placeholder="Ex: Cartão de Crédito" disabled/>
                    </div>
                    <div className="space-y-2">
                        <Label htmlFor="paymentValue">Valor Pago</Label>
                        <Input id="paymentValue" placeholder="R$ 0,00" disabled/>
                    </div>
                </div>
            </DialogContent>
        </Dialog>
    );
};
