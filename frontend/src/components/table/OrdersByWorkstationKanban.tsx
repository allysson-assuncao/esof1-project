'use client'

import {fetchSimpleWorkstationsByEmployee} from "@/services/workstationService";
import {useInfiniteQuery, useMutation, useQuery, useQueryClient} from "react-query";
import {
    KanbanOrderResultsFilter,
    SimpleWorkstation
} from "@/model/Interfaces";
import {useMemo, useState} from "react";
import {MultiSelect} from "@/components/ui/multi-select";
import {fetchFilteredOrderKanbanResults, nextOrderStatus, previousOrderStatus} from "@/services/orderService";
import {toast} from "sonner";
import {AxiosError} from "axios";
import {OrdersByWorkstationDataTable} from "@/components/table/data-table/OrdersByWorkstationDataTable";
import {makeOrderKanbanColumns} from "@/components/table/columns/OrdersByWorkstationColumns";
import {DataTableSkeleton} from "@/components/skeleton/DataTableSkeleton";

export const OrdersByWorkstationKanban = () => {
    const [selectedFilters, setSelectedFilters] = useState<KanbanOrderResultsFilter>({
        workstationIds: [],
    })

    const {data: workstationOptions, isLoading: isLoadingWorkstations} = useQuery<SimpleWorkstation[]>({
        queryKey: ['workstations'],
        queryFn: fetchSimpleWorkstationsByEmployee,
    });

    const optionsForFilter = workstationOptions?.map(ws => ({value: ws.id, label: ws.name})) ?? [];

    const queryClient = useQueryClient();

    const {
        data,
        error,
        isLoading: isLoadingOrders,
        fetchNextPage,
        hasNextPage,
        isFetchingNextPage,
    } = useInfiniteQuery({
        queryKey: ['kanbanOrders', selectedFilters],
        queryFn: ({pageParam = 0}) => fetchFilteredOrderKanbanResults({
            filter: selectedFilters,
            page: pageParam,
            size: 20,
        }),
        getNextPageParam: (lastPage, allPages) => {
            const hasMoreInSent = (lastPage?.sentOrders?.totalPages ?? 0) > allPages.length;
        const hasMoreInPrepare = (lastPage?.inPrepareOrders?.totalPages ?? 0) > allPages.length;
        const hasMoreInReady = (lastPage?.readyOrders?.totalPages ?? 0) > allPages.length;

        const hasMore = hasMoreInSent || hasMoreInPrepare || hasMoreInReady;
        return hasMore ? allPages.length : undefined;
        },
        enabled: !isLoadingWorkstations,
    });

    const { mutate: advanceStatus } = useMutation({
        mutationFn: ({ orderId }: { orderId: number }) =>
            nextOrderStatus({ orderId }),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['kanbanOrders'] });
            toast.success("Status do Pedido Avançado!", {
                description: "O pedido foi movido para a próxima fila.",
            });
        },
        onError: (error: unknown) => {
            if (error instanceof AxiosError) {
                toast.error("Erro ao avançar status do pedido", {
                    description: error.response?.data?.message || 'Ocorreu um erro inesperado.',
                });
            } else {
                toast.error("Erro ao avançar status do pedido", {
                    description: 'Ocorreu um erro inesperado.',
                });
            }
        },
    });

    const { mutate: revertStatus } = useMutation({
        mutationFn: ({ orderId }: { orderId: number }) =>
            previousOrderStatus({ orderId }),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['kanbanOrders'] });
            toast.success("Status do Pedido Revertido!", {
                description: "O pedido foi movido para a fila anterior.",
            });
        },
        onError: (error: unknown) => {
            if (error instanceof AxiosError) {
                toast.error("Erro ao reverter status do pedido", {
                    description: error.response?.data?.message || 'Ocorreu um erro inesperado.',
                });
            } else {
                toast.error("Erro ao reverter status do pedido", {
                    description: 'Ocorreu um erro inesperado.',
                });
            }
        },
    });

    const handleAdvanceStatus = (orderId: number) => {
        advanceStatus({ orderId });
    };

    const handleRevertStatus = (orderId: number) => {
        revertStatus({ orderId });
    };

    const columns = useMemo(
        () =>
            makeOrderKanbanColumns({
                onAdvanceStatus: handleAdvanceStatus,
                onRevertStatus: handleRevertStatus,
            }),
        []
    );

    const sentOrders = useMemo(() => data?.pages.flatMap(page => page.sentOrders.content) ?? [], [data]);
    const inPrepareOrders = useMemo(() => data?.pages.flatMap(page => page.inPrepareOrders.content) ?? [], [data]);
    const readyOrders = useMemo(() => data?.pages.flatMap(page => page.readyOrders.content) ?? [], [data]);

    if (isLoadingOrders) return <DataTableSkeleton />
    if (error) return <div>Erro carregando os dados</div>

    const kanbanQueues = [
        {title: "Enviados", data: sentOrders},
        {title: "Em Preparo", data: inPrepareOrders},
        {title: "Prontos", data: readyOrders}
    ];

    return (
        <div className="container mx-auto py-10 w-full max-w-[1920px] 5xl:mx-auto 5xl:px-32">
            <div className="flex flex-col md:flex-row justify-center gap-3 md:gap-8 items-start md:items-center">
                Filas de Pedidos
            </div>
            <div className="max-w-md p-2 rounded-lg shadow">
                <MultiSelect
                    options={optionsForFilter}
                    onValueChange={(selectedValues) => setSelectedFilters({
                        ...selectedFilters,
                        workstationIds: selectedValues,
                    })}
                    defaultValue={selectedFilters.workstationIds || []}
                    placeholder="Selecione a área de Trabalho..."
                    disabled={isLoadingWorkstations}
                    animation={2}
                    maxCount={3}
                />
            </div>

            <main className="flex gap-6 overflow-x-auto p-2">
                {kanbanQueues.map(queue => (
                    <OrdersByWorkstationDataTable
                        key={queue.title}
                        title={queue.title}
                        columns={columns}
                        data={queue.data}
                        fetchNextPage={fetchNextPage}
                        hasNextPage={hasNextPage}
                        isFetchingNextPage={isFetchingNextPage}
                    />
                ))}
            </main>
        </div>
    );
};
