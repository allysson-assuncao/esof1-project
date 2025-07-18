import React from "react";
import ProtectedRoute from "@/components/ProtectedRoute";
import {UserRoles} from "@/model/Interfaces";

export default function ManageLayout({children,}: Readonly<{ children: React.ReactNode; }>) {

    return (
        <ProtectedRoute roles={[UserRoles.CASHIER.value]}>
            <div>
                <main>
                    {children}
                </main>
            </div>
        </ProtectedRoute>
    );
}