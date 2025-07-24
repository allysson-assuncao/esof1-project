'use client'

import {ColumnDef} from '@tanstack/react-table'
import {CategorySales, ProductSales} from "@/model/Interfaces";
import {CheckCircle2, ChevronDown, ChevronRight, Dot, XCircle} from "lucide-react";

type MenuRowData = CategorySales | ProductSales;

const formatCurrency = (value?: number) => {
    if (value === undefined || value === null) return '-';
    return new Intl.NumberFormat("pt-BR", { style: "currency", currency: "BRL" }).format(value);
};

export const menuPerformanceReportColumns: ColumnDef<MenuRowData>[] = [
    {
        accessorKey: 'name',
        header: 'Item',
        cell: ({ row, getValue }) => {
            const isCategory = 'categoryId' in row.original;
            return (
                <div
                    style={{ paddingLeft: `${row.depth * 1.5}rem` }}
                    className="flex items-center gap-2"
                >
                    {row.getCanExpand() ? (
                        <button onClick={row.getToggleExpandedHandler()} className="cursor-pointer">
                            {row.getIsExpanded() ? <ChevronDown size={16} /> : <ChevronRight size={16} />}
                        </button>
                    ) : (
                        <Dot size={16} className="invisible" />
                    )}
                    <span className={isCategory ? 'font-semibold' : ''}>{getValue<string>()}</span>
                </div>
            );
        },
    },
    {
        accessorKey: 'quantitySold',
        header: () => <div className="text-right">Qtd. Vendida</div>,
        cell: ({ getValue }) => <div className="text-right">{getValue<number>()}</div>,
    },
    {
        id: 'active',
        header: () => <div className="text-center">Ativo</div>,
        cell: ({ row }) => {
            const isProduct = 'productId' in row.original;
            if (!isProduct) return <div className="text-center">-</div>;
            const active = (row.original as ProductSales).active;
            return (
                <div className="flex justify-center items-center">
                    {active ? (
                        <CheckCircle2 size={20} className="text-green-600" />
                    ) : (
                        <XCircle size={20} className="text-red-600" />
                    )}
                </div>
            );
        },
        enableSorting: false,
        enableColumnFilter: false,
    },
    {
        accessorKey: 'unitPrice',
        header: () => <div className="text-right">Preço Unitário</div>,
        cell: ({ row }) => {
            const isProduct = 'productId' in row.original;
            return (
                <div className="text-right">
                    {isProduct ? formatCurrency("unitPrice" in row.original ? row.original.unitPrice : 0) : '-'}
                </div>
            );
        },
    },
    {
        accessorKey: 'totalValue',
        header: () => <div className="text-right">Valor Total</div>,
        cell: ({ getValue }) => (
            <div className="text-right font-medium">
                {formatCurrency(getValue<number>())}
            </div>
        ),
    },
];
