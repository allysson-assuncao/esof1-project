import React from "react";
import {cn} from "@/lib/utils";
import {Label} from "@/components/ui/label";
import {Input} from "@/components/ui/input";
import {Button} from "@/components/ui/button";
import {Checkbox} from "@/components/ui/checkbox";

export function AddWorkstationForm({className, onSubmit}: { className?: string; onSubmit?: (data: any) => void }) {
    const [workstationName, setWorkstationName] = React.useState("");
    const [selectedItems, setSelectedItems] = React.useState<string[]>([]);

    const items = [
        { id: "category1", name: "Placeholder_Category_1" },
        { id: "category2", name: "Placeholder_Category_2" },
        // ...
    ];

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        if (onSubmit) onSubmit({workstationName});
    };

    return (
        <form className={cn("grid items-start gap-6", className)} onSubmit={handleSubmit}>
            <div className="grid gap-3">
                <Label htmlFor="workstationName">Nome do destino</Label>
                <Input id="workstationName" value={workstationName} onChange={e => setWorkstationName(e.target.value)}/>
            </div>
            <div className="grid gap-3">
                <Label>Selecione as categorias</Label>
                <div className="space-y-2">
                    {items.map((item) => (
                        <div key={item.id} className="flex items-center gap-2">
                            <Checkbox
                                id={item.id}
                                checked={selectedItems.includes(item.id)}
                                onCheckedChange={(checked) => {
                                    if (checked) {
                                        setSelectedItems([...selectedItems, item.id]);
                                    }else {
                                        setSelectedItems([...selectedItems.filter(id => id !== item.id)]);
                                    }
                                }}
                            />
                            <Label htmlFor={item.id}>{item.name}</Label>
                        </div>
                    ))}
                </div>
            </div>
            <Button type="submit">Salvar Destino</Button>
        </form>
    );
}