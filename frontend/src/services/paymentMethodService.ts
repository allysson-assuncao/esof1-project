import {paymentMethod} from "@/services/index";
import {SimplePaymentMethod} from "@/model/Interfaces";

export const fetchAllPaymentMethods = async (): Promise<SimplePaymentMethod[]> => {
    const response = await paymentMethod.get('/', {
        headers: {
            'Authorization': `Bearer ${localStorage.getItem('token')}`,
        },
    });
    return response.data;
};