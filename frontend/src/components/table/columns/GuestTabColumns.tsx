'use client'

import {ColumnDef} from '@tanstack/react-table'
import {DisplayGuestTabItem, GuestTabStatus} from "@/model/Interfaces";
import {ChevronDown, ChevronRight} from "lucide-react";
import {DataTableColumnHeader} from "@/components/ui/data-table";

export const guestTabColumns: ColumnDef<DisplayGuestTabItem>[] = [
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
        accessorKey: "id",
        header: ({column}) => <DataTableColumnHeader column={column} title="ID Comanda"/>,
    },
    {
        accessorKey: "status",
        header: ({column}) => <DataTableColumnHeader column={column} title="Status"/>,
        cell: ({row}) => {
            return GuestTabStatus[row.original.status as keyof typeof GuestTabStatus]?.label || row.original.status || "-";
        },
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
