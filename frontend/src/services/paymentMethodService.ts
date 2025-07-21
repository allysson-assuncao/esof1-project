import {paymentMethod} from "@/services/index";
import {SimplePaymentMethod} from "@/model/Interfaces";

export const fetchSimplePaymentMethods = async (): Promise<SimplePaymentMethod[]> => {
    const response = await paymentMethod.get('/select-all-simple', {
        headers: {
            'Authorization': `Bearer ${localStorage.getItem('token')}`,
        },
    });
    console.log(response.data);
    return response.data;
};
