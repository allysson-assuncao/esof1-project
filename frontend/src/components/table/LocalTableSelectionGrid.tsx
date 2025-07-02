"use client";

import {LocalTable, LocalTableStatus} from "@/model/Interfaces";
import {toast} from "sonner"
import {useEffect, useState} from "react";
import {useMutation} from "react-query";
import {AxiosError} from "axios";
import {fetchLocalTables} from "@/services/localTableService";
import {Card, CardContent, CardDescription, CardHeader, CardTitle} from "@/components/ui/card";
import {useRouter} from "next/navigation";

const statusStyles: Record<LocalTableStatus, string> = {
    FREE: "border-emerald-500/80 hover:bg-emerald-500/10 dark:border-emerald-400/70 dark:hover:bg-emerald-400/10",

    OCCUPIED: "bg-destructive/5 border-destructive/50 hover:bg-destructive/10",

    RESERVED: "bg-muted/60 border-muted-foreground/30 hover:bg-muted",
};



export function LocalTableSelectionGrid() {
    const router = useRouter();
    const [tables, setTables] = useState<LocalTable[]>([]);
    const [isHydrated, setIsHydrated] = useState(false);
    const [wasFetched, setWasFetched] = useState(false);

    const mutation = useMutation(fetchLocalTables, {
        onSuccess: (data: LocalTable[]) => {
            setTables(data);
            toast.success("Mesas carregadas com sucesso!", {
                description: `Total de mesas: ${data.length}`,
            })
        },
        onError: (error: unknown) => {
            if (error instanceof AxiosError) {
                toast.error("Erro ao carregar as mesas", {
                    description: error.response?.data?.message || 'Não foi possível carregar os dados das mesas.',
                })
            } else {
                toast.error("Erro ao carregar as mesas", {
                    description: 'Não foi possível carregar os dados das mesas.',
                })
            }
        },
    });

    useEffect(() => {
        if (isHydrated && !wasFetched) {
            mutation.mutate();
            setWasFetched(true);
        }
    }, [mutation, isHydrated, wasFetched]);

    useEffect(() => {
        setIsHydrated(true);
    }, []);

    if (!tables || mutation.isLoading) {
        return <div>Carregando mesas...</div>;
    }

    return (
        <div className="container w-full mx-auto p-4">
            <h1 className="text-3xl font-bold mb-6 text-center">Selecione uma Mesa</h1>
            <div
                className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6 p-4 md:p-8 w-full">
                {tables?.map((table: LocalTable) => (
                    <Card
                        key={table.id}
                        className={`w-full mx-auto border-2 transition-all duration-300 ease-in-out cursor-pointer hover:shadow-lg hover:-translate-y-1 ${statusStyles[table.status] || statusStyles.RESERVED}`}
                        onClick={() => router.push('/dashboard/table/guest-tabs/' + table.id)}
                    >
                        <CardHeader className="space-y-1">
                            <CardTitle className="text-2xl">Mesa {String(table.number).padStart(2, '0')}</CardTitle>
                            <CardDescription>
                                {table.guestTabCountToday} Comanda(s)
                            </CardDescription>
                        </CardHeader>
                        <CardContent className="grid gap-4">
                            <div className="relative">
                                <div className="absolute inset-0 flex items-center">
                                    <span className="w-full border-t"/>
                                </div>
                            </div>
                            <CardDescription>
                                {LocalTableStatus[table.status]?.label || table.status}
                            </CardDescription>
                        </CardContent>
                    </Card>
                ))}
            </div>
        </div>
    );
}