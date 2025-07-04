import {product} from "@/services/index";

export const fetchSimpleProducts = async () => {
    const response = await product.get(`/select-all-simple`, {
        headers: {
            'Authorization': `Bearer ${localStorage.getItem('token')}`,
        },
    });
    return response.data;
};