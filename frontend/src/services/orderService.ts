import {order} from "@/services/index";

export const fetchSimpleOrders = async (localTableId: string) => {
    const response = await order.get(`/select-all/${localTableId}`, {
        /*headers: {
            'Authorization': `Bearer ${localStorage.getItem('token')}`,
        },*/
    });
    console.log(response.data)
    return response.data;
};
