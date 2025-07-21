import React from "react";
import {
    useReactTable,
    getCoreRowModel,
    ColumnDef,
    flexRender,
} from "@tanstack/react-table";
import {Table, TableHeader, TableBody, TableRow, TableCell} from "@/components/ui/table";
import {PaymentItem} from "@/model/Interfaces";

interface SalesReportDataTableProps {
    data: PaymentItem[];
    columns: ColumnDef<PaymentItem>[];
}

export function PerformanceReportDataTable({data, columns}: SalesReportDataTableProps) {
    const table = useReactTable({
        data,
        columns,
        getCoreRowModel: getCoreRowModel(),
    });

    return (
        <div className="rounded-md border overflow-x-auto">
            <Table className="min-w-full" style={{tableLayout: 'fixed', width: '100%'}}>
                <colgroup>
                    {columns.map((_, idx) => (
                        <col key={idx} style={{width: `${100 / columns.length}%`}}/>
                    ))}
                </colgroup>
                <TableHeader>
                    {table.getHeaderGroups().map(headerGroup => (
                        <TableRow key={headerGroup.id}>
                            {headerGroup.headers.map(header => (
                                <TableCell key={header.id} className="text-center font-semibold">
                                    {header.isPlaceholder ? null : flexRender(header.column.columnDef.header, header.getContext())}
                                </TableCell>
                            ))}
                        </TableRow>
                    ))}
                </TableHeader>
                <TableBody>
                    {table.getRowModel().rows.length ? (
                        table.getRowModel().rows.map(row => (
                            <TableRow key={row.id}>
                                {row.getVisibleCells().map(cell => (
                                    <TableCell key={cell.id} className="text-center">
                                        {flexRender(cell.column.columnDef.cell, cell.getContext())}
                                    </TableCell>
                                ))}
                            </TableRow>
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
    );
}
