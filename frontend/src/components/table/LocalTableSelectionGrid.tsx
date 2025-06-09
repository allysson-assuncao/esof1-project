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
    FREE: "bg-green-100 border-green-400 text-green-800 hover:bg-green-200",
    OCCUPIED: "bg-red-100 border-red-400 text-red-800 hover:bg-red-200",
    RESERVED: "bg-gray-200 border-gray-400 text-gray-600 hover:bg-gray-300",
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
        return <div>Carregando perfil...</div>;
    }

    return (
        <div className="container w-full mx-auto p-4">
            <h1 className="text-3xl font-bold mb-6 text-center">Selecione uma Mesa</h1>
            <div
                className="grid grid-cols-1 lg:grid-cols-2 3xl:grid-cols-3 gap-4 md:gap-8 p-4 md:p-8 justify-items-stretch items-start w-full mt-20">
                {tables?.map((table: LocalTable) => (
                    <Card
                        key={table.id}
                        className={`w-full md:max-w-[700px] lg:max-w-[900px] mx-auto ${statusStyles[table.status] || statusStyles.RESERVED}`}
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