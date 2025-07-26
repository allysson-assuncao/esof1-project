import {ChevronDown, ChevronRight} from "lucide-react";
import {PaymentGroup} from "@/model/Interfaces";
import {ColumnDef} from "@tanstack/react-table";

export const paymentGroupColumns = (): ColumnDef<PaymentGroup>[] => [
    {
        id: "expander",
        header: ({table}) => (
            <button
                onClick={() => table.toggleAllRowsExpanded(!table.getIsAllRowsExpanded())}
                aria-label={table.getIsAllRowsExpanded() ? "Recolher todos" : "Expandir todos"}
            >
                {table.getIsAllRowsExpanded() ? <ChevronDown/> : <ChevronRight/>}
            </button>
        ),
        cell: ({row}) => (
            <button onClick={() => row.toggleExpanded(!row.getIsExpanded())}>
                {row.getIsExpanded() ? <ChevronDown/> : <ChevronRight/>}
            </button>
        ),
    },
    {
        accessorKey: "date",
        header: "Data",
        cell: ({row}) => new Date(row.original.date + 'T00:00:00').toLocaleDateString('pt-BR'),
    },
    {
        accessorKey: "paymentCount",
        header: "Qtd. Pagamentos",
    },
    {
        accessorKey: "totalAmount",
        header: "Valor Total",
        cell: ({row}) => new Intl.NumberFormat("pt-BR", {
            style: "currency",
            currency: "BRL"
        }).format(row.original.totalAmount),
    },
];
