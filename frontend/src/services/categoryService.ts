// services/categoryService.ts

import {category} from "@/services/index";
import {CategoryFormData} from "@/model/FormData";
import {SimpleCategory} from "@/model/Interfaces";

/**
 * Cadastra uma nova categoria.
 * Requisição POST para /app/category/register
 */
export const registerCategoryService = async (data: CategoryFormData) => {
    try {
        const response = await category.post(`/register`, data, {
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('token')}`,
            }
        });
        return response.data;
    } catch (error) {
        console.error("Erro ao registrar categoria:", error);

        // Tratamento específico para erros do axios
        if (error && typeof error === 'object' && 'response' in error) {
            const axiosError = error as any;
            console.error("Detalhes do erro:", {
                status: axiosError.response?.status,
                data: axiosError.response?.data,
                url: axiosError.config?.url,
                method: axiosError.config?.method
            });
        }

        throw new Error("Falha ao registrar categoria. Verifique sua conexão e tente novamente.");
    }
};

/**
 * Busca todas as categorias em formato simplificado.
 * Requisição GET para /app/category/select-all
 */
export const fetchSimpleCategories = async (): Promise<SimpleCategory[]> => {
    try {
        const response = await category.get(`/select-all`, {
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('token')}`,
            }
        });
        return response.data;
    } catch (error) {
        console.error("Erro ao buscar categorias:", error);

        if (error instanceof Error) {
            console.error("Mensagem:", error.message);
        }

        if (error && typeof error === 'object' && 'response' in error) {
            const axiosError = error as any;
            console.error("Detalhes do erro:", {
                status: axiosError.response?.status,
                data: axiosError.response?.data,
                url: axiosError.config?.url,
                method: axiosError.config?.method
            });
        }

        throw new Error("Não foi possível carregar as categorias.");
    }
};