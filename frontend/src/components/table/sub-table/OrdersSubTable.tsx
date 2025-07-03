import {ColumnDef, flexRender, getCoreRowModel, useReactTable} from "@tanstack/react-table";
import {DisplayOrderItem, OrderStatus} from "@/model/Interfaces";
import {ChevronDown, ChevronRight} from "lucide-react";
import {formatDateDisplay} from "@/utils/operations/date-convertion";
import React from "react";
import {getExpandedRowModel} from "@tanstack/table-core";
import {Table, TableBody, TableCell, TableHead, TableHeader, TableRow} from "@/components/ui/table";
import {
    Dialog,
    DialogContent,
    DialogDescription,
    DialogHeader,
    DialogTitle,
    DialogTrigger
} from "@/components/ui/dialog";
import {Button} from "@/components/ui/button";
import {AddOrderForm} from "@/components/form/add/AddOrderForm";
import {
    Drawer, DrawerClose,
    DrawerContent,
    DrawerDescription, DrawerFooter,
    DrawerHeader,
    DrawerTitle,
    DrawerTrigger
} from "@/components/ui/drawer";

const getOrderColumns = (): ColumnDef<DisplayOrderItem>[] => [
    {
        id: "expander",
        header: () => null,
        cell: ({row}) => {
            const canExpand = row.original.additionalOrders && row.original.additionalOrders.length > 0;
            return canExpand ? (
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

    const [openAddOrder, setOpenAddOrder] = React.useState(false);
    const isDesktop = true;

    const columns = React.useMemo(() => getOrderColumns(), []);

    const table = useReactTable({
        data: orders,
        columns,
        getCoreRowModel: getCoreRowModel(),
        getExpandedRowModel: getExpandedRowModel(),
    });

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
                                        <OrdersSubTable
                                            orders={row.original.additionalOrders}
                                            guestTabId={guestTabId}
                                            parentOrderId={row.original.id}
                                        />
                                    </TableCell>
                                </TableRow>
                            )}
                        </React.Fragment>
                    ))}

                    {parentOrderId !== null && (
                        <TableRow>
                            <TableCell colSpan={columns.length} className="text-center">
                                {isDesktop ? (
                                    <Dialog open={openAddOrder} onOpenChange={setOpenAddOrder}>
                                        <DialogTrigger asChild>
                                            <Button variant="outline">Adicionar pedido</Button>
                                        </DialogTrigger>
                                        <DialogContent className="sm:max-w-[425px]">
                                            <DialogHeader>
                                                <DialogTitle>Novo Pedido</DialogTitle>
                                                <DialogDescription>
                                                    Preencha os dados do novo pedido.
                                                </DialogDescription>
                                            </DialogHeader>
                                            <AddOrderForm
                                                guestTabId={guestTabId}
                                                parentOrderId={parentOrderId}
                                                onSubmit={() => setOpenAddOrder(false)}
                                            />
                                        </DialogContent>
                                    </Dialog>
                                ) : (
                                    <Drawer open={openAddOrder} onOpenChange={setOpenAddOrder}>
                                        <DrawerTrigger asChild>
                                            <Button variant="outline">Adicionar pedido</Button>
                                        </DrawerTrigger>
                                        <DrawerContent>
                                            <DrawerHeader className="text-left">
                                                <DrawerTitle>Novo Pedido</DrawerTitle>
                                                <DrawerDescription>
                                                    Preencha os dados do novo pedido.
                                                </DrawerDescription>
                                            </DrawerHeader>
                                            <AddOrderForm
                                                guestTabId={guestTabId}
                                                parentOrderId={parentOrderId}
                                                onSubmit={() => setOpenAddOrder(false)}
                                            />
                                            <DrawerFooter className="pt-2">
                                                <DrawerClose asChild>
                                                    <Button variant="outline">Cancelar</Button>
                                                </DrawerClose>
                                            </DrawerFooter>
                                        </DrawerContent>
                                    </Drawer>
                                )}
                            </TableCell>
                        </TableRow>
                    )}
                </TableBody>
            </Table>
        </div>
    );
};
