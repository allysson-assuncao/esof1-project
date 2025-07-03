import {ReactNode} from "react";
import {UserRole} from "@/model/Interfaces";

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
