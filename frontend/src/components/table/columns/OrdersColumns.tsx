'use client'

import { ColumnDef } from '@tanstack/react-table'
import {Order, OrderStatus} from "@/model/Interfaces";

export const ordersColumns: ColumnDef<Order>[] = [
    {
        accessorKey: 'id',
        header: 'ID',
        cell: ({ row }) => <div>{row.original.id}</div>,
    },
    {
        accessorKey: 'orderStatus',
        header: 'Status',
        cell: ({ row }) => <div>{OrderStatus[row.original.orderStatus]?.label || row.original.orderStatus}</div>,
    },
]