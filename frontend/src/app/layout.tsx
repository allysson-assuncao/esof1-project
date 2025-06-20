"use client"

import "./globals.css";
import {QueryClient, QueryClientProvider} from "react-query";
import {Provider} from "react-redux";
import {ThemeProvider} from "next-themes";
import {Toaster} from "@/components/ui/sonner";
import store from "@/store";
import React from "react";

const queryClient = new QueryClient();

export default function RootLayout({children,}: Readonly<{ children: React.ReactNode; }>) {

    return (
        <html lang="pt-br">
        <body className="h-screen flex flex-col justify-center items-center">

        <Provider store={store}>
            <QueryClientProvider client={queryClient}>
                <ThemeProvider
                    attribute="class"
                    defaultTheme="system"
                    enableSystem
                    themes={['light-green', 'dark-green', 'light-blue', 'dark-blue', 'light-purple', 'dark-purple', 'light-red', 'dark-red', 'light-gray', 'dark-gray', 'light-orange', 'dark-orange', 'light-yellow', 'dark-yellow']}
                    disableTransitionOnChange
                >
                    <main className="flex-grow h-full w-full max-w-[1920px] px-4 md:px-8 5xl:mx-auto 5xl:px-32">
                        {children}
                        <Toaster/>
                    </main>
                </ThemeProvider>
            </QueryClientProvider>
        </Provider>
        </body>
        </html>
    );
}
