import {payment} from "@/services/index";
import {RegisterPaymentRequest} from "@/model/Interfaces";

export const registerPaymentRequest = async ({ paymentId, data }: { paymentId: number, data: RegisterPaymentRequest }) => {
    const response = await payment.post(`/${paymentId}/register`, data, {
        headers: {
            'Authorization': `Bearer ${localStorage.getItem('token')}`,
        },
    });
    return response.data;
};