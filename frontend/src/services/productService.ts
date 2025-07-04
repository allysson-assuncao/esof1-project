import {product} from "@/services/index";
import {ProductRegisterFormData} from "@/model/FormData";

export const fetchSimpleProducts = async () => {
    const response = await product.get(`/select-all-simple`, {
        headers: {
            'Authorization': `Bearer ${localStorage.getItem('token')}`,
        },
    });
    return response.data;
};

export const fetchSimpleProductsIfAdditional = async (isAdditional: boolean) => {
    const response = await product.get(`/select-all-simple/${isAdditional}`, {
        headers: {
            'Authorization': `Bearer ${localStorage.getItem('token')}`,
        },
    });
    console.log(isAdditional);
    return response.data;
};

export const registerProduct = async (data: ProductRegisterFormData) => {
    const response = await product.post('/register', data, {});
    return response.data;
}