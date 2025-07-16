import {ColumnDef, flexRender, getCoreRowModel, useReactTable} from "@tanstack/react-table";
import {OrderKanban} from "@/model/Interfaces";
import React, {useRef, useCallback} from 'react';
import {Table, TableBody, TableCell, TableHead, TableHeader, TableRow} from "@/components/ui/table";
import {useVirtualizer} from "@tanstack/react-virtual";
import {Loader2, Package} from "lucide-react";
import {Badge} from "@/components/ui/badge";

interface DataTableProps<TData, TValue> {
    columns: ColumnDef<TData, TValue>[];
    data: TData[];
    title: string;
    fetchNextPage: () => void;
    hasNextPage: boolean | undefined;
    isFetchingNextPage: boolean;
}

export function OrdersByWorkstationDataTable<TData extends OrderKanban, TValue>({
                                                                                    columns,
                                                                                    data,
                                                                                    title,
                                                                                    fetchNextPage,
                                                                                    hasNextPage,
                                                                                    isFetchingNextPage,
                                                                                }: DataTableProps<TData, TValue>) {
    const table = useReactTable({
        data,
        columns,
        getCoreRowModel: getCoreRowModel(),
        getRowCanExpand: (row) => row.original.additionalOrders && row.original.additionalOrders.length > 0,
    });

    const tableContainerRef = useRef<HTMLDivElement>(null);

    const {rows} = table.getRowModel();

    const virtualRows: { row: typeof rows[0]; isSubRow: boolean; virtualIndex: number }[] = [];
    let vIndex = 0;
    rows.forEach((row) => {
        virtualRows.push({row, isSubRow: false, virtualIndex: vIndex++});
        if (row.getIsExpanded()) {
            virtualRows.push({row, isSubRow: true, virtualIndex: vIndex++});
        }
    });

    const rowVirtualizer = useVirtualizer({
        count: hasNextPage ? virtualRows.length + 1 : virtualRows.length,
        getScrollElement: () => tableContainerRef.current,
        estimateSize: (index) => {
            const vRow = virtualRows[index];
            if (!vRow) return 64;
            if (vRow.isSubRow) {
                const additionalOrders = vRow.row.original.additionalOrders || [];
                return additionalOrders.length > 0
                    ? additionalOrders.length * 72 + 80
                    : 80;
            }
            return 64;
        },
        overscan: 10,
    });

    const handleScroll = useCallback((event: React.UIEvent<HTMLDivElement>) => {
        const {scrollTop, scrollHeight, clientHeight} = event.currentTarget;
        const scrollThreshold = 300;
        if (
            scrollHeight - scrollTop - clientHeight < scrollThreshold &&
            hasNextPage &&
            !isFetchingNextPage
        ) {
            fetchNextPage();
        }
    }, [hasNextPage, isFetchingNextPage, fetchNextPage]);

    const renderSubComponent = ({row}: { row: typeof rows[0] }) => {
        const additionalOrders = row.original.additionalOrders || [];

        return (
            <div className="p-4 bg-muted/50">
                <div className="rounded-xl border bg-card text-card-foreground shadow">
                    <div className="px-4 py-2 border-b">
                        <span className="text-lg font-semibold">Pedidos Adicionais</span>
                    </div>
                    <div className="overflow-x-auto">
                        <Table className="min-w-full divide-y divide-muted">
                            <TableHeader className="bg-muted/30">
                                <TableRow>
                                    <TableCell
                                        className="px-4 py-2 text-left text-xs font-medium text-muted-foreground uppercase">Produto</TableCell>
                                    <TableCell
                                        className="px-4 py-2 text-left text-xs font-medium text-muted-foreground uppercase">Observação</TableCell>
                                    <TableCell
                                        className="px-4 py-2 text-center text-xs font-medium text-muted-foreground uppercase">Quantidade</TableCell>
                                </TableRow>
                            </TableHeader>
                            <TableBody className="bg-card divide-y divide-muted">
                                {additionalOrders.length === 0 ? (
                                    <TableRow>
                                        <TableCell colSpan={3}
                                                   className="text-center py-4 text-muted-foreground text-sm">
                                            Nenhum pedido adicional.
                                        </TableCell>
                                    </TableRow>
                                ) : (
                                    additionalOrders.map(subOrder => (
                                        <TableRow key={subOrder.id}>
                                            <TableCell className="px-4 py-2 font-semibold flex items-center gap-2">
                                                <Package className="h-5 w-5 text-muted-foreground"/>
                                                {subOrder.productName}
                                            </TableCell>
                                            <TableCell className="px-4 py-2 text-sm text-muted-foreground">
                                                {subOrder.observation ||
                                                    <span className="italic text-muted-foreground/60">—</span>}
                                            </TableCell>
                                            <TableCell className="px-4 py-2 text-center">
                                                <Badge variant="secondary">Qtd: {subOrder.amount}</Badge>
                                            </TableCell>
                                        </TableRow>
                                    ))
                                )}
                            </TableBody>
                        </Table>
                    </div>
                </div>
            </div>
        );
    };

    return (
        <div className="flex flex-col w-1/3 min-w-[400px] h-[80vh]">
            <h2 className="text-2xl font-bold p-4 bg-secondary sticky top-0 z-10">{title} ({data.length})</h2>
            <div
                ref={tableContainerRef}
                onScroll={handleScroll}
                className="flex-1 overflow-auto rounded-md border"
            >
                <Table>
                    <TableHeader className="sticky top-0 z-10 bg-background">
                        {table.getHeaderGroups().map((headerGroup) => (
                            <TableRow key={headerGroup.id}>
                                {headerGroup.headers.map((header) => (
                                    <TableHead key={header.id}>
                                        {flexRender(header.column.columnDef.header, header.getContext())}
                                    </TableHead>
                                ))}
                            </TableRow>
                        ))}
                    </TableHeader>
                    <TableBody style={{height: `${rowVirtualizer.getTotalSize()}px`, position: 'relative'}}>
                        {rowVirtualizer.getVirtualItems().map((virtualRow) => {
                            const index = virtualRow.index;
                            if (hasNextPage && index === virtualRows.length) {
                                return (
                                    <TableRow
                                        key="loader-row"
                                        style={{
                                            height: `${virtualRow.size}px`,
                                            transform: `translateY(${virtualRow.start}px)`,
                                            position: "absolute",
                                            width: "100%",
                                        }}
                                    >
                                        <TableCell colSpan={columns.length} className="text-center">
                                            {hasNextPage && (
                                                <div className="flex justify-center items-center py-4">
                                                    <Loader2 className="mr-2 h-6 w-6 animate-spin"/>
                                                    <span>Carregando...</span>
                                                </div>
                                            )}
                                        </TableCell>
                                    </TableRow>
                                );
                            }
                            const vRow = virtualRows[index];
                            if (!vRow) return null;
                            if (vRow.isSubRow) {
                                return (
                                    <TableRow
                                        key={vRow.row.id + "-expanded"}
                                        style={{
                                            height: `${virtualRow.size}px`,
                                            transform: `translateY(${virtualRow.start}px)`,
                                            position: "absolute",
                                            width: "100%",
                                        }}
                                    >
                                        <TableCell colSpan={columns.length}
                                                   style={{padding: 0, background: "transparent"}}>
                                            {renderSubComponent({row: vRow.row})}
                                        </TableCell>
                                    </TableRow>
                                );
                            }
                            return (
                                <TableRow
                                    key={vRow.row.id}
                                    data-index={index}
                                    style={{
                                        height: `${virtualRow.size}px`,
                                        transform: `translateY(${virtualRow.start}px)`,
                                        position: "absolute",
                                        width: "100%",
                                        verticalAlign: "top",
                                    }}
                                >
                                    {vRow.row.getVisibleCells().map((cell) => (
                                        <TableCell key={cell.id} style={{verticalAlign: "top"}}>
                                            {flexRender(cell.column.columnDef.cell, cell.getContext())}
                                        </TableCell>
                                    ))}
                                </TableRow>
                            );
                        })}
                        {rows.length === 0 && !isFetchingNextPage && (
                            <TableRow>
                                <TableCell colSpan={columns.length} className="text-center h-24">
                                    Nenhum pedido nesta fila.
                                </TableCell>
                            </TableRow>
                        )}
                    </TableBody>
                </Table>
            </div>
        </div>
    );
}
