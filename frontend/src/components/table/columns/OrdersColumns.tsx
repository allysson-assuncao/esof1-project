'use client'

import { ColumnDef } from '@tanstack/react-table'
import {Order, OrderStatus} from "@/model/Interfaces";
import {TableHeader} from "@/components/ui/table";
import {Checkbox} from "@/components/ui/checkbox";

export const ordersColumns: ColumnDef<Order>[] = [
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
    /*{
        accessorKey: "unitPrice",
        header: () => <TableHeader title="Preço Unit." />,
        cell: ({ row }) => <div>R$ {row.original.unitPrice.toFixed(2)}</div>,
    },
    {
        accessorKey: "totalItemPrice",
        header: () => <TableHeader title="Total Item" />,
        cell: ({ row }) => <div>R$ {row.original.totalItemPrice.toFixed(2)}</div>,
    },*/
    {
        accessorKey: "orderStatus",
        header: () => <TableHeader title="Status" />,
        cell: ({ row }) => {
            const statusLabel = OrderStatus[row.original.orderStatus as keyof typeof OrderStatus]?.label || row.original.orderStatus;
            // Maybe add some color
            return <div>{statusLabel}</div>;
        },
        /*filterFn: (row, id, value) => {
          return value.includes(row.getValue(id));
        },*/
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
    /*{
      accessorKey: "guestTabId",
      header: "ID Comanda",
    },*/
];