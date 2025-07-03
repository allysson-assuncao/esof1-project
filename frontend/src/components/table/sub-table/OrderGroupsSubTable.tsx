import {ColumnDef, flexRender, getCoreRowModel, useReactTable} from "@tanstack/react-table";
import {DisplayOrderGroup} from "@/model/Interfaces";
import {ChevronDown, ChevronRight} from "lucide-react";
import {formatDateDisplay} from "@/utils/operations/date-convertion";
import React from "react";
import {getExpandedRowModel} from "@tanstack/table-core";
import {Table, TableBody, TableCell, TableHead, TableHeader, TableRow} from "@/components/ui/table";
import {OrdersSubTable} from "@/components/table/sub-table/OrdersSubTable";

const getOrderGroupColumns = (): ColumnDef<DisplayOrderGroup>[] => [
    {
        id: 'expander',
        header: () => null,
        cell: ({ row }) => (
            <button onClick={() => row.toggleExpanded(!row.getIsExpanded())}>
                {row.getIsExpanded() ? <ChevronDown /> : <ChevronRight />}
            </button>
        ),
        size: 20,
    },
    {
        accessorKey: 'representativeTime',
        header: 'Hora do Pedido',
        cell: ({ row }) => formatDateDisplay(row.original.representativeTime)
    },
    {
        accessorKey: 'numberOfItems',
        header: 'Qtd. Itens',
        cell: ({ row }) => <div className="text-center">{row.original.numberOfItems}</div>
    },
    {
        accessorKey: 'groupTotalPrice',
        header: 'Total do Pedido',
        cell: ({ row }) => {
            const formatted = new Intl.NumberFormat("pt-BR", {
                style: "currency",
                currency: "BRL",
            }).format(row.original.groupTotalPrice);
            return <div className="text-right font-medium">{formatted}</div>;
        }
    },
];

export const OrderGroupsSubTable = ({ orderGroups, guestTabId }: { orderGroups: DisplayOrderGroup[], guestTabId: number }) => {
    const columns = React.useMemo(() => getOrderGroupColumns(), []);

    const table = useReactTable({
        data: orderGroups,
        columns,
        getCoreRowModel: getCoreRowModel(),
        getExpandedRowModel: getExpandedRowModel(),
    });

    return (
        <div className="p-4 bg-muted/70 pl-12">
            <Table>
                <TableHeader>
                    {table.getHeaderGroups().map(headerGroup => (
                        <TableRow key={headerGroup.id}>
                            {headerGroup.headers.map(header => (
                                <TableHead key={header.id}>
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
                                            orders={row.original.orders}
                                            guestTabId={guestTabId}
                                        />
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
