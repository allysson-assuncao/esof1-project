import React from "react";
import { cn } from "@/lib/utils";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Button } from "@/components/ui/button";

export function AddGuestTabForm({className, onSubmit}: { className?: string; onSubmit?: (data: any) => void }) {
    const [clientName, setClientName] = React.useState("");

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        if (onSubmit) onSubmit({clientName});
    };

    return (
        <form className={cn("grid items-start gap-6", className)} onSubmit={handleSubmit}>
            <div className="grid gap-3">
                <Label htmlFor="clientName">Nome do Cliente</Label>
                <Input id="clientName" value={clientName} onChange={e => setClientName(e.target.value)}/>
            </div>
            <Button type="submit">Salvar Comanda</Button>
        </form>
    );
}