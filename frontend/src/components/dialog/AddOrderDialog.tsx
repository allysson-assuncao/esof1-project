import React from "react";
import {AddOrderDialogProps} from "@/model/Props";
import {
    Dialog,
    DialogContent,
    DialogDescription,
    DialogHeader,
    DialogTitle,
    DialogTrigger
} from "@/components/ui/dialog";
import {
    Drawer, DrawerClose,
    DrawerContent,
    DrawerDescription, DrawerFooter,
    DrawerHeader,
    DrawerTitle,
    DrawerTrigger
} from "@/components/ui/drawer";
import {Button} from "@/components/ui/button";
import {AddOrderForm} from "@/components/form/add/AddOrderForm";

export function AddOrderDialog({guestTabId, parentOrderId = null, buttonText}: AddOrderDialogProps) {
    const [open, setOpen] = React.useState(false);
    const isDesktop =/* useMediaQuery("(min-width: 768px)")*/ true;

    const title = parentOrderId ? "Adicionar Itens Adicionais" : "Adicionar Novo Pedido";
    const description = "Preencha os dados dos itens abaixo. Você pode adicionar vários de uma vez.";

    if (isDesktop) {
        return (
            <Dialog open={open} onOpenChange={setOpen}>
                <DialogTrigger asChild>
                    <Button variant="outline">{buttonText}</Button>
                </DialogTrigger>
                <DialogContent className="sm:max-w-[425px]">
                    <DialogHeader>
                        <DialogTitle>{title}</DialogTitle>
                        <DialogDescription>{description}</DialogDescription>
                    </DialogHeader>
                    <AddOrderForm
                        guestTabId={guestTabId}
                        parentOrderId={parentOrderId}
                        onSuccess={() => setOpen(false)}
                    />
                </DialogContent>
            </Dialog>
        );
    }

    return (
        <Drawer open={open} onOpenChange={setOpen}>
            <DrawerTrigger asChild>
                <Button variant="outline">{buttonText}</Button>
            </DrawerTrigger>
            <DrawerContent>
                <DrawerHeader className="text-left">
                    <DrawerTitle>{title}</DrawerTitle>
                    <DrawerDescription>{description}</DrawerDescription>
                </DrawerHeader>
                <div className="p-4">
                    <AddOrderForm
                        guestTabId={guestTabId}
                        parentOrderId={parentOrderId}
                        onSuccess={() => setOpen(false)}
                    />
                </div>
                <DrawerFooter className="pt-2">
                    <DrawerClose asChild>
                        <Button variant="outline">Cancelar</Button>
                    </DrawerClose>
                </DrawerFooter>
            </DrawerContent>
        </Drawer>
    );
}
