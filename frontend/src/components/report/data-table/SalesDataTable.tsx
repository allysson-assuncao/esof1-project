import React, {SetStateAction, useState} from 'react'
import {
    useReactTable,
    getCoreRowModel, ColumnDef, SortingState, getPaginationRowModel, getSortedRowModel, getFilteredRowModel, flexRender
} from '@tanstack/react-table'
import {Table, TableHeader, TableBody, TableRow, TableCell} from '@/components/ui/table'
import {Button} from '@/components/ui/button'
import {PaymentFilters, PaymentGroup, PaymentMetrics, SimplePaymentMethod} from "@/model/Interfaces";
import {DatePicker} from "@/components/ui/date-picker";
import {PaymentsSubTable} from "@/components/report/sub-table/PaymentsSubTable";
import {Skeleton} from "@/components/ui/skeleton";
import {Card, CardContent, CardHeader, CardTitle} from "@/components/ui/card";
import {Input} from "@/components/ui/input";
import {MultiSelect} from "@/components/ui/multi-select";
import {useQuery} from "react-query";
import {fetchSimplePaymentMethods} from "@/services/paymentMethodService";

interface DataTableProps<TValue> {
    columns: ColumnDef<PaymentGroup, TValue>[];
    data: PaymentGroup[];
    setPage: (update: (prev: number) => number) => void;
    selectedFilters: PaymentFilters;
    setSelectedFilters: (filters: SetStateAction<PaymentFilters>) => void;
    page: number;
    totalPages: number;
    setPageSize: (size: number) => void;
    metrics?: PaymentMetrics;
    isMetricsLoading: boolean;
}

const MetricsDisplay = ({metrics, isLoading}: { metrics?: PaymentMetrics; isLoading: boolean; }) => {
    const formatCurrency = (value?: number) => {
        if (value === undefined || value === null) return <Skeleton className="h-6 w-24"/>;
        return new Intl.NumberFormat("pt-BR", {style: "currency", currency: "BRL"}).format(value);
    };

    if (isLoading) {
        return (
            <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
                {[...Array(4)].map((_, i) => (
                    <Card key={i}>
                        <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                            <Skeleton className="h-5 w-2/3"/>
                        </CardHeader>
                        <CardContent><Skeleton className="h-8 w-1/2"/></CardContent>
                    </Card>
                ))}
            </div>
        );
    }

    return (
        <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
            <Card>
                <CardHeader>
                    <CardTitle>
                        Faturamento Total
                    </CardTitle>
                </CardHeader>
                <CardContent><p className="text-2xl font-bold">{formatCurrency(metrics?.totalRevenue)}</p>
                </CardContent>
            </Card>
            <Card>
                <CardHeader>
                    <CardTitle>
                        Total de Pagamentos
                    </CardTitle>
                </CardHeader>
                <CardContent><p className="text-2xl font-bold">{metrics?.totalPayments ?? '...'}</p>
                </CardContent>
            </Card>
            <Card>
                <CardHeader>
                    <CardTitle>
                        Ticket Médio
                    </CardTitle>
                </CardHeader>
                <CardContent><p className="text-2xl font-bold">{formatCurrency(metrics?.averageTicket)}</p>
                </CardContent>
            </Card>
        </div>
    );
};

export function SalesDataTable<TValue>({
                                           columns,
                                           data,
                                           setPage,
                                           selectedFilters,
                                           setSelectedFilters,
                                           page,
                                           totalPages,
                                           metrics,
                                           isMetricsLoading
                                       }: DataTableProps<TValue>) {
    const [sorting, setSorting] = useState<SortingState>([]);

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

    const {data: simplePaymentMethods, isLoading: isLoadingPaymentMethods} = useQuery<SimplePaymentMethod[]>(
        'simplePaymentMethods', fetchSimplePaymentMethods
    );

    const paymentMethodOptions =
        simplePaymentMethods?.map((method) => ({
            label: method.name,
            value: String(method.id),
        })) ?? [];

    const handlePreviousPage = () => {
        table.previousPage()
        setPage((prev) => prev - 1)
    }

    const handleNextPage = () => {
        table.nextPage()
        setPage((prev) => prev + 1)
    }

    return (
        <div>
            {/* Filters */}
            <div
                className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 5xl:grid-cols-4 gap-4 md:gap-6 py-4">
                {/*<MetricsDisplay metrics={metrics} isLoading={isMetricsLoading}/>*/}
                <DatePicker
                    onDateSelected={(startDate) => setSelectedFilters({...selectedFilters, startDate})}
                />
                <DatePicker
                    onDateSelected={(endDate) => setSelectedFilters({...selectedFilters, endDate})}
                />
                <Input
                    type="time"
                    value={selectedFilters.businessDayStartTime}
                    onChange={(e) => setSelectedFilters(f => ({...f, businessDayStartTime: e.target.value}))}
                    className="max-w-sm"
                />
                <Input
                    type="time"
                    value={selectedFilters.businessDayEndTime}
                    onChange={(e) => setSelectedFilters(f => ({...f, businessDayEndTime: e.target.value}))}
                    className="max-w-sm"
                />
                <MultiSelect
                    options={paymentMethodOptions}
                    onValueChange={(selectedValues) =>
                        setSelectedFilters({
                            ...selectedFilters,
                            paymentMethodIds: selectedValues.map((id) => Number(id)),
                        })
                    }
                    defaultValue={
                        selectedFilters.paymentMethodIds ?? []
                            ? selectedFilters.paymentMethodIds?.map(String)
                            : []
                    }
                    placeholder="Métodos de Pag."
                    disabled={isLoadingPaymentMethods}
                    animation={2}
                    maxCount={2}
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
                                                <TableCell colSpan={columns.length} className="p-0">
                                                    <PaymentsSubTable payments={row.original.payments}/>
                                                </TableCell>
                                            </TableRow>
                                        )}
                                    </React.Fragment>
                                ))}
                            </>
                        ) : (
                            <>
                                <TableRow>
                                    <TableCell colSpan={columns.length} className="h-24 text-center">
                                        Nenhum resultado.
                                    </TableCell>
                                </TableRow>
                            </>
                        )}
                    </TableBody>
                </Table>
            </div>

            {/* Pagination */}
            <div className="flex items-center justify-end space-x-2 py-4">
                <Button onClick={() => handlePreviousPage()} disabled={page === 0}>Anterior</Button>
                <span>Página {page + 1} de {totalPages}</span>
                <Button onClick={() => handleNextPage()} disabled={page >= totalPages - 1}>Próximo</Button>
            </div>
        </div>
    );
}
