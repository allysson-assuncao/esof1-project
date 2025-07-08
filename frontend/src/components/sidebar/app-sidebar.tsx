"use client"

import * as React from "react"
import {
    Command, FingerprintIcon, FolderKanbanIcon,
    GlobeIcon,
    LifeBuoy,
    Send, Settings2Icon, ShieldPlusIcon,
    SquareTerminal,
} from "lucide-react"

import {
    Sidebar,
    SidebarContent,
    SidebarFooter,
    SidebarHeader,
    SidebarMenu,
    SidebarMenuButton,
    SidebarMenuItem,
} from "@/components/ui/sidebar"
import {NavMain} from "@/components/sidebar/nav/nav-main";
import {NavAbout} from "@/components/sidebar/nav/nav-about";
import {NavSecondary} from "@/components/sidebar/nav/nav-secondary";
import {NavUser} from "@/components/sidebar/nav/nav-user";

const data = {
    user: {
        name: "Hub Restaurantes",
        email: "hub.restaurantes@gmail.com",
        avatar: "/avatars/rest_hub.jpg",
    },
    navMain: [
        {
            title: "Gestão",
            /*url: "#",*/
            icon: SquareTerminal,
            isActive: true,
            items: [
                {
                    title: "Produtos",
                    url: '/dashboard/manage/product',
                },
                {
                    title: "Categorias",
                    url: '/dashboard/manage/category',
                },
                {
                    title: "Destinos",
                    url: '/dashboard/manage/workstation',
                },
                {
                    title: "Mesas",
                    url: '/dashboard/manage/table',
                },
            ],
        },
        {
            title: "Operação",
            /*url: "#",*/
            icon: FolderKanbanIcon,
            isActive: true,
            items: [
                {
                    title: "Seleção de Mesas",
                    url: '/dashboard/table/grid',
                },
                {
                    title: "Cardápio",
                    url: '/dashboard/menu',
                },
                {
                    title: "Pedidos por Área",
                    url: '/dashboard/table/orders-by-workstation',
                },
            ],
        },
        {
            title: 'Autenticação',
            /*url: "#",*/
            icon: FingerprintIcon,
            items: [
                {
                    title: 'Login',
                    url: '/login',
                },
                {
                    title: 'Cadastro',
                    url: '/register',
                },
                {
                    title: 'Esqueceu a senha',
                    url: '/forgot-password',
                },
            ],
        },
        {
            title: 'Configurações',
            /*url: "#",*/
            icon: Settings2Icon,
            isActive: true,
            items: [
                {
                    title: 'Perfil',
                    url: '/dashboard/settings/profile',
                },
                {
                    title: 'Conta',
                    url: '/dashboard/settings/account',
                },
                {
                    title: 'Aparência',
                    url: '/dashboard/settings/appearance',
                },
                {
                    title: 'Notificações',
                    url: '/dashboard/settings/notifications',
                },
                {
                    title: 'Display',
                    url: '/dashboard/settings/display',
                },
            ],
        },
        {
            title: 'Administrador',
            /*url: "#",*/
            icon: ShieldPlusIcon,
            isActive: false,
            items: [
                {
                    title: 'Relatório de Vendas',
                    url: '/dashboard/table/sales',
                },
                {
                    title: 'Relatório de Operação',
                    url: '/dashboard/table/operation',
                },
                {
                    title: 'Relatório de Funcionários',
                    url: '/dashboard/table/workers',
                },
            ],
        },
    ],
    projects: [
        {
            name: 'Sites',
            /*url: "#",*/
            icon: GlobeIcon,
            itemNames: ['GitHub'],
            itemUrls: ['https://github.com/allysson-assuncao/esof1-project'],
        },
        /*{
            name: "Sales & Marketing",
            url: "#",
            icon: PieChart,
        },
        {
            name: "Travel",
            url: "#",
            icon: Map,
        },*/
    ],
    navSecondary: [
        {
            title: "Suporte",
            url: "#",
            icon: LifeBuoy,
        },
        {
            title: "Feedback",
            url: "#",
            icon: Send,
        },
    ],
}

export function AppSidebar({...props}: React.ComponentProps<typeof Sidebar>) {
    return (
        <Sidebar variant="inset" {...props}>
            <SidebarHeader>
                <SidebarMenu>
                    <SidebarMenuItem>
                        <SidebarMenuButton size="lg" asChild>
                            <a href="#">
                                <div
                                    className="bg-sidebar-primary text-sidebar-primary-foreground flex aspect-square size-8 items-center justify-center rounded-lg">
                                    <Command className="size-4"/>
                                </div>
                                <div className="grid flex-1 text-left text-sm leading-tight">
                                    <span className="truncate font-medium">Hub Restaurantes</span>
                                    <span className="truncate text-xs">Empresa</span>
                                </div>
                            </a>
                        </SidebarMenuButton>
                    </SidebarMenuItem>
                </SidebarMenu>
            </SidebarHeader>
            <SidebarContent>
                <NavMain items={data.navMain}/>
                <NavAbout projects={data.projects}/>
                <NavSecondary items={data.navSecondary} className="mt-auto"/>
            </SidebarContent>
            <SidebarFooter>
                <NavUser user={data.user}/>
            </SidebarFooter>
        </Sidebar>
    )
}
