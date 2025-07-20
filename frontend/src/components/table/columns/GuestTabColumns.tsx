'use client'

import {ColumnDef} from '@tanstack/react-table'
import {DisplayGuestTabItem, GuestTabStatus} from "@/model/Interfaces";
import {ChevronDown, ChevronRight} from "lucide-react";
import {DataTableColumnHeader} from "@/components/ui/data-table";
import {formatDateDisplay} from "@/utils/operations/date-convertion";
import {GuestTabActions} from "@/components/actions/GuestTabActions";

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
        accessorKey: "guestName",
        header: ({column}) => <DataTableColumnHeader column={column} title="Nome do Cliente"/>,
    },
    {
        accessorKey: 'timeOpened',
        header: 'Hora',
        cell: ({row}) => formatDateDisplay(row.original.timeOpened)
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
        cell: ({row}) => row.original.orderGroups.length,
    },
    {
        id: 'actions',
        header: () => <div className="text-center">Ações</div>,
        cell: ({row}) => {
            const guestTab = row.original;
            return (
                <div className="flex justify-center">
                    <GuestTabActions guestTab={guestTab}/>
                </div>
            )
        },
    },
]
