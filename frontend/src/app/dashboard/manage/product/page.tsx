"use client";


import ProtectedRoute from "@/components/ProtectedRoute";
import {UserRoles} from "@/model/Interfaces";
import {RegisterProduct} from "@/components/product/RegisterProduct";

const RegisterProductPage = () => {
    return (
        <ProtectedRoute roles={[UserRoles.ADMIN.value]}>
            <div className="h-full">
                <RegisterProduct/>
            </div>
        </ProtectedRoute>
    );
};

export default RegisterProductPage;
