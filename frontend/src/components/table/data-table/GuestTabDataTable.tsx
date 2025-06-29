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
import {
    Dialog,
    DialogContent,
    DialogDescription,
    DialogHeader,
    DialogTitle,
    DialogTrigger
} from "@/components/ui/dialog";
import {
    Drawer,
    DrawerClose,
    DrawerContent,
    DrawerDescription,
    DrawerFooter,
    DrawerHeader,
    DrawerTitle,
    DrawerTrigger
} from "@/components/ui/drawer";
import {AddOrderForm} from "@/components/form/add/AddOrderForm";
import {AddGuestTabForm} from "@/components/form/add/AddGuestTabForm";
import {getExpandedRowModel} from "@tanstack/table-core";
import {ChevronDown, ChevronRight} from "lucide-react";

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
    const [openAddGuestTab, setOpenAddGuestTab] = useState(false);

    /*const isDesktop = useMediaQuery("(min-width: 768px)");*/
    const isDesktop = true;

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
            label: tab.userName,
        })) ?? [];

    const guestTabStatusOptions = Object.entries(GuestTabStatus).map(([, {value, label}]) => ({
        value,
        label,
    }));

    const orderStatusOptions = Object.entries(OrderStatus).map(([, {value, label}]) => ({
        value,
        label,
    }));

    const addGuestTabButtonRow = (
        <TableRow>
            <TableCell colSpan={columns.length} className="text-center">
                {isDesktop ? (
                    <Dialog open={openAddGuestTab} onOpenChange={setOpenAddGuestTab}>
                        <DialogTrigger asChild>
                            <Button variant="outline">Adicionar comanda</Button>
                        </DialogTrigger>
                        <DialogContent className="sm:max-w-[425px]">
                            <DialogHeader>
                                <DialogTitle>Nova Comanda</DialogTitle>
                                <DialogDescription>
                                    Preencha os dados da nova comanda.
                                </DialogDescription>
                            </DialogHeader>
                            <AddGuestTabForm
                                localTableId={localTableId}
                                onSubmit={() => setOpenAddGuestTab(false)}/>
                        </DialogContent>
                    </Dialog>
                ) : (
                    <Drawer open={openAddGuestTab} onOpenChange={setOpenAddGuestTab}>
                        <DrawerTrigger asChild>
                            <Button variant="outline">Adicionar comanda</Button>
                        </DrawerTrigger>
                        <DrawerContent>
                            <DrawerHeader className="text-left">
                                <DrawerTitle>Nova Comanda</DrawerTitle>
                                <DrawerDescription>
                                    Preencha os dados da nova comanda.
                                </DrawerDescription>
                            </DrawerHeader>
                            <AddGuestTabForm
                                localTableId={localTableId}
                                onSubmit={() => setOpenAddGuestTab(false)}/>
                            <DrawerFooter className="pt-2">
                                <DrawerClose asChild>
                                    <Button variant="outline">Cancelar</Button>
                                </DrawerClose>
                            </DrawerFooter>
                        </DrawerContent>
                    </Drawer>
                )}
            </TableCell>
        </TableRow>
    );

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
                            <>
                                {table.getRowModel().rows.map((row) => (
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
                                                <TableCell colSpan={row.getVisibleCells().length} className="p-0">
                                                    <OrdersSubTable
                                                        orders={row.original.orders}
                                                        guestTabId={row.original.id}
                                                    />
                                                </TableCell>
                                            </TableRow>
                                        )}
                                    </React.Fragment>
                                ))}
                                {addGuestTabButtonRow}
                            </>
                        ) : (
                            <>
                                <TableRow>
                                    <TableCell colSpan={columns.length} className="h-24 text-center">
                                        Nenhum resultado.
                                    </TableCell>
                                </TableRow>
                                {addGuestTabButtonRow}
                            </>
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

const getOrderColumns = (): ColumnDef<DisplayOrderItem>[] => [
    {
        id: "expander",
        header: () => null,
        cell: ({row}) => {
            const canExpand = row.original.additionalOrders && row.original.additionalOrders.length > 0;
            return canExpand ? (
                <button {...{onClick: () => row.toggleExpanded(!row.getIsExpanded())}}>
                    {row.getIsExpanded() ? <ChevronDown/> : <ChevronRight/>}
                </button>
            ) : <span className="inline-block w-4"></span>;
        },
        size: 20,
    },
    {accessorKey: "productName", header: "Produto"},
    {accessorKey: "amount", header: "Qtd.", cell: ({row}) => <div className="text-center">{row.original.amount}</div>},
    {
        accessorKey: "status",
        header: "Status",
        cell: ({row}) => OrderStatus[row.original.status as keyof typeof OrderStatus]?.label || row.original.status
    },
    {
        accessorKey: "orderedTime",
        header: "Hora",
        cell: ({row}) => formatDateDisplay(row.original.orderedTime.toString())
    },
    {
        accessorKey: "observation",
        header: "Observação",
        cell: ({row}) => <p className="truncate max-w-xs">{row.original.observation || "-"}</p>
    },
    {
        accessorKey: "productUnitPrice",
        header: "Total Item",
        cell: ({row}) => {
            const total = row.original.productUnitPrice * row.original.amount;
            return <div className="text-right">R$ {total.toFixed(2)}</div>
        }
    },
    {accessorKey: "waiterName", header: "Garçom"},
];

const OrdersSubTable = ({
                            orders,
                            guestTabId,
                            parentOrderId = null
                        }: {
    orders: DisplayOrderItem[],
    guestTabId: number,
    parentOrderId?: number | null
}) => {

    const [openAddOrder, setOpenAddOrder] = React.useState(false);
    const isDesktop = true;

    const columns = React.useMemo(() => getOrderColumns(), []);

    const table = useReactTable({
        data: orders,
        columns,
        getCoreRowModel: getCoreRowModel(),
        getExpandedRowModel: getExpandedRowModel(),
    });

    return (
        <div className="p-4 bg-muted/50 pl-6">
            <Table>
                <TableHeader>
                    {table.getHeaderGroups().map(headerGroup => (
                        <TableRow key={headerGroup.id}>
                            {headerGroup.headers.map(header => (
                                <TableHead key={header.id}
                                           style={{width: header.getSize() !== 150 ? `${header.getSize()}px` : undefined}}>
                                    {flexRender(header.column.columnDef.header, header.getContext())}
                                </TableHead>
                            ))}
                        </TableRow>
                    ))}
                </TableHeader>
                <TableBody>
                    {table.getRowModel().rows.map(row => (
                        <React.Fragment key={row.id}>
                            <TableRow>
                                {row.getVisibleCells().map(cell => (
                                    <TableCell key={cell.id}>
                                        {flexRender(cell.column.columnDef.cell, cell.getContext())}
                                    </TableCell>
                                ))}
                            </TableRow>
                            {row.getIsExpanded() && (
                                <TableRow>
                                    <TableCell colSpan={columns.length} className="p-0">
                                        <OrdersSubTable
                                            orders={row.original.additionalOrders}
                                            guestTabId={guestTabId}
                                            parentOrderId={row.original.id}
                                        />
                                    </TableCell>
                                </TableRow>
                            )}
                        </React.Fragment>
                    ))}

                    <TableRow>
                        <TableCell colSpan={columns.length} className="text-center">
                            {isDesktop ? (
                                <Dialog open={openAddOrder} onOpenChange={setOpenAddOrder}>
                                    <DialogTrigger asChild>
                                        <Button variant="outline">Adicionar pedido</Button>
                                    </DialogTrigger>
                                    <DialogContent className="sm:max-w-[425px]">
                                        <DialogHeader>
                                            <DialogTitle>Novo Pedido</DialogTitle>
                                            <DialogDescription>
                                                Preencha os dados do novo pedido.
                                            </DialogDescription>
                                        </DialogHeader>
                                        <AddOrderForm
                                            guestTabId={guestTabId}
                                            parentOrderId={parentOrderId}
                                            onSubmit={() => setOpenAddOrder(false)}
                                        />
                                    </DialogContent>
                                </Dialog>
                            ) : (
                                <Drawer open={openAddOrder} onOpenChange={setOpenAddOrder}>
                                    <DrawerTrigger asChild>
                                        <Button variant="outline">Adicionar pedido</Button>
                                    </DrawerTrigger>
                                    <DrawerContent>
                                        <DrawerHeader className="text-left">
                                            <DrawerTitle>Novo Pedido</DrawerTitle>
                                            <DrawerDescription>
                                                Preencha os dados do novo pedido.
                                            </DrawerDescription>
                                        </DrawerHeader>
                                        <AddOrderForm
                                            guestTabId={guestTabId}
                                            parentOrderId={parentOrderId}
                                            onSubmit={() => setOpenAddOrder(false)}
                                        />
                                        <DrawerFooter className="pt-2">
                                            <DrawerClose asChild>
                                                <Button variant="outline">Cancelar</Button>
                                            </DrawerClose>
                                        </DrawerFooter>
                                    </DrawerContent>
                                </Drawer>
                            )}
                        </TableCell>
                    </TableRow>
                </TableBody>
            </Table>
        </div>
    );
};
