import React from "react";
import {ReportPayment} from "@/model/Interfaces";
import {ColumnDef, flexRender, getCoreRowModel, useReactTable} from "@tanstack/react-table";
import {getExpandedRowModel} from "@tanstack/table-core";
import {Table, TableBody, TableCell, TableHead, TableHeader, TableRow} from "@/components/ui/table";
import {IndividualPaymentsSubTable} from "@/components/report/sub-table/IndividualPaymentsSubTable";
import {ChevronDown, ChevronRight} from "lucide-react";

const reportPaymentColumns = (): ColumnDef<ReportPayment>[] => [
    { id: "expander", cell: ({ row }) => <button onClick={() => row.toggleExpanded(!row.getIsExpanded())}>{row.getIsExpanded() ? <ChevronDown /> : <ChevronRight />}</button> },
    { accessorKey: "id", header: "ID Pag." },
    { accessorKey: "status", header: "Status" },
    { accessorKey: "totalAmount", header: "Valor", cell: ({ row }) => `R$ ${row.original.totalAmount.toFixed(2)}` },
    { accessorKey: "createdAt", header: "Hora", cell: ({ row }) => new Date(row.original.createdAt).toLocaleTimeString('pt-BR') },
];

export const PaymentsSubTable = ({ payments }: { payments: ReportPayment[] }) => {
    const columns = React.useMemo(() => reportPaymentColumns(), []);
    const table = useReactTable({
        data: payments,
        columns,
        getCoreRowModel: getCoreRowModel(),
        getExpandedRowModel: getExpandedRowModel()
    });

    return (
        <div className="p-4 bg-muted/20 pl-12">
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
                        <React.Fragment key={row.id}>
                            <TableRow>
                                {row.getVisibleCells().map(cell => (
                                    <TableCell key={cell.id}>{flexRender(cell.column.columnDef.cell, cell.getContext())}</TableCell>
                                ))}
                            </TableRow>
                            {row.getIsExpanded() && (
                                <TableRow>
                                    <TableCell colSpan={columns.length} className="p-0">
                                        <IndividualPaymentsSubTable individualPayments={row.original.individualPayments} />
                                    </TableCell>
                                </TableRow>
                            )}
                        </React.Fragment>
                    ))}
                </TableBody>
            </Table>
        </div>
    );
};
