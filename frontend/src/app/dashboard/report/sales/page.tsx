"use client";


import SalesReportTable from "@/components/report/SalesReportTable";

function OrdersTablePage({
                             params,
                         }: {
    params: { id: string };
}) {
    return (
        <div className="h-full">
            <SalesReportTable/>
        </div>
    );
};

export default OrdersTablePage;
