'use client'

import {ColumnDef} from '@tanstack/react-table'
import {PaymentItem} from "@/model/Interfaces";

export const performanceReportColumns: ColumnDef<PaymentItem>[] = [
    {
        accessorKey: "id",
        header: "ID",
        cell: info => info.getValue(),
    },
    {
        accessorKey: "updatedAt",
        header: "Data/Hora",
        cell: info => {
            const value = info.getValue();
            if (!value) return "-";
            const date = new Date(value as string);
            return date.toLocaleString("pt-BR");
        },
    },
    {
        accessorKey: "totalAmount",
        header: "Valor Total",
        cell: info => `R$ ${Number(info.getValue()).toFixed(2)}`,
    },
];
