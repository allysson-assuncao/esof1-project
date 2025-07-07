import {Button} from "@/components/ui/button";
import {MakeOrderColumnsProps} from "@/model/Props";
import {ColumnDef} from "@tanstack/react-table";
import {OrderKanban, OrderStatus} from "@/model/Interfaces";
import {ArrowLeft, ArrowRight, ChevronDown, ChevronRight} from "lucide-react";
import {Badge} from "@/components/ui/badge";

export const makeOrderKanbanColumns = ({
                                           onAdvanceStatus,
                                           onRevertStatus,
                                       }: MakeOrderColumnsProps): ColumnDef<OrderKanban>[] => [
    {
        id: 'expander',
        header: () => null,
        cell: ({row}) => {
            return row.getCanExpand() ? (
                <Button
                    variant="ghost"
                    size="icon"
                    onClick={row.getToggleExpandedHandler()}
                    className="h-8 w-8 p-0"
                >
                    {row.getIsExpanded() ? <ChevronDown className="h-4 w-4"/> : <ChevronRight className="h-4 w-4"/>}
                </Button>
            ) : null;
        },
    },
    {
        accessorKey: 'productName',
        header: 'Produto',
        cell: ({row}) => <div className="font-medium">{row.original.productName}</div>,
    },
    {
        accessorKey: 'amount',
        header: 'Qtd',
        cell: ({row}) => <div>{row.original.amount}</div>,
    },
    {
        accessorKey: 'observation',
        header: 'Observação',
        cell: ({row}) => <div className="text-sm text-muted-foreground">{row.original.observation || '-'}</div>,
    },
    {
        accessorKey: 'workstationName',
        header: 'Estação',
        cell: ({row}) => <Badge variant="outline">{row.original.workstationName}</Badge>,
    },
    {
        accessorKey: 'orderedTime',
        header: 'Horário',
        cell: ({row}) => {
            const date = new Date(row.original.orderedTime);
            return <div>{date.toLocaleTimeString('pt-BR')}</div>;
        },
    },
    {
        id: 'actions',
        header: 'Ações',
        cell: ({row}) => {
            const order = row.original;
            return (
                <div className="flex gap-2">
                    {/* Precious status */}
                    {order.status === OrderStatus.IN_PREPARE.value && (
                        <Button
                            variant="outline"
                            size="icon"
                            onClick={() => onRevertStatus(order.id)}
                        >
                            <ArrowLeft className="h-4 w-4"/>
                        </Button>
                    )}
                    {order.status === OrderStatus.READY.value && (
                        <Button
                            variant="outline"
                            size="icon"
                            onClick={() => onRevertStatus(order.id)}
                        >
                            <ArrowLeft className="h-4 w-4"/>
                        </Button>
                    )}

                    {/* Next status */}
                    {order.status === OrderStatus.SENT.value && (
                        <Button
                            variant="outline"
                            size="icon"
                            onClick={() => onAdvanceStatus(order.id)}
                        >
                            <ArrowRight className="h-4 w-4"/>
                        </Button>
                    )}
                    {order.status === OrderStatus.IN_PREPARE.value && (
                        <Button
                            variant="outline"
                            size="icon"
                            onClick={() => onAdvanceStatus(order.id)}
                        >
                            <ArrowRight className="h-4 w-4"/>
                        </Button>
                    )}
                </div>
            );
        },
    },
];
