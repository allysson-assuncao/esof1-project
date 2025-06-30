"use client";


import ProtectedRoute from "@/components/ProtectedRoute";
import {UserRoles} from "@/model/Interfaces";

const RegisterProductPage = () => {
    return (
        <ProtectedRoute roles={[UserRoles.ADMIN.value]}>
            <div className="h-full">
                Pagina de cadastro de produto
            </div>
        </ProtectedRoute>
    );
};

export default RegisterProductPage;
