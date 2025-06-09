"use client";

import GuestTabTable from "@/components/table/GuestTabTable";

function OrdersTablePage({
                             params,
                         }: {
    params: { id: string };
}) {
    return (
        <div className="h-full">
            <GuestTabTable localTableId={params.id} />
        </div>
    );
};

export default OrdersTablePage;
