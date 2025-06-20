import React from "react";
import ProtectedRoute from "@/components/ProtectedRoute";
import {SidebarInset, SidebarProvider, SidebarTrigger} from "@/components/ui/sidebar";
import {AppSidebar} from "@/components/sidebar/app-sidebar";
import {Separator} from "@/components/ui/separator";
import {
    Breadcrumb,
    BreadcrumbItem,
    BreadcrumbLink,
    BreadcrumbList, BreadcrumbPage,
    BreadcrumbSeparator
} from "@/components/ui/breadcrumb";

export default function DashboardLayout({children,}: Readonly<{ children: React.ReactNode; }>) {

    return (
        <ProtectedRoute>
            <div>
                <SidebarProvider>
                    <AppSidebar/>
                    <main className="flex-grow h-full w-full max-w-[1920px] px-4 md:px-8 5xl:mx-auto 5xl:px-32">
                        {/*<SidebarInset>
                            <header className="flex h-16 shrink-0 items-center gap-2">
                                <div className="flex items-center gap-2 px-4">
                                    <SidebarTrigger className="-ml-1"/>
                                    <Separator
                                        orientation="vertical"
                                        className="mr-2 data-[orientation=vertical]:h-4"
                                    />
                                    <Breadcrumb>
                                        <BreadcrumbList>
                                            <BreadcrumbItem className="hidden md:block">
                                                <BreadcrumbLink href="#">
                                                    Gerencie seu Estabelecimento
                                                </BreadcrumbLink>
                                            </BreadcrumbItem>
                                            <BreadcrumbSeparator className="hidden md:block"/>
                                            <BreadcrumbItem>
                                                <BreadcrumbPage>Relatório de Vendas</BreadcrumbPage>
                                            </BreadcrumbItem>
                                        </BreadcrumbList>
                                    </Breadcrumb>
                                </div>
                            </header>
                        </SidebarInset>*/}
                        {children}
                    </main>
                </SidebarProvider>
            </div>
        </ProtectedRoute>
    );
}
