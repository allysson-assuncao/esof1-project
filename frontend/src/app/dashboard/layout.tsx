import React from "react";
import ProtectedRoute from "@/components/ProtectedRoute";

export default function DashboardLayout({children,}: Readonly<{ children: React.ReactNode; }>) {

    return (
        <ProtectedRoute>
            <div>
                <main className="flex-grow h-full w-full max-w-[1920px] px-4 md:px-8 5xl:mx-auto 5xl:px-32">
                    {children}
                </main>
            </div>
        </ProtectedRoute>
    );
}
