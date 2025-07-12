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
        return (
            <div className="p-4 bg-muted/50">
                <Card>
                    <CardHeader>
                        <CardTitle className="text-lg">Pedidos Adicionais</CardTitle>
                    </CardHeader>
                    <CardContent>
                        <ul className="space-y-3">
                            {row.original.additionalOrders.map(subOrder => (
                                <li key={subOrder.id} className="flex items-center gap-4 p-2 border-b last:border-b-0">
                                    <Package className="h-5 w-5 text-muted-foreground"/>
                                    <div className="flex-1">
                                        <p className="font-semibold">{subOrder.productName}</p>
                                        {subOrder.observation && (
                                            <p className="text-sm text-muted-foreground">{subOrder.observation}</p>
                                        )}
                                    </div>
                                    <Badge variant="secondary">Qtd: {subOrder.amount}</Badge>
                                </li>
                            ))}
                        </ul>
                    </CardContent>
                </Card>
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
