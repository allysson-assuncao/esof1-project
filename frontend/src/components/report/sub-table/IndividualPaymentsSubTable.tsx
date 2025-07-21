import React from "react";
import {IndividualPayment} from "@/model/Interfaces";
import {ColumnDef, flexRender, getCoreRowModel, useReactTable} from "@tanstack/react-table";
import {Table, TableBody, TableCell, TableHead, TableHeader, TableRow} from "@/components/ui/table";

const individualPaymentColumns = (): ColumnDef<IndividualPayment>[] => [
    { accessorKey: "id", header: "ID" },
    { accessorKey: "paymentMethodName", header: "Método" },
    { accessorKey: "amount", header: "Valor", cell: ({ row }) => `R$ ${row.original.amount.toFixed(2)}` },
];

export const IndividualPaymentsSubTable = ({ individualPayments }: { individualPayments: IndividualPayment[] }) => {
    const columns = React.useMemo(() => individualPaymentColumns(), []);
    const table = useReactTable({
        data: individualPayments,
        columns,
        getCoreRowModel: getCoreRowModel()
    });

    return (
        <div className="p-4 bg-muted/40 pl-20">
            <Table>
                <TableHeader>
                    {table.getHeaderGroups().map(headerGroup => (
                        <TableRow key={headerGroup.id}>
                            {headerGroup.headers.map(header => (
                                <TableHead key={header.id}>{flexRender(header.column.columnDef.header, header.getContext())}</TableHead>
                            ))}
                        </TableRow>
                    ))}
                </TableHeader>
                <TableBody>
                    {table.getRowModel().rows.map(row => (
                        <TableRow key={row.id}>
                            {row.getVisibleCells().map(cell => (
                                <TableCell key={cell.id}>{flexRender(cell.column.columnDef.cell, cell.getContext())}</TableCell>
                            ))}
                        </TableRow>
                    ))}
                </TableBody>
            </Table>
        </div>
    );
};
