"use client";

import { category, product } from "./index";
import { HierarchicalCategoryDTO, ProductDTO } from "@/model/Interfaces";


export const fetchMenuStructure = async (): Promise<HierarchicalCategoryDTO[]> => {

    const response = await category.get('/structure');
    return response.data;
};

export const fetchProductsByCategoryId = async (categoryId: string): Promise<ProductDTO[]> => {

    const response = await product.get(`/by-category/${categoryId}`);
    return response.data;
};