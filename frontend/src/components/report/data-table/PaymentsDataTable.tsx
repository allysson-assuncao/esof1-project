import React, {SetStateAction, useState} from 'react'
import {
    useReactTable,
    getCoreRowModel, ColumnDef, SortingState, getPaginationRowModel, getSortedRowModel, getFilteredRowModel, flexRender
} from '@tanstack/react-table'
import {Table, TableHeader, TableBody, TableRow, TableCell} from '@/components/ui/table'
import {Button} from '@/components/ui/button'
import {PaymentFilters, PaymentGroup} from "@/model/Interfaces";
import {DatePicker} from "@/components/ui/date-picker";
import {PaymentsSubTable} from "@/components/report/sub-table/PaymentsSubTable";

interface DataTableProps<TValue> {
    columns: ColumnDef<PaymentGroup, TValue>[];
    data: PaymentGroup[];
    setPage: (page: (prev: number) => number) => void;
    selectedFilters: PaymentFilters;
    setSelectedFilters: (filters: SetStateAction<PaymentFilters>) => void;
    page: number;
    totalPages: number;
    setPageSize: (page: number) => void;
}

export function PaymentsDataTable<TValue>({
                                              columns,
                                              data,
                                              setPage,
                                              selectedFilters,
                                              setSelectedFilters,
                                              page,
                                              totalPages,
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
                <DatePicker
                    onDateSelected={(startDate) => setSelectedFilters({...selectedFilters, startDate})}
                />
                <DatePicker
                    onDateSelected={(endDate) => setSelectedFilters({...selectedFilters, endDate})}
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
                <Button onClick={() => handlePreviousPage()} disabled={page == 0}>Anterior</Button>
                <Button onClick={() => handleNextPage()} disabled={page >= totalPages - 1}>Próximo</Button>
            </div>
        </div>
    );
}
