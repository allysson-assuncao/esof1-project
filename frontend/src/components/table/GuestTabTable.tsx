'use client'

import {useQuery} from 'react-query'
import {useState} from 'react'
import {GuestTabDataTable} from "@/components/table/data-table/GuestTabDataTable";
import {DataTableSkeleton} from "@/components/skeleton/DataTableSkeleton";
import {guestTabColumns} from "@/components/table/columns/GuestTabColumns";
import {GuestTabFilters} from "@/model/Interfaces";
import {fetchFilteredGuestTabs} from "@/services/guestTabService";

const GuestTabTable = () => {
    const [selectedFilters, setSelectedFilters] = useState<GuestTabFilters>({
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
    const [cachedPages, setCachedPages] = useState<{ [key: number]: GuestTabFilters[] }>({});

    const {data, error, isLoading} = useQuery(
        ['guestTabs', selectedFilters, page],
        async () => {
            if (page !== 0 && cachedPages[page]) {
                return cachedPages[page]
            } else {
                const res = await fetchFilteredGuestTabs({
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

    if (isLoading) return <DataTableSkeleton/>
    if (error) return <div>Erro carregando os dados</div>

    return (
        <div className="container mx-auto py-10 w-full max-w-[1920px] 5xl:mx-auto 5xl:px-32">
            <div className="flex flex-col md:flex-row justify-center gap-3 md:gap-8 items-start md:items-center">
                <h1 className="text-2xl font-bold">Comandas e Pedidos</h1>
            </div>
            <GuestTabDataTable
                columns={guestTabColumns}
                data={data || []}
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

export default GuestTabTable
