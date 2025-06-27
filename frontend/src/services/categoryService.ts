// services/categoryService.ts
import {category} from "@/services/index";
import {SimpleCategory} from "@/model/Interfaces";

export const fetchSimpleCategories = async (): Promise<SimpleCategory[]> => {
    try {
        const response = await category.get(`/select-all`, {
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('token')}`,
            }
        });
        console.log("API Response:", response);
        return response.data;
    } catch (error) {
        console.error("API Error Details:", error);
        
        if (error instanceof Error) {
            console.error("Error message:", error.message);
        }
        
        // Handle axios errors specifically
        if (error && typeof error === 'object' && 'response' in error) {
            const axiosError = error as any;
            console.error("Axios error details:", {
                status: axiosError.response?.status,
                data: axiosError.response?.data,
                url: axiosError.config?.url,
                method: axiosError.config?.method
            });
        }
        
        throw new Error("Failed to load categories. Please check your network and API configuration.");
    }
};