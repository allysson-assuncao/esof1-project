import {order} from "@/services/index";
import {RegisterOrdersFormData} from "@/model/FormData";

export const registerOrdersRequest = async (data: RegisterOrdersFormData) => {
    const response = await order.post(`/register`, data, {
        headers: {
            'Authorization': `Bearer ${localStorage.getItem('token')}`,
        },
    });
    console.log(data)
    console.log(response.data)
    return response.data;
};

export const fetchSimpleOrders = async (localTableId: string) => {
    const response = await order.get(`/select-all/${localTableId}`, {
        headers: {
            'Authorization': `Bearer ${localStorage.getItem('token')}`,
        },
    });
    console.log(response.data)
    return response.data;
};
