import {order} from "@/services/index";
import {RegisterOrdersFormData} from "@/model/FormData";
import {FetchKanbanOrderResultsParams, KanbanOrders} from "@/model/Interfaces";

export const registerOrdersRequest = async (data: RegisterOrdersFormData) => {
    const response = await order.post(`/register`, data, {
        headers: {
            'Authorization': `Bearer ${localStorage.getItem('token')}`,
        },
    });
    console.log(response.data)
    return response.data;
};

export const fetchSimpleOrders = async (localTableId: string) => {
    const response = await order.get(`/select-all/${localTableId}`, {
        headers: {
            'Authorization': `Bearer ${localStorage.getItem('token')}`,
        },
    });
    return response.data;
};

export const fetchFilteredOrderKanbanResults = async (params: FetchKanbanOrderResultsParams): Promise<KanbanOrders> => {
    const response = await order.post(`/filter-kanban`, params.filter, {
        headers: {
            'Authorization': `Bearer ${localStorage.getItem('token')}`,
            'ngrok-skip-browser-warning': '69420',
        },
        params: {
            page: params.page || 0,
            size: params.size || 50,
            orderBy: params.orderBy || 'orderedTime',
            direction: params.direction || 'ASC',
        },
    })
    console.log(params.filter)
    console.log(response.data)
    return response.data
}

export const advanceOrderStatus = async ({ orderId }: { orderId: number }) => {
    const response = await order.get(`/${orderId}/advance`);
    return response.data;
};

export const regressOrderStatus = async ({ orderId }: { orderId: number }) => {
    const response = await order.get(`/${orderId}/regress`);
    return response.data;
};
