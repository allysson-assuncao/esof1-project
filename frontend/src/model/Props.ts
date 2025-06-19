import {ReactNode} from "react";
import {UserRole} from "@/model/Interfaces";

export interface ProtectedRouteProps {
    children: ReactNode;
    roles?: UserRole[];
}