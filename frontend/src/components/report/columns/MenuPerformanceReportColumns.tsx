"use client";

import {ColumnDef} from "@tanstack/react-table";
import {ReportRow} from "@/model/Interfaces";
import {ChevronDown, ChevronRight, CheckCircle2, XCircle} from "lucide-react";
import {Badge} from "@/components/ui/badge";

const formatCurrency = (value?: number) => {
    if (value === undefined || value === null) return '-';
    return new Intl.NumberFormat("pt-BR", {style: "currency", currency: "BRL"}).format(value);
};

export const menuPerformanceReportColumns: ColumnDef<ReportRow>[] = [
    {
        accessorKey: "name",
        header: ({table}) => (
            <div className="flex flex-row">
                <button
                    onClick={() => table.toggleAllRowsExpanded(!table.getIsAllRowsExpanded())}
                    aria-label={table.getIsAllRowsExpanded() ? "Recolher todos" : "Expandir todos"}
                    className="p-1"
                >
                    {table.getIsAllRowsExpanded() ? <ChevronDown size={18}/> : <ChevronRight size={18}/>}
                </button>
                <div className="justify-center p-2">
                    Item
                </div>
            </div>
        ),
        cell: ({row}) => {
            const paddingLeft = `${row.depth * 1.5}rem`;

            return (
                <div style={{paddingLeft}} className="flex items-center gap-2">
                    {}
                    {row.getCanExpand() ? (
                        <button
                            onClick={row.getToggleExpandedHandler()}
                            style={{cursor: 'pointer'}}
                            className="p-0.5 rounded hover:bg-gray-200 dark:hover:bg-gray-700"
                        >
                            {row.getIsExpanded() ? <ChevronDown size={16}/> : <ChevronRight size={16}/>}
                        </button>
                    ) : (
                        <span className="inline-block w-5"></span>
                    )}
                    {}
                    <span
                        className={row.original.type === 'CATEGORY' ? 'font-semibold' : ''}>{row.getValue("name")}</span>
                    {row.original.type === 'CATEGORY' && <Badge variant="secondary">Categoria</Badge>}
                </div>
            );
        },
    },
    {
        accessorKey: 'quantitySold',
        header: () => <div className="text-center">Qtd. Vendida</div>,
        cell: ({getValue}) => <div className="text-center">{getValue<number>()}</div>,
    },
    {
        accessorKey: 'active',
        header: () => <div className="text-center">Ativo</div>,
        cell: ({row}) => {
            if (row.original.type !== 'PRODUCT') {
                return <div className="text-center">-</div>;
            }
            const active = row.original.active;
            return (
                <div className="flex justify-center">
                    {active ? <CheckCircle2 size={18} className="text-green-500"/> :
                        <XCircle size={18} className="text-red-500"/>}
                </div>
            );
        }
    },
    {
        accessorKey: 'unitPrice',
        header: () => <div className="text-right">Preço Unit.</div>,
        cell: ({row}) => {
            const price = row.original.unitPrice;
            return <div className="text-right">{formatCurrency(price)}</div>;
        },
    },
    {
        accessorKey: "totalValue",
        header: () => <div className="text-right">Valor Total</div>,
        cell: ({getValue}) => (
            <div className="text-right font-medium">
                {formatCurrency(getValue<number>())}
            </div>
        ),
    },
];