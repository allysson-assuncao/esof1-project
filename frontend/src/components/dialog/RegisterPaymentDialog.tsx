import {
    Dialog,
    DialogContent,
    DialogHeader,
    DialogTitle,
    DialogTrigger
} from "@/components/ui/dialog";
import {Button} from "@/components/ui/button";
import {DisplayGuestTabItem} from "@/model/Interfaces";
import React from "react";
import {RegisterPaymentForm} from "@/components/form/register/RegisterPaymentForm";

interface RegisterPaymentDialogProps {
    guestTab: DisplayGuestTabItem;
}

export const RegisterPaymentDialog: React.FC<RegisterPaymentDialogProps> = ({guestTab}) => {
    const [open, setOpen] = React.useState(false);

    if (guestTab.status !== 'CLOSED' || !guestTab.payment) {
        return <Button variant="default" size="sm" disabled>Pagar</Button>;
    }

    return (
        <Dialog open={open} onOpenChange={setOpen}>
            <DialogTrigger asChild>
                <Button variant="default" size="sm">Registrar Pagamento</Button>
            </DialogTrigger>
            <DialogContent className="sm:max-w-lg">
                <DialogHeader>
                    <DialogTitle>Registrar Pagamento - Comanda #{guestTab.id}</DialogTitle>
                </DialogHeader>
                <RegisterPaymentForm
                    guestTab={guestTab}
                    onSuccess={() => setOpen(false)}
                />
            </DialogContent>
        </Dialog>
    );
};
