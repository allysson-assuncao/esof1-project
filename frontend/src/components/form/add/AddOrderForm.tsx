import React from "react";
import {cn} from "@/lib/utils";
import {Input} from "@/components/ui/input";
import {Label} from "@/components/ui/label";
import {Button} from "@/components/ui/button";

interface AddOrderFormProps {
  className?: string;
  onSubmit?: (data: any) => void;
  guestTabId?: number;
  parentOrderId?: number | null;
}

export function AddOrderForm({
  className,
  onSubmit,
  guestTabId,
  parentOrderId,
}: AddOrderFormProps) {
    const [productName, setProductName] = React.useState("");
    const [amount, setAmount] = React.useState(1);

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        if (onSubmit) onSubmit({productName, amount});
    };

    return (
        <form className={cn("grid items-start gap-6", className)} onSubmit={handleSubmit}>
            <div className="grid gap-3">
                <Label htmlFor="productName">Produto</Label>
                <Input id="productName" value={productName} onChange={e => setProductName(e.target.value)}/>
            </div>
            <div className="grid gap-3">
                <Label htmlFor="amount">Quantidade</Label>
                <Input id="amount" type="number" min={1} value={amount}
                       onChange={e => setAmount(Number(e.target.value))}/>
            </div>
            <Button type="submit">Salvar Pedido</Button>
        </form>
    );
}
