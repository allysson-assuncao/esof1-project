'use client'

import { useSelector } from 'react-redux'
import { useEffect, useState } from 'react'
import { RootState } from '@/store'
import { useRouter } from 'next/navigation'
import { Icons } from '@/public/icons'
import {toast} from "sonner"
import {UserRole, UserRoles} from "@/model/Interfaces";
import {ProtectedRouteProps} from "@/model/Props";

const ProtectedRoute = ({ children, roles }: ProtectedRouteProps) => {
    const { isAuthenticated, role: userRole } = useSelector((state: RootState) => state.auth)
    const router = useRouter();
    const [isLoading, setIsLoading] = useState(false);

    useEffect(() => {
        if (!isAuthenticated) {
            setIsLoading(true);
            router.push('/login');
            toast.error("Não autenticado!", {
                description: 'Faça login para ter acesso a essa página',
            })
        } else if (roles && !roles.includes(userRole as UserRole)) {
            setIsLoading(true);
            router.push('/login');
            const requiredRoles = roles.map(role => UserRoles[role].label).join(', ')
            toast.error("Não autorizado!", {
                description: `Faça login com uma das seguintes permissões: ${requiredRoles.toLowerCase()} para acessar essa página`,
            })
            // Send back to the last route and show a waring snackbar or smth
        }
    }, [isAuthenticated, roles, userRole, router]);

    if (isLoading || !isAuthenticated || (roles && !roles.includes(userRole as UserRole))) {
        return (
            <div className="flex justify-center items-center h-screen">
                <Icons.spinner className="animate-spin"/>
            </div>
        );
    }

    return <>{children}</>
}

export default ProtectedRoute

