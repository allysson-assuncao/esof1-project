import {ReactNode} from "react";
import {DisplayOrderItem, OrderStatus, UserRole} from "@/model/Interfaces";

export interface ProtectedRouteProps {
    children: ReactNode;
    roles?: UserRole[];
}

export interface AddOrderFormProps {
  guestTabId: number;
  parentOrderId: number | null;
  onSuccess: () => void;
}

export interface AddOrderDialogProps {
    guestTabId: number;
    parentOrderId?: number | null;
    buttonText: string;
}

export interface AdditionalOrdersContainerProps {
  guestTabId: number;
  parentOrder: DisplayOrderItem;
}

export interface MakeOrderColumnsProps {
    onAdvanceStatus: (orderId: number, newStatus: OrderStatus) => void;
    onRevertStatus: (orderId: number, newStatus: OrderStatus) => void;
}
