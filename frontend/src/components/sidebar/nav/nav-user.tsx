"use client"

import {
    BadgeCheck,
    Bell, ChevronDownIcon, ChevronRightIcon,
    ChevronsUpDown,
    LogOut, MoonIcon, PaletteIcon, SunIcon,
} from "lucide-react"

import {
    Avatar,
    AvatarFallback,
    AvatarImage,
} from "@/components/ui/avatar"
import {
    DropdownMenu,
    DropdownMenuContent,
    DropdownMenuGroup,
    DropdownMenuItem,
    DropdownMenuLabel,
    DropdownMenuSeparator, DropdownMenuSub,
    DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu"
import {
    SidebarMenu,
    SidebarMenuButton,
    SidebarMenuItem,
    useSidebar,
} from "@/components/ui/sidebar"
import {useRouter} from "next/navigation";
import {useDispatch} from "react-redux";
import {logout} from "@/store/slices/authSlice";
import {useEffect, useState} from "react";
import {useTheme} from "next-themes";

export function NavUser({
                            user,
                        }: {
    user: {
        name: string
        email: string
        avatar: string
    }
}) {
    const {isMobile} = useSidebar();
    const router = useRouter();
    const dispatch = useDispatch();

    const {setTheme, themes} = useTheme();
    const [showColors, setShowColors] = useState(false);

    const [, setIsClient] = useState(false)

    useEffect(() => {
        setIsClient(true)
    }, [])

    const colors = [
        {name: 'green', hex: '#22c55e', label: 'verde'},
        {name: 'blue', hex: '#3b82f6', label: 'azul'},
        {name: 'purple', hex: '#6d28d9', label: 'roxo'},
        {name: 'red', hex: '#dc2626', label: 'vermelho'},
        {name: 'gray', hex: '#4b5563', label: 'cinza'},
        {name: 'orange', hex: '#ea580c', label: 'laranja'},
        {name: 'yellow', hex: '#facc15', label: 'amarelo'},
    ]

    const [mode, setMode] = useState<string>('light')
    const [color, setColor] = useState<string>('green')
    const [isMounted, setIsMounted] = useState<boolean>(false)

    useEffect(() => {
        setIsMounted(true)

        const savedMode = typeof window !== 'undefined' ? localStorage.getItem('mode') : null
        const savedColor = typeof window !== 'undefined' ? localStorage.getItem('color') : null

        if (savedMode) {
            setMode(savedMode)
        }
        if (savedColor) {
            setColor(savedColor)
        }
    }, [])

    useEffect(() => {
        if (isMounted) {
            setTheme(`${mode}-${color}`)
            localStorage.setItem('mode', mode)
            localStorage.setItem('color', color)
        }
    }, [mode, color, setTheme, isMounted, themes])

    const handleLogout = () => {
        router.push('/login');
        dispatch(logout());
    };

    /*if (/!*isClient && *!/!profile) {
        return <div>Carregando...</div>;
    }*/

    return (
        <SidebarMenu>
            <SidebarMenuItem>
                <DropdownMenu>
                    <DropdownMenuTrigger asChild>
                        <SidebarMenuButton
                            size="lg"
                            className="data-[state=open]:bg-sidebar-accent data-[state=open]:text-sidebar-accent-foreground"
                        >
                            <Avatar className="h-8 w-8 rounded-lg">
                                <AvatarImage src={user.avatar} alt={user.name}/>
                                <AvatarFallback className="rounded-lg">CN</AvatarFallback>
                            </Avatar>
                            <div className="grid flex-1 text-left text-sm leading-tight">
                                <span className="truncate font-medium">{user.name}</span>
                                <span className="truncate text-xs">{user.email}</span>
                            </div>
                            <ChevronsUpDown className="ml-auto size-4"/>
                        </SidebarMenuButton>
                    </DropdownMenuTrigger>
                    <DropdownMenuContent
                        className="w-(--radix-dropdown-menu-trigger-width) min-w-56 rounded-lg"
                        side={isMobile ? "bottom" : "right"}
                        align="end"
                        sideOffset={4}
                    >
                        <DropdownMenuLabel className="p-0 font-normal">
                            <div className="flex items-center gap-2 px-1 py-1.5 text-left text-sm">
                                <Avatar className="h-8 w-8 rounded-lg">
                                    <AvatarImage src={user.avatar} alt={user.name}/>
                                    <AvatarFallback className="rounded-lg">CN</AvatarFallback>
                                </Avatar>
                                <div className="grid flex-1 text-left text-sm leading-tight">
                                    <span className="truncate font-medium">{user.name}</span>
                                    <span className="truncate text-xs">{user.email}</span>
                                </div>
                            </div>
                        </DropdownMenuLabel>
                        <DropdownMenuSeparator/>
                        <DropdownMenuGroup>
                            <DropdownMenuItem onClick={(event) => {
                                event.preventDefault();
                                setMode(mode === 'light' ? 'dark' : 'light');
                            }}>
                                {mode === 'light' ? (
                                    <SunIcon className={"h-[1.2rem] w-[1.2rem]"}/>
                                ) : (
                                    <MoonIcon className={"h-[1.2rem] w-[1.2rem]"}/>
                                )}
                                Alternar tema
                            </DropdownMenuItem>
                            <DropdownMenuItem onClick={(event) => {
                                event.preventDefault();
                                setShowColors(!showColors);
                            }}>
                                <PaletteIcon/>
                                {showColors ? "Ocultar Cores" : "Escolher cor"}
                                {showColors ? <ChevronRightIcon/> : <ChevronDownIcon/>}
                            </DropdownMenuItem>
                            {showColors && (
                                <DropdownMenuSub>
                                    {colors.map(c => (
                                        <DropdownMenuItem key={c.name} onClick={() => setColor(c.name)}>
                                            <span
                                                className={"block h-4 w-4 rounded-full mr-2"}
                                                style={{backgroundColor: c.hex}}
                                            />
                                            {c.label.charAt(0).toUpperCase() + c.label.slice(1)}
                                        </DropdownMenuItem>
                                    ))}
                                </DropdownMenuSub>
                            )}
                        </DropdownMenuGroup>
                        <DropdownMenuSeparator/>
                        <DropdownMenuGroup>
                            <DropdownMenuItem onClick={() => router.push(`/dashboard/profile/`)}>
                                <BadgeCheck/>
                                Perfil
                            </DropdownMenuItem>
                            <DropdownMenuItem>
                                <Bell/>
                                Notificações
                            </DropdownMenuItem>
                        </DropdownMenuGroup>
                        <DropdownMenuSeparator/>
                        <DropdownMenuItem onSelect={handleLogout}>
                            <LogOut/>
                            Sair
                        </DropdownMenuItem>
                    </DropdownMenuContent>
                </DropdownMenu>
            </SidebarMenuItem>
        </SidebarMenu>
    )
}
