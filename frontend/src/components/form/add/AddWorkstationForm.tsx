import React from "react";
import {cn} from "@/lib/utils";
import {Label} from "@/components/ui/label";
import {Input} from "@/components/ui/input";
import {Button} from "@/components/ui/button";
import {Checkbox} from "@/components/ui/checkbox";
import {useQuery} from "react-query";
import {SimpleCategory} from "@/model/Interfaces";
import {fetchSimpleCategories} from "@/services/categoryService";
import {Skeleton} from "@/components/ui/skeleton";
import {Dialog} from "@/components/ui/dialog"; // Add skeleton for loading state

export function AddWorkstationForm({className, onSubmit}: { className?: string; onSubmit?: (data: any) => void }) {
    const [workstationName, setWorkstationName] = React.useState("");
    const [selectedItems, setSelectedItems] = React.useState<string[]>([]);

    const {
        data: simpleCategories,
        isLoading,
        error
    } = useQuery<SimpleCategory[]>(
        ["simpleCategories"],
        fetchSimpleCategories
    );

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        if (onSubmit) onSubmit({
            workstationName,
            categoryIds: selectedItems
        });
    };

    return (
        <Dialog>
            <form className={cn("grid items-start gap-6", className)} onSubmit={handleSubmit}>
                <div className="grid gap-3">
                    <Label htmlFor="workstationName">Nome da destinação</Label>
                    <Input
                        id="workstationName"
                        value={workstationName}
                        onChange={e => setWorkstationName(e.target.value)}
                        required
                    />
                </div>

                <div className="grid gap-3">
                    <Label>Selecione as categorias pertencentes ao destino</Label>

                    {isLoading ? (
                        <div className="space-y-2">
                            {[...Array(3)].map((_, i) => (
                                <div key={i} className="flex items-center gap-2">
                                    <Skeleton className="h-4 w-4 rounded" />
                                    <Skeleton className="h-4 w-32 rounded" />
                                </div>
                            ))}
                        </div>
                    ) : error ? (
                        <div className="text-red-500 text-sm">
                            Erro ao carregar categorias: {(error as Error)?.message || "Erro desconhecido"}
                        </div>
                    ) : (
                        <div className="space-y-2">
                            {simpleCategories?.map((item) => (
                                <div key={item.id} className="flex items-center gap-2">
                                    <Checkbox
                                        id={item.id}
                                        checked={selectedItems.includes(item.id)}
                                        onCheckedChange={(checked) => {
                                            setSelectedItems(prev =>
                                                checked
                                                    ? [...prev, item.id]
                                                    : prev.filter(id => id !== item.id)
                                            );
                                        }}
                                    />
                                    <Label htmlFor={item.id}>{item.name}</Label>
                                </div>
                            ))}
                        </div>
                    )}
                </div>

                <Button
                    type="submit"
                    disabled={isLoading}
                >
                    {isLoading ? "Carregando..." : "Salvar Destino"}
                </Button>
            </form>
        </Dialog>

    );
}