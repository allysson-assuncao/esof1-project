"use client"

import {ChevronRight, type LucideIcon} from "lucide-react"

import {
    Collapsible,
    CollapsibleContent,
    CollapsibleTrigger,
} from "@/components/ui/collapsible"
import {
    SidebarGroup,
    SidebarGroupLabel,
    SidebarMenu,
    SidebarMenuAction,
    SidebarMenuButton,
    SidebarMenuItem,
    SidebarMenuSub,
    SidebarMenuSubButton,
    SidebarMenuSubItem,
} from "@/components/ui/sidebar"
import {useSelector} from "react-redux";
import {RootState} from "@/store";
import {UserRole, UserRoles} from "@/model/Interfaces";
import {getInheritedRoles} from "@/components/ProtectedRoute";

export function NavMain({
                            items,
                        }: {
    items: {
        title: string
        url?: string
        icon: LucideIcon
        isActive?: boolean
        items?: {
            title: string
            url: string
        }[]
    }[]
}) {
    const role = useSelector((state: RootState) => state.auth.role)
    const allowedManageRoles = [UserRoles.ADMIN.value, UserRoles.CASHIER.value];

    const hasManagePermission = () => {
        if (!role) return false;
        const inheritedRoles = getInheritedRoles(role as UserRole);
        return allowedManageRoles.some(r => inheritedRoles.includes(r));
    };
    return (
        <SidebarGroup>
            <SidebarGroupLabel>Platform</SidebarGroupLabel>
            <SidebarMenu>
                {items.map((item) => (
                    (item.title !== 'Gestão' || hasManagePermission()) && (
                        (item.title !== 'Administrador' || role === UserRoles.ADMIN.value) && (
                            <Collapsible key={item.title} asChild defaultOpen={item.isActive}>
                                <SidebarMenuItem>
                                    <SidebarMenuButton asChild tooltip={item.title}>
                                        <a href={item.url}>
                                            <item.icon/>
                                            <span>{item.title}</span>
                                        </a>
                                    </SidebarMenuButton>
                                    {item.items?.length ? (
                                        <>
                                            <CollapsibleTrigger asChild>
                                                <SidebarMenuAction className="data-[state=open]:rotate-90">
                                                    <ChevronRight/>
                                                    <span className="sr-only">Toggle</span>
                                                </SidebarMenuAction>
                                            </CollapsibleTrigger>
                                            <CollapsibleContent>
                                                <SidebarMenuSub>
                                                    {item.items?.map((subItem) => (
                                                        /*(subItem.title !== 'Administrador' || role === UserRoles.ADMIN.value) && (*/
                                                        <SidebarMenuSubItem key={subItem.title}>
                                                            <SidebarMenuSubButton asChild>
                                                                <a href={subItem.url}>
                                                                    <span>{subItem.title}</span>
                                                                </a>
                                                            </SidebarMenuSubButton>
                                                        </SidebarMenuSubItem>
                                                        /*)*/
                                                    ))}
                                                </SidebarMenuSub>
                                            </CollapsibleContent>
                                        </>
                                    ) : null}
                                </SidebarMenuItem>
                            </Collapsible>
                        )
                    )
                ))}
            </SidebarMenu>
        </SidebarGroup>
    )
}
