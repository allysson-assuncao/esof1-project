import {ColumnDef, flexRender, getCoreRowModel, useReactTable} from "@tanstack/react-table";
import {DisplayOrderItem, OrderStatus} from "@/model/Interfaces";
import {ChevronDown, ChevronRight} from "lucide-react";
import {formatDateDisplay} from "@/utils/operations/date-convertion";
import React, {useEffect} from "react";
import {getExpandedRowModel} from "@tanstack/table-core";
import {Table, TableBody, TableCell, TableHead, TableHeader, TableRow} from "@/components/ui/table";
import {AddOrderDialog} from "@/components/dialog/AddOrderDialog";
import {AdditionalOrdersContainer} from "@/components/container/AdditionalOrdersContainer";

const getOrderColumns = (parentOrderId: number | null): ColumnDef<DisplayOrderItem>[] => [
    {
        id: "expander",
        header: () => null,
        cell: ({row}) => {
            // Um item principal (onde a tabela pai tem parentOrderId nulo) SEMPRE pode ser expandido
            // para mostrar o contêiner de adicionais.
            const isMainOrderItem = parentOrderId === null;
            if (isMainOrderItem) {
                return (
                    <button {...{onClick: () => row.toggleExpanded(!row.getIsExpanded())}}>
                        {row.getIsExpanded() ? <ChevronDown/> : <ChevronRight/>}
                    </button>
                );
            }

            // Um item adicional só pode ser expandido se ele mesmo tiver filhos.
            const canExpandAdditional = row.original.additionalOrders && row.original.additionalOrders.length > 0;
            return canExpandAdditional ? (
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

export const OrdersSubTable = ({
                                   orders,
                                   guestTabId,
                                   parentOrderId = null
                               }: {
    orders: DisplayOrderItem[],
    guestTabId: number,
    parentOrderId?: number | null
}) => {

    const columns = React.useMemo(() => getOrderColumns(parentOrderId), [parentOrderId]);

    const table = useReactTable({
        data: orders,
        columns,
        getCoreRowModel: getCoreRowModel(),
        getExpandedRowModel: getExpandedRowModel(),
    });

    useEffect(() => {
        console.log(`[useEffect] A instância da tabela mudou. O parentOrderId AGORA é:`, parentOrderId);
    }, [parentOrderId]);

    return (
        <div className="p-4 bg-muted/40 pl-6">
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
                                        <AdditionalOrdersContainer
                                            guestTabId={guestTabId}
                                            parentOrder={row.original}
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
