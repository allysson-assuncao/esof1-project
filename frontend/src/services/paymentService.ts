import {payment} from "@/services/index";
import {FetchPaymentParams, RegisterPaymentRequest} from "@/model/Interfaces";

export const registerPaymentRequest = async ({ paymentId, data }: { paymentId: number, data: RegisterPaymentRequest }) => {
    const response = await payment.post(`/${paymentId}/register`, data, {
        headers: {
            'Authorization': `Bearer ${localStorage.getItem('token')}`,
        },
    });
    return response.data;
};

export const fetchFilteredPayments = async (params: FetchPaymentParams) => {
    const response = await payment.post(`/reports/filter-payments`, params.filter, {
        headers: {
            'Authorization': `Bearer ${localStorage.getItem('token')}`,
        },
        params: {
            page: params.page || 0,
            size: params.size || 350,
            orderBy: params.orderBy || 'updatedAt',
            direction: params.direction || 'ASC',
        },
    });
    console.log(response);
    console.log(response.data);
    return response.data;
}
