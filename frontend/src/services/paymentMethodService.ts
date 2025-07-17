import {paymentMethod} from "@/services/index";
import {SimplePaymentMethod} from "@/model/Interfaces";

export const fetchAllPaymentMethods = async (): Promise<SimplePaymentMethod[]> => {
    const response = await paymentMethod.get('/select-all-simple', {
        headers: {
            'Authorization': `Bearer ${localStorage.getItem('token')}`,
        },
    });
    return response.data;
};