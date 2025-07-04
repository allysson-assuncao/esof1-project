import {useForm} from 'react-hook-form'
import {zodResolver} from '@hookform/resolvers/zod'
import {loginSchema} from '@/utils/authValidation'
import {useMutation} from 'react-query'
import {useDispatch} from 'react-redux'
import {login, logout} from '@/store/slices/authSlice'
import {Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle} from '@/components/ui/card'
import {Button} from '@/components/ui/button'
import {Icons} from '@/public/icons'
import {Input} from '@/components/ui/input'
import Link from 'next/link'
import {AxiosError} from 'axios'
import {useRouter} from 'next/navigation'
import {LoginFormData} from '@/model/FormData'
import {Form, FormControl, FormField, FormItem, FormLabel, FormMessage} from '@/components/ui/form'
import * as React from 'react'
import {toast} from "sonner"
import {loginRequest} from "@/services/authService";

const LoginForm = () => {
    const router = useRouter();
    const dispatch = useDispatch();

    const form = useForm<LoginFormData>({
        resolver: zodResolver(loginSchema),
        defaultValues: {
            email: 'assuncaoallysson5@gmail.com',
            password: 'Senha123#',
        },
        mode: 'onBlur',
    })

    const mutation = useMutation(loginRequest, {
        onSuccess: (data) => {
            dispatch(logout())
            dispatch(login({username: data.username, email: data.email, role: data.role, token: data.token}))
            router.push('dashboard/home')

            toast.success("Login realizado com sucesso!", {
                description: `Bem-vindo, ${data.username}!`,
            })
        },
        onError: (error: unknown) => {
            if (error instanceof AxiosError) {
                toast.error("Erro ao fazer login", {
                    description: error.response?.data?.message || 'Ocorreu um erro inesperado.',
                })
            } else {
                toast.error("Erro ao fazer login", {
                    description: 'Ocorreu um erro inesperado.',
                })
            }
        },
    })

    const onSubmit = (data: LoginFormData) => {
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
                                    name="email"
                                    render={({field}) => (
                                        <FormItem>
                                            <FormLabel>Email</FormLabel>
                                            <FormControl>
                                                <Input placeholder="exemplo@gmail.com" {...field} />
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
                                <div className="flex justify-center">
                                    <Button className="w-full justify-center touch-manipulation"
                                            type="submit" disabled={mutation.isLoading}>
                                        {mutation.isLoading ? <Icons.spinner className="animate-spin"/> : 'Login'}
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
                        <Button variant="outline" className="touch-manipulation"
                                onClick={() => toast.error("Ainda não implementado", {
                                    description: `Essa funcionalidade será implementada em breve`,
                                })}>
                            <Icons.gitHub className="mr-2 h-4 w-4"/>
                            Github
                        </Button>
                        <Button variant="outline" className="touch-manipulation"
                                onClick={() => toast.error("Ainda não implementado", {
                                    description: `Essa funcionalidade será implementada em breve`,
                                })}>
                            <Icons.google className="mr-2 h-4 w-4"/>
                            Google
                        </Button>
                    </div>
                </CardContent>
                <CardFooter className={'justify-center'}>
                    <Link
                        href="/register"
                        className={'text-sm font-medium transition-colors hover:text-primary'}
                    >
                        Crie uma conta
                    </Link>
                </CardFooter>
                <CardFooter className={'justify-center'}>
                    <Link
                        href="/forgot-password"
                        className={'text-sm font-medium transition-colors hover:text-primary'}
                    >
                        Esqueceu a senha?
                    </Link>
                </CardFooter>
            </Card>
        </div>
    )
}

export default LoginForm
