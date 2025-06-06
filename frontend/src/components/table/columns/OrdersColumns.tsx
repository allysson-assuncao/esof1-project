'use client'

import {ColumnDef} from '@tanstack/react-table'
import {DisplayGuestTabItem, DisplayOrderItem} from "@/model/Interfaces";
import {Table, TableBody, TableCell, TableHead, TableHeader, TableRow} from "@/components/ui/table";
import {ChevronDown, ChevronRight} from "lucide-react";
import {DataTableColumnHeader} from "@/components/ui/data-table";

const OrdersSubTable = ({ orders }: { orders: DisplayOrderItem[] }) => {
  return (
    <div className="p-4 bg-muted/50">
      <Table>
        <TableHeader>
          <TableRow>
            <TableHead>Produto</TableHead>
            <TableHead className="text-center">Qtd.</TableHead>
            <TableHead>Garçom</TableHead>
            <TableHead>Status</TableHead>
            <TableHead>Observação</TableHead>
            <TableHead className="text-right">Total Item</TableHead>
          </TableRow>
        </TableHeader>
        <TableBody>
          {orders.map((order) => (
            <TableRow key={order.orderId}>
              <TableCell>{order.productName}</TableCell>
              <TableCell className="text-center">{order.amount}</TableCell>
              <TableCell>{order.waiterName}</TableCell>
              <TableCell>{order.orderStatus}</TableCell>
              <TableCell className="truncate max-w-xs">{order.observation || "-"}</TableCell>
              <TableCell className="text-right">R$ {order.productUnitPrice.toFixed(2)}</TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </div>
  )
}

export const ordersColumns: ColumnDef<DisplayGuestTabItem>[] = [
    // DrillDown
    {
        id: "expander",
        header: ({table}) => (
            <button
                onClick={() => table.toggleAllRowsExpanded(!table.getIsAllRowsExpanded())}
            >
                {table.getIsAllRowsExpanded() ? <ChevronDown/> : <ChevronRight/>}
            </button>
        ),
        cell: ({row}) => (
            <button
                onClick={() => row.toggleExpanded(!row.getIsExpanded())}
            >
                {row.getIsExpanded() ? <ChevronDown/> : <ChevronRight/>}
            </button>
        ),
    },

    // GuestTab
    {
        accessorKey: "guestTabId",
        header: ({column}) => <DataTableColumnHeader column={column} title="Comanda ID"/>,
    },
    {
        accessorKey: "totalPrice",
        header: ({column}) => <DataTableColumnHeader column={column} title="Total da Comanda"/>,
        cell: ({row}) => {
            const formatted = new Intl.NumberFormat("pt-BR", {
                style: "currency",
                currency: "BRL",
            }).format(row.original.totalPrice);
            return <div className="font-medium">{formatted}</div>;
        },
    },
    {
        header: "Qtd. Pedidos",
        cell: ({row}) => row.original.orders.length,
    },
]

/*export const ordersColumns: ColumnDef<DisplayGuestTabItem>[] = [
    {
      id: "select",
      header: ({ table }) => (
        <Checkbox
          checked={table.getIsAllPageRowsSelected()}
          onCheckedChange={(value) => table.toggleAllPageRowsSelected(!!value)}
          aria-label="Select all"
        />
      ),
      cell: ({ row }) => (
        <Checkbox
          checked={row.getIsSelected()}
          onCheckedChange={(value) => row.toggleSelected(!!value)}
          aria-label="Select row"
        />
      ),
      enableSorting: false,
      enableHiding: false,
    },
    {
        accessorKey: "id",
        header: () => <TableHeader title="ID Pedido" />,
        cell: ({ row }) => <div>{row.original.id}</div>,
    },
    {
        accessorKey: "productName",
        header: () => <TableHeader title="Produto" />,
        cell: ({ row }) => <div>{row.original.productName}</div>,
    },
    {
        accessorKey: "amount",
        header: () => <TableHeader title="Qtd." />,
        cell: ({ row }) => <div className="text-center">{row.original.amount}</div>,
    },
    {
        accessorKey: "unitPrice",
        header: () => <TableHeader title="Preço Unit." />,
        cell: ({ row }) => <div>R$ {row.original.productUnitPrice.toFixed(2)}</div>,
    },
    {
        accessorKey: "totalItemPrice",
        header: () => <TableHeader title="Total Item" />,
        cell: ({ row }) => <div>R$ {row.original.orderTotalPrice.toFixed(2)}</div>,
    },
    {
        accessorKey: "orderStatus",
        header: () => <TableHeader title="Status" />,
        cell: ({ row }) => {
            const statusLabel = OrderStatus[row.original.orderStatus as keyof typeof OrderStatus]?.label || row.original.orderStatus;
            // Maybe add some color
            return <div>{statusLabel}</div>;
        },
        filterFn: (row, id, value) => {
          return value.includes(row.getValue(id));
        },
    },
    {
        accessorKey: "waiterName",
        header: () => <TableHeader title="Garçom" />,
        cell: ({ row }) => <div>{row.original.waiterName}</div>,
    },
    {
        accessorKey: "orderedTime",
        header: () => <TableHeader title="Horário" />,
        cell: ({ row }) => <div>{new Date(row.original.orderedTime).toLocaleString('pt-BR')}</div>,
    },
    {
        accessorKey: "observation",
        header: "Observação",
        cell: ({ row }) => <div className="truncate max-w-xs" title={row.original.observation}>{row.original.observation || '-'}</div>,
    },
    {
      accessorKey: "guestTabId",
      header: "ID Comanda",
    },
];*/
