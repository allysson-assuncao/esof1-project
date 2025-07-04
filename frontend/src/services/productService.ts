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


export const registerProduct = async (data: ProductRegisterFormData) => {
    const response = await product.post('/register', data, {});
    return response.data;
}