'use client'

import {useQuery} from 'react-query'
import {useMemo, useState} from 'react'
import {OrdersDataTable} from "@/components/table/data-table/OrdersDataTable";
import {DataTableSkeleton} from "@/components/skeleton/DataTableSkeleton";
import {ordersColumns} from "@/components/table/columns/OrdersColumns";
import {groupOrdersByGuestTab, OrderFilters} from "@/model/Interfaces";
import {fetchFilteredProcesses} from "@/services/ordersService";

const OrdersTable = () => {
    const [selectedFilters, setSelectedFilters] = useState<OrderFilters>({
        tableId: "",
        guestTabIds: [],
        orderIds: [],
        orderStatuses: [],
        guestTabStatuses: [],
        minPrice: 0,
        maxPrice: 9999999,
        startTime: undefined,
        endTime: undefined,
        waiterIds: [],
        productName: "",
    })
    const [page, setPage] = useState(0);
    const [pageSize, setPageSize] = useState(10);
    const [totalPages, setTotalPages] = useState(30);
    const [cachedPages, setCachedPages] = useState<{ [key: number]: OrderFilters[] }>({});

    const {data: flatOrders, error, isLoading} = useQuery(
        ['orders', selectedFilters, page, pageSize],
        async () => {
            if (page !== 0 && cachedPages[page]) {
                return cachedPages[page]
            } else {
                const res = await fetchFilteredProcesses({
                    filter: selectedFilters,
                    page: page,
                    size: pageSize,
                    direction: 'DESC',
                })
                setTotalPages(res.totalPages)
                setCachedPages((prev) => ({...prev, [page]: res.content}))
                return res.content
            }
        },
        {
            keepPreviousData: true,
            initialData: cachedPages[page] || undefined,
        },
    )

    const groupedData = useMemo(() => {
        const content = !flatOrders ? [] : flatOrders;
        console.log(content)
        return groupOrdersByGuestTab(content);
    }, [flatOrders]);

    if (isLoading) return <DataTableSkeleton/>
    if (error) return <div>Erro carregando os dados</div>

    return (
        <div className="container mx-auto py-10 w-full max-w-[1920px] 5xl:mx-auto 5xl:px-32">
            <div className="flex flex-col md:flex-row justify-center gap-3 md:gap-8 items-start md:items-center">
                <h1 className="text-2xl font-bold">Comandas e Pedidos</h1>
            </div>
            <OrdersDataTable
                columns={ordersColumns}
                data={groupedData}
                setPage={setPage}
                selectedFilters={selectedFilters}
                setSelectedFilters={setSelectedFilters}
                page={page}
                totalPages={totalPages}
                setPageSize={setPageSize}
            />
        </div>
    )
}

export default OrdersTable
