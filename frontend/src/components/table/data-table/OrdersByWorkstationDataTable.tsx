import {ColumnDef, flexRender, getCoreRowModel, useReactTable} from "@tanstack/react-table";
import {OrderKanban} from "@/model/Interfaces";
import React, {useRef, useCallback} from 'react';
import {Table, TableBody, TableCell, TableHead, TableHeader, TableRow} from "@/components/ui/table";
import {useVirtualizer} from "@tanstack/react-virtual";
import {Loader2, Package} from "lucide-react";
import {Card, CardContent, CardHeader, CardTitle} from "@/components/ui/card";
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

    const rowVirtualizer = useVirtualizer({
        count: hasNextPage ? rows.length + 1 : rows.length,
        getScrollElement: () => tableContainerRef.current,
        estimateSize: (index) => {
            const row = rows[index];
            const baseHeight = 64;
            if (row?.getIsExpanded()) {
                const subItemsHeight = (row.original.additionalOrders.length * 48) + 16;
                return baseHeight + subItemsHeight;
            }
            return baseHeight;
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
                                <TableCell className="px-4 py-2 text-left text-xs font-medium text-muted-foreground uppercase">Produto</TableCell>
                                <TableCell className="px-4 py-2 text-left text-xs font-medium text-muted-foreground uppercase">Observação</TableCell>
                                <TableCell className="px-4 py-2 text-center text-xs font-medium text-muted-foreground uppercase">Quantidade</TableCell>
                            </TableRow>
                            </TableHeader>
                            <TableBody className="bg-card divide-y divide-muted">
                            {additionalOrders.length === 0 ? (
                                <TableRow>
                                    <TableCell colSpan={3} className="text-center py-4 text-muted-foreground text-sm">
                                        Nenhum pedido adicional.
                                    </TableCell>
                                </TableRow>
                            ) : (
                                additionalOrders.map(subOrder => (
                                    <TableRow key={subOrder.id}>
                                        <TableCell className="px-4 py-2 font-semibold flex items-center gap-2">
                                            <Package className="h-4 w-4 text-muted-foreground"/>
                                            {subOrder.productName}
                                        </TableCell>
                                        <TableCell className="px-4 py-2 text-sm text-muted-foreground">
                                            {subOrder.observation ||
                                                <span className="italic text-muted-foreground/60">—</span>}
                                        </TableCell>
                                        <TableCell className="px-4 py-2 text-center">
                                                <span
                                                    className="inline-block rounded bg-muted px-2 py-1 text-xs font-medium">
                                                    {subOrder.amount}
                                                </span>
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
                            const isLoaderRow = virtualRow.index > rows.length - 1;
                            const row = rows[virtualRow.index];

                            return isLoaderRow ? (
                                <TableRow key="loader-row" style={{
                                    height: `${virtualRow.size}px`,
                                    transform: `translateY(${virtualRow.start}px)`,
                                    position: 'absolute',
                                    width: '100%'
                                }}>
                                    <TableCell colSpan={columns.length} className="text-center">
                                        {hasNextPage && <div className="flex justify-center items-center py-4"><Loader2
                                            className="mr-2 h-6 w-6 animate-spin"/><span>Carregando...</span></div>}
                                    </TableCell>
                                </TableRow>
                            ) : (
                                <React.Fragment key={row.id}>
                                    <TableRow
                                        data-index={virtualRow.index}
                                        style={{
                                            height: `${virtualRow.size}px`,
                                            transform: `translateY(${virtualRow.start}px)`,
                                            position: 'absolute',
                                            width: '100%'
                                        }}
                                    >
                                        {row.getVisibleCells().map((cell) => (
                                            <TableCell key={cell.id}>
                                                {flexRender(cell.column.columnDef.cell, cell.getContext())}
                                            </TableCell>
                                        ))}
                                    </TableRow>
                                    {row.getIsExpanded() && (
                                        <TableRow
                                            style={{
                                                transform: `translateY(${virtualRow.start}px)`,
                                                position: 'absolute',
                                                width: '100%',
                                                top: `${virtualRow.size}px`,
                                            }}
                                        >
                                            <TableCell colSpan={columns.length} className="p-0">
                                                {renderSubComponent({row})}
                                            </TableCell>
                                        </TableRow>
                                    )}
                                </React.Fragment>
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
