import {orders} from "@/services/index";
import {FetchOrdersParams} from "@/model/Interfaces";

export const fetchFilteredProcesses = async (params: FetchOrdersParams) => {
    const response = await orders.post(`/filter`, params.filter, {
        headers: {
            'Authorization': `Bearer ${localStorage.getItem('token')}`,
        },
        params: {
            page: params.page || 0,
            size: params.size || 350,
            orderBy: params.orderBy || 'id',
            direction: params.direction || 'ASC',
        },
    });
    console.log(params.filter)
    return response.data;
};