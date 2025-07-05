import {fetchSimpleWorkstationsByEmployee} from "@/services/workstationService";
import {useInfiniteQuery, useQuery} from "react-query";
import {KanbanOrderResultsFilter, OrderKanbanStatus, OrderKanban, SimpleWorkstation} from "@/model/Interfaces";
import {useState} from "react";
import {MultiSelect} from "@/components/ui/multi-select";
import {fetchFilteredOrderKanbanResults} from "@/services/orderService";

export const OrdersByWorkstationKanban = () => {
    const [selectedFilters, setSelectedFilters] = useState<KanbanOrderResultsFilter>({
        workstationIds: [],
    })

    const {data: workstationOptions, isLoading: isLoadingWorkstations} = useQuery<SimpleWorkstation[]>({
        queryKey: ['workstations'],
        queryFn: fetchSimpleWorkstationsByEmployee,
    });

    const optionsForFilter = workstationOptions?.map(ws => ({value: ws.id, label: ws.name})) ?? [];

    const {
        data,
        error,
        isLoading: isLoadingResults,
        fetchNextPage,
        hasNextPage,
        isFetchingNextPage,
        isFetching,
    } = useInfiniteQuery(
        ['answerSheetResults', selectedFilters],
        ({pageParam = 0}) => fetchFilteredOrderKanbanResults({
            filter: selectedFilters,
            page: pageParam,
            size: 50,
            orderBy: 'user.name',
            direction: 'ASC',
        }),
        {
            getNextPageParam: (lastPage, allPages) => {
                return lastPage.hasNextPage ? allPages.length : undefined;
            },
            enabled: isLoadingWorkstations && workstationOptions,
        },
    )

    return (
        <div className="flex flex-col h-screen p-4 gap-4">
            <header className="flex-shrink-0">
                <h1 className="text-2xl font-bold mb-4">Painel de Pedidos</h1>
                <div className="max-w-md p-2 rounded-lg shadow">
                    <MultiSelect
                        options={optionsForFilter}
                        onValueChange={(selectedValues) => setSelectedFilters({
                            ...selectedFilters,
                            workstationIds: selectedValues,
                        })}
                        defaultValue={selectedFilters.workstationIds || []}
                        placeholder="Selecione a área de Trabalho..."
                        animation={2}
                        maxCount={3}
                    />
                </div>
            </header>

            <main className="flex-1 flex gap-6 overflow-x-auto">
                {KANBAN_STATUSES.map((status) => (
                    <OrderColumn
                        key={status}
                        status={status}
                        title={statusLabels[status]}
                        workstationIds={workstationIds}
                    />
                ))}
            </main>
            <Toaster/>
        </div>
    );
};
