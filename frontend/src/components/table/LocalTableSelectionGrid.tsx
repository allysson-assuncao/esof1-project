"use client";

import {useQuery} from "react-query";
import {LocalTable} from "@/model/Interfaces";
import {useRouter} from "next/router";
import {LocalTableCard} from "@/components/card/LocalTableCard";

async function fetchTables(): Promise<LocalTable[]> {
    const response = await fetch('/api/tables/grid');
    if (!response.ok) {
        throw new Error("Falha ao buscar as mesas");
    }
    return response.json();
}

export function LocalTableSelectionGrid() {
    const router = useRouter();

    const {data: tables, isLoading, error} = useQuery("tablesGrid", fetchTables, {
        // Atualiza os dados a cada 30 segundos, por exemplo
        refetchInterval: 30000,
    });

    // Função chamada ao clicar em uma mesa
    const handleTableSelect = (tableId: string) => {
        console.log(`Mesa selecionada: ${tableId}`);
        // AQUI VAI A LÓGICA DE REDIRECIONAMENTO
        // Exemplo: redirecionar para a página de comandas daquela mesa
        router.push(`/dashboard/mesas/${tableId}/comandas`);
    };

    if (isLoading) {
        return <div className="text-center p-10">Carregando mesas...</div>;
    }

    if (error) {
        return <div className="text-center text-red-500 p-10">Erro ao carregar as mesas. Tente novamente mais
            tarde.</div>;
    }

    return (
        <div className="container mx-auto p-4">
            <h1 className="text-3xl font-bold mb-6 text-center">Selecione uma Mesa</h1>

            {/* GRID RESPONSIVO */}
            <div
                className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 xl:grid-cols-6 2xl:grid-cols-8 gap-4 md:gap-6">
                {tables?.map((table) => (
                    <LocalTableCard
                        key={table.id}
                        table={table}
                        onSelect={handleTableSelect}
                    />
                ))}
            </div>
        </div>
    );
}