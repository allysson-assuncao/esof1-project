import {useForm} from 'react-hook-form'
import {zodResolver} from '@hookform/resolvers/zod'
import {registerSchema} from '@/utils/authValidation'
import {useMutation} from 'react-query'
import {useDispatch} from 'react-redux'
import {logout, signup} from '@/store/slices/authSlice'
import {Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle} from '@/components/ui/card'
import {Button} from '@/components/ui/button'
import {Icons} from '@/public/icons'
import {Input} from '@/components/ui/input'
import Link from 'next/link'
import {AxiosError} from 'axios'
import {useRouter} from 'next/navigation'
import {RegisterFormData} from '@/model/FormData'
import {Form, FormControl, FormField, FormItem, FormLabel, FormMessage} from '@/components/ui/form'
import * as React from 'react'
import {toast} from "sonner"
import {registerRequest} from "@/services/authService";
import {UserRoles} from "@/model/Interfaces";
import {useEffect, useState} from "react";
import {
    Select,
    SelectContent,
    SelectGroup,
    SelectItem,
    SelectLabel,
    SelectTrigger,
    SelectValue
} from "@/components/ui/select";

const RegisterForm = () => {
    const router = useRouter();
    const dispatch = useDispatch();
    const [selectedRole, setSelectedRole] = useState<string>('');

    const form = useForm<RegisterFormData>({
        resolver: zodResolver(registerSchema),
        defaultValues: {
            username: '',
            email: '',
            password: 'Senha123#',
            name: '',
            role: '',
        },
        mode: 'onBlur',
    })

    const handleRoleChange = (value: string) => {
        form.setValue('role', value);
        setSelectedRole(value);
    }

    const watchedRole = form.watch('role');

    useEffect(() => {
        setSelectedRole(watchedRole);
    }, [watchedRole]);

    const mutation = useMutation(registerRequest, {
        onSuccess: (data) => {
            dispatch(logout())
            dispatch(signup({username: data.username, email: data.email, role: data.role, token: data.token}))
            router.push('dashboard/home')

            toast.success("Usuário cadastrado com sucesso!", {
                description: `Bem-vindo, ${data.username}!`,
            })
        },
        onError: (error: unknown) => {
            if (error instanceof AxiosError) {
                toast.error("Erro ao fazer o cadastro", {
                    description: error.response?.data?.message || 'Ocorreu um erro inesperado.',
                })
            } else {
                toast.error("Erro ao fazer o cadastro", {
                    description: 'Ocorreu um erro inesperado.',
                })
            }
        },
    })

    const onSubmit = (data: RegisterFormData) => {
        mutation.mutate(data);
    }

    return (
        <div className="flex flex-col items-center mt-10 space-y-8 md:space-y-6">
            <Card className="w-full md:max-w-[700px] lg:max-w-[900px] mx-auto">
                <CardHeader className="space-y-1">
                    <CardTitle className="text-2xl">Login</CardTitle>
                    <CardDescription>
                        Informe o email e a senha
                    </CardDescription>
                </CardHeader>
                <CardContent className="grid gap-4">
                    <Form {...form}>
                        <form onSubmit={form.handleSubmit(onSubmit)}>
                            <div className="grid gap-4 sm:gap-6">
                                <FormField
                                    control={form.control}
                                    name="name"
                                    render={({field}) => (
                                        <FormItem>
                                            <FormLabel>Nome Completo</FormLabel>
                                            <FormControl>
                                                <Input placeholder="Fulano da Silva..." {...field} />
                                            </FormControl>
                                            <FormMessage/>
                                        </FormItem>
                                    )}
                                />
                                <FormField
                                    control={form.control}
                                    name="username"
                                    render={({field}) => (
                                        <FormItem>
                                            <FormLabel>Nome de usuário</FormLabel>
                                            <FormControl>
                                                <Input placeholder="fulano_silva123" {...field} />
                                            </FormControl>
                                            <FormMessage/>
                                        </FormItem>
                                    )}
                                />
                                <FormField
                                    control={form.control}
                                    name="email"
                                    render={({field}) => (
                                        <FormItem>
                                            <FormLabel>Email</FormLabel>
                                            <FormControl>
                                                <Input placeholder="seu.nome@gmail.com" {...field} />
                                            </FormControl>
                                            <FormMessage/>
                                        </FormItem>
                                    )}
                                />
                                <FormField
                                    control={form.control}
                                    name="password"
                                    render={({field}) => (
                                        <FormItem>
                                            <FormLabel>Senha</FormLabel>
                                            <FormControl>
                                                <Input type={'password'} {...field} />
                                            </FormControl>
                                            <FormMessage/>
                                        </FormItem>
                                    )}
                                />
                                <FormField
                                    control={form.control}
                                    name="role"
                                    render={({field}) => (
                                        <FormItem>
                                            <FormLabel>Cargo</FormLabel>
                                            <FormControl>
                                                <Select onValueChange={handleRoleChange}>
                                                    <SelectTrigger {...field}>
                                                        <SelectValue placeholder="Você é: "/>
                                                    </SelectTrigger>
                                                    <SelectContent>
                                                        <SelectGroup>
                                                            <SelectLabel>Você é: </SelectLabel>
                                                            {Object.values(UserRoles).map((role) => (
                                                                <SelectItem
                                                                    key={role.value}
                                                                    value={role.value}
                                                                >
                                                                    {role.label}
                                                                </SelectItem>
                                                            ))}
                                                        </SelectGroup>
                                                    </SelectContent>
                                                </Select>
                                            </FormControl>
                                            <FormMessage/>
                                        </FormItem>
                                    )}
                                />
                                <div className="flex justify-center">
                                    <Button className="w-full justify-center touch-manipulation"
                                            type="submit" disabled={mutation.isLoading}>
                                        {mutation.isLoading ? <Icons.spinner className="animate-spin"/> : 'Cadastro'}
                                    </Button>
                                </div>
                            </div>
                        </form>
                    </Form>
                    <div className="relative">
                        <div className="absolute inset-0 flex items-center">
                            <span className="w-full border-t"/>
                        </div>
                        <div className="relative flex justify-center text-xs uppercase">
                        <span className="bg-background px-2 text-muted-foreground">
                          Ou faça login com
                        </span>
                        </div>
                    </div>
                    <div className="grid grid-cols-2 gap-6">
                        <Button variant="outline" className="touch-manipulation">
                            <Icons.gitHub className="mr-2 h-4 w-4"/>
                            Github
                        </Button>
                        <Button variant="outline" className="touch-manipulation">
                            <Icons.google className="mr-2 h-4 w-4"/>
                            Google
                        </Button>
                    </div>
                </CardContent>
                <div className="relative">
                    <div className="absolute inset-0 flex items-center">
                        <span className="w-full border-t"/>
                    </div>
                    <div className="relative flex justify-center text-xs uppercase">
                        <span className="bg-background px-2 text-muted-foreground">
                          Ou
                        </span>
                    </div>
                </div>
                <CardFooter className="justify-center mt-5">
                    <Link
                        href="/login"
                        className="text-sm font-medium transition-colors hover:text-primary"
                    >
                        Faça login
                    </Link>
                </CardFooter>
            </Card>
        </div>
    )
}

export default RegisterForm
