import React, {SetStateAction, useEffect, useState} from 'react'
import {
    useReactTable,
    getCoreRowModel,
    getPaginationRowModel,
    getSortedRowModel,
    getFilteredRowModel,
    SortingState,
    ColumnDef, flexRender,
} from '@tanstack/react-table'
import {Table, TableHeader, TableBody, TableRow, TableCell, TableHead} from '@/components/ui/table'
import {Button} from '@/components/ui/button'
import {
    DisplayGuestTabItem,
    DisplayOrderItem,
    SimpleGuestTab,
    GuestTabFilters,
    GuestTabStatus, OrderStatus, SimpleOrder, SimpleWaiter
} from "@/model/Interfaces";
import {Input} from "@/components/ui/input";
import {DatePicker} from "@/components/ui/date-picker";
import {formatDateDisplay} from "@/utils/operations/date-convertion";
import {useQuery} from "react-query";
import {fetchSimpleGuestTabs} from "@/services/guestTabService";
import {MultiSelect} from "@/components/ui/multi-select";
import {fetchSimpleOrders} from "@/services/orderService";
import {fetchSimpleWaiters} from "@/services/userService";

interface DataTableProps<TValue> {
    columns: ColumnDef<DisplayGuestTabItem, TValue>[];
    data: DisplayGuestTabItem[];
    setPage: (page: (prev: number) => number) => void;
    selectedFilters: GuestTabFilters;
    setSelectedFilters: (filters: SetStateAction<GuestTabFilters>) => void;
    page: number;
    totalPages: number;
    setPageSize: (page: number) => void;
    localTableId: string;
}

