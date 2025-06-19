import {user} from "@/services/index";

export const fetchSimpleWaiters = async (localTableId: string) => {
    const response = await user.get(`/select-all/${localTableId}`, {
        /*headers: {
            'Authorization': `Bearer ${localStorage.getItem('token')}`,
        },*/
    });
    console.log(response.data)
    return response.data;
};
