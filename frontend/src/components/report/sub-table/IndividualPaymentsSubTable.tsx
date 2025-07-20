import React from "react";
import {IndividualPayment} from "@/model/Interfaces";
import {ColumnDef, getCoreRowModel, useReactTable} from "@tanstack/react-table";
import {Table, TableBody} from "@/components/ui/table";

const getIndividualPaymentColumns = (): ColumnDef<IndividualPayment>[] => [
    { accessorKey: "id", header: "ID" },
    { accessorKey: "paymentMethodName", header: "Método" },
    { accessorKey: "amount", header: "Valor", cell: ({ row }) => `R$ ${row.original.amount.toFixed(2)}` },
];

export const IndividualPaymentsSubTable = ({ individualPayments }: { individualPayments: IndividualPayment[] }) => {
    const columns = React.useMemo(() => getIndividualPaymentColumns(), []);
    const table = useReactTable({ data: individualPayments, columns, getCoreRowModel: getCoreRowModel() });

    return (
         <div className="p-4 bg-muted/40 pl-20">
            <Table>
                {/* ... Thead ... */}
                <TableBody>
                   {/* ... TRows and TCells ... */}
                </TableBody>
            </Table>
        </div>
    );
};
