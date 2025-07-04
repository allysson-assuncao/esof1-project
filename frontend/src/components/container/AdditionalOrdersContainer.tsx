import {AdditionalOrdersContainerProps} from "@/model/Props";
import {OrdersSubTable} from "@/components/table/sub-table/OrdersSubTable";
import {AddOrderDialog} from "@/components/dialog/AddOrderDialog";

export const AdditionalOrdersContainer = ({guestTabId, parentOrder}: AdditionalOrdersContainerProps) => {
    const {id: parentOrderId, additionalOrders} = parentOrder;

    return (
        <div className="p-4 bg-background pl-16">
            <h4 className="text-sm font-semibold mb-2 text-muted-foreground">Itens Adicionais</h4>

            {additionalOrders && additionalOrders.length > 0 ? (
                <OrdersSubTable
                    orders={additionalOrders}
                    guestTabId={guestTabId}
                    parentOrderId={parentOrderId}
                />
            ) : (
                <p className="text-sm text-muted-foreground py-4 text-center">
                    Nenhum item adicional.
                </p>
            )}

            <div className="text-center mt-4">
                <AddOrderDialog
                    guestTabId={guestTabId}
                    parentOrderId={parentOrderId}
                    buttonText="Adicionar Item Adicional"
                />
            </div>
        </div>
    );
};
