// services/categoryService.ts
import {category} from "@/services/index";
import {SimpleCategory} from "@/model/Interfaces";

export const fetchSimpleCategories = async (): Promise<SimpleCategory[]> => {
    try {
        const response = await category.get(`/select-all/`);
        console.log("API Response:", response);
        return response.data;
    } catch (error) {
        console.error("API Error Details:", {
            message: error.message,
            url: error.config?.url,
            method: error.config?.method,
            status: error.response?.status,
            data: error.response?.data
        });
        throw new Error("Failed to load categories. Please check your network and API configuration.");
    }
};