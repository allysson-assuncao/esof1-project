import {FetchPaymentParams, PaymentFilters} from "@/model/Interfaces";
import {report} from "@/services/index";

export const fetchFilteredPayments = async (params: FetchPaymentParams) => {
    const response = await report.post(`/filter-payments`, params.filter, {
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
    console.log(params);
    console.log(response.data);
    return response.data;
}

export const fetchPaymentMethods = async (filters: PaymentFilters) => {
    const response = await report.post('/payment-metrics', filters, {
        headers: {
            'Authorization': `Bearer ${localStorage.getItem('token')}`,
        },
    });
    return response.data;
};