export function GuestTabDataTable<TValue>({
                                              columns,
                                              data,
                                              setPage,
                                              selectedFilters,
                                              setSelectedFilters,
                                              page,
                                              totalPages,
                                              localTableId,
                                          }: DataTableProps<TValue>) {
    const [sorting, setSorting] = useState<SortingState>([])

    const table = useReactTable({
        data,
        columns,
        state: {sorting},
        onSortingChange: setSorting,
        getCoreRowModel: getCoreRowModel(),
        getPaginationRowModel: getPaginationRowModel(),
        getSortedRowModel: getSortedRowModel(),
        getFilteredRowModel: getFilteredRowModel(),
    })

    const handlePreviousPage = () => {
        table.previousPage()
        setPage((prev) => prev - 1)
    }

    const handleNextPage = () => {
        table.nextPage()
        setPage((prev) => prev + 1)
    }

    const {data: simpleGuestTabs, isLoading: isLoadingGuestTabs} = useQuery<SimpleGuestTab[]>(
        ['simpleGuestTabs', localTableId],
        () => fetchSimpleGuestTabs(localTableId)
    );

    const {data: simpleOrders, isLoading: isLoadingOrders} = useQuery<SimpleOrder[]>(
        ["simpleOrders", localTableId],
        () => fetchSimpleOrders(localTableId)
    );

    const {data: simpleWaiters, isLoading: isLoadingWaiters} = useQuery<SimpleWaiter[]>(
        ["simpleWaiters", localTableId],
        () => fetchSimpleWaiters(localTableId)
    );

    const [initialized, setInitialized] = useState<boolean>(false);
    const [isClient, setIsClient] = useState(false);

    useEffect(() => {
        setIsClient(true);
    }, [])

    useEffect(() => {
        if (simpleGuestTabs && simpleGuestTabs.length > 0 &&
            simpleOrders && simpleOrders.length > 0 &&
            simpleWaiters && simpleWaiters.length > 0 && !initialized) {
            setInitialized(true);
        }
    }, [simpleGuestTabs, initialized, simpleOrders, simpleWaiters]);

    if (isClient && isLoadingGuestTabs && isLoadingOrders && isLoadingWaiters) {
        return <div>Carregando...</div>
    }

    const guestTabsOptions =
        simpleGuestTabs?.map((tab) => ({
            value: tab.id.toString(),
            label: tab.id + tab.clientName,
        })) ?? [];

    const orderOptions =
        simpleOrders?.map((tab) => ({
            value: tab.id.toString(),
            label: tab.id.toString(),
        })) ?? [];

    const waiterOptions =
        simpleWaiters?.map((tab) => ({
            value: tab.id,
            label: tab.id + tab.userName,
        })) ?? [];

    const guestTabStatusOptions = Object.entries(GuestTabStatus).map(([, {value, label}]) => ({
        value,
        label,
    }));

    const orderStatusOptions = Object.entries(OrderStatus).map(([, {value, label}]) => ({
        value,
        label,
    }));

    return (
        <div>
            {/* Filters */}
            <div
                className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 5xl:grid-cols-4 gap-4 md:gap-6 py-4">
                <MultiSelect
                    options={guestTabsOptions}
                    onValueChange={(selectedValues) =>
                        setSelectedFilters({
                            ...selectedFilters,
                            guestTabIds: selectedValues.map((id) => Number(id)),
                        })
                    }
                    defaultValue={
                        selectedFilters.guestTabIds
                            ? selectedFilters.guestTabIds.map(String)
                            : []
                    }
                    placeholder="Selecione as comandas"
                    animation={2}
                    maxCount={3}
                />
                <MultiSelect
                    options={guestTabStatusOptions}
                    onValueChange={(selectedValues) => setSelectedFilters({
                        ...selectedFilters,
                        guestTabStatuses: selectedValues,
                    })}
                    defaultValue={selectedFilters.guestTabStatuses || []}
                    placeholder="Selecione o status da comanda"
                    animation={2}
                    maxCount={3}
                />
                <MultiSelect
                    options={orderStatusOptions}
                    onValueChange={(selectedValues) => setSelectedFilters({
                        ...selectedFilters,
                        orderStatuses: selectedValues,
                    })}
                    defaultValue={selectedFilters.orderStatuses || []}
                    placeholder="Selecione o status do pedido"
                    animation={2}
                    maxCount={3}
                />
                <MultiSelect
                    options={orderOptions}
                    onValueChange={(selectedValues) =>
                        setSelectedFilters({
                            ...selectedFilters,
                            orderIds: selectedValues.map((id) => Number(id)),
                        })
                    }
                    defaultValue={
                        selectedFilters.orderIds
                            ? selectedFilters.orderIds.map(String)
                            : []
                    }
                    placeholder="Selecione os pedidos"
                    animation={2}
                    maxCount={3}
                />
                <MultiSelect
                    options={waiterOptions}
                    onValueChange={(selectedValues) =>
                        setSelectedFilters({
                            ...selectedFilters,
                            waiterIds: selectedValues.map((id) => id),
                        })
                    }
                    defaultValue={
                        selectedFilters.waiterIds
                            ? selectedFilters.waiterIds.map(String)
                            : []
                    }
                    placeholder="Selecione a garçom"
                    animation={2}
                    maxCount={3}
                />
                <Input
                    placeholder="Nome do Produto: "
                    value={selectedFilters.productName}
                    onChange={(event) => setSelectedFilters({...selectedFilters, productName: event.target.value})}
                    className="max-w-sm"
                />
                <Input
                    type="number"
                    placeholder="Preço min: "
                    value={selectedFilters.minPrice}
                    onChange={(event) => setSelectedFilters({...selectedFilters, minPrice: event.target.valueAsNumber})}
                    className="max-w-sm"
                />
                <Input
                    type="number"
                    placeholder="Preço max: "
                    value={selectedFilters.maxPrice}
                    onChange={(event) => setSelectedFilters({...selectedFilters, maxPrice: event.target.valueAsNumber})}
                    className="max-w-sm"
                />
                <DatePicker
                    onDateSelected={(startTime) => setSelectedFilters({...selectedFilters, startTime})}
                />
                <DatePicker
                    onDateSelected={(endTime) => setSelectedFilters({...selectedFilters, endTime})}
                />
            </div>

            {/* Table */}
            <div className="rounded-md border overflow-x-auto">
                <Table className="min-w-full">
                    <TableHeader>
                        {table.getHeaderGroups().map((headerGroup) => (
                            <TableRow key={headerGroup.id}>
                                {headerGroup.headers.map((header) => (
                                    <TableCell key={header.id}>
                                        {header.isPlaceholder
                                            ? null
                                            : flexRender(header.column.columnDef.header, header.getContext())}
                                    </TableCell>
                                ))}
                            </TableRow>
                        ))}
                    </TableHeader>
                    <TableBody>
                        {table.getRowModel().rows?.length ? (
                            table.getRowModel().rows.map((row) => (
                                <React.Fragment key={row.id}>
                                    <TableRow data-state={row.getIsSelected() && "selected"}>
                                        {row.getVisibleCells().map((cell) => (
                                            <TableCell key={cell.id}>
                                                {flexRender(cell.column.columnDef.cell, cell.getContext())}
                                            </TableCell>
                                        ))}
                                    </TableRow>
                                    {row.getIsExpanded() && (
                                        <TableRow>
                                            <TableCell colSpan={row.getVisibleCells().length}>
                                                <OrdersSubTable orders={row.original.orders}/>
                                            </TableCell>
                                        </TableRow>
                                    )}
                                </React.Fragment>
                            ))
                        ) : (
                            <TableRow>
                                <TableCell colSpan={columns.length} className="h-24 text-center">
                                    Nenhum resultado.
                                </TableCell>
                            </TableRow>
                        )}
                    </TableBody>
                </Table>
            </div>

            {/* Pagination */}
            <div className="flex items-center justify-end space-x-2 py-4">
                <Button onClick={() => handlePreviousPage()} disabled={page == 0}>Anterior</Button>
                <Button onClick={() => handleNextPage()} disabled={page >= totalPages - 1}>Próximo</Button>
            </div>
        </div>
    )
}

const OrdersSubTable = ({orders}: { orders: DisplayOrderItem[] }) => {
    return (
        <div className="p-4 bg-muted/50">
            <Table>
                <TableHeader>
                    <TableRow>
                        <TableHead>Produto</TableHead>
                        <TableHead className="text-center">Qtd.</TableHead>
                        <TableHead>Status</TableHead>
                        <TableHead>Hora</TableHead>
                        <TableHead>Observação</TableHead>
                        <TableHead className="text-right">Total Item</TableHead>
                        <TableHead>Garçom</TableHead>
                    </TableRow>
                </TableHeader>
                <TableBody>
                    {orders.map((order) => (
                        <TableRow key={order.id}>
                            <TableCell>{order.productName}</TableCell>
                            <TableCell className="text-center">{order.amount}</TableCell>
                            <TableCell>{OrderStatus[order.status]?.label || order.status}</TableCell>
                            <TableCell>{formatDateDisplay(order.orderedTime.toString())}</TableCell>
                            <TableCell className="truncate max-w-xs">{order.observation || "-"}</TableCell>
                            <TableCell className="text-right">R$ {order.productUnitPrice.toFixed(2)}</TableCell>
                            <TableCell>{order.waiterName}</TableCell>
                        </TableRow>
                    ))}
                </TableBody>
            </Table>
        </div>
    )
}
