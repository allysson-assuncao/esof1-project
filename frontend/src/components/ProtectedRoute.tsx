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
            toast.error("Não autenticado!", {
                description: 'Faça login para ter acesso a essa página',
            })
            router.push('/login');
        } else if (roles && !roles.includes(userRole as UserRole)) {
            setIsLoading(true);
            const requiredRoles = roles.map(role => UserRoles[role].label).join(', ')
            toast.error("Não autorizado!", {
                description: `Faça login com uma das seguintes permissões: ${requiredRoles.toLowerCase()} para acessar essa página`,
            })
            console.log("Cargo do usuário atual: " + userRole + " =! " + roles)
            // Loading component maybe?
            router.push('/login');
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

