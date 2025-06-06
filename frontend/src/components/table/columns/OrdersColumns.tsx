'use client'

import {ColumnDef} from '@tanstack/react-table'
import {DisplayGuestTabItem} from "@/model/Interfaces";
import {ChevronDown, ChevronRight} from "lucide-react";
import {DataTableColumnHeader} from "@/components/ui/data-table";

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
