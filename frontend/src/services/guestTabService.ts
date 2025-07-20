import {guestTab} from "@/services/index";
import {FetchGuestTabParams} from "@/model/Interfaces";
import {GuestTabRegisterFormData} from "@/model/FormData";

export const fetchFilteredGuestTabs = async (params: FetchGuestTabParams) => {
    const response = await guestTab.post(`/filter`, params.filter, {
        headers: {
            'Authorization': `Bearer ${localStorage.getItem('token')}`,
        },
        params: {
            page: params.page || 0,
            size: params.size || 350,
            orderBy: params.orderBy || 'timeOpened',
            direction: params.direction || 'ASC',
        },
    });
    console.log(response);
    console.log(response.data);
    return response.data;
};

export const fetchSimpleGuestTabs = async (localTableId: string) => {
    const response = await guestTab.get(`/select-all/${localTableId}`, {
        headers: {
            'Authorization': `Bearer ${localStorage.getItem('token')}`,
        },
    });
    return response.data;
};

export const registerGuestTab = async (data: GuestTabRegisterFormData) => {
    const response = await guestTab.post(`/register`, data, {
        headers: {
            'Authorization': `Bearer ${localStorage.getItem('token')}`,
        },
    });
    return response.data;
}

export const closeGuestTab = async ({ guestTabId, numberOfPayers }: { guestTabId: number; numberOfPayers: number }) => {
    const response = await guestTab.patch(`/${guestTabId}/close`, { numberOfPayers }, {
        headers: {
            'Authorization': `Bearer ${localStorage.getItem('token')}`,
        },
    });
    return response.data;
};
