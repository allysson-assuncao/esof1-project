import {guestTab} from "@/services/index";
import {FetchGuestTabParams} from "@/model/Interfaces";

export const fetchFilteredGuestTabs = async (params: FetchGuestTabParams) => {
    const response = await guestTab.post(`/filter`, params.filter, {
        /*headers: {
            /!*'Authorization': `Bearer ${localStorage.getItem('token')}`,*!/
            "Access-Control-Allow-Origin": "",
        },*/
        params: {
            page: params.page || 0,
            size: params.size || 350,
            orderBy: params.orderBy || 'id',
            direction: params.direction || 'ASC',
        },
    });
    console.log(params)
    console.log(response.data)
    return response.data;
};

export const fetchGuestTabs = async (localTableId: string) => {
    const response = await guestTab.get(`/select-all/${localTableId}`, {
        /*headers: {
            'Authorization': `Bearer ${localStorage.getItem('token')}`,
        },*/
    });
    console.log(response.data)
    return response.data;
};
