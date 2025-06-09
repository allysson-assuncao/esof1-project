import {localTable} from "@/services/index";

export const fetchLocalTables = async () => {
    const response = await localTable.get(`/select-all`, {
        /*headers: {
            'Authorization': `Bearer ${localStorage.getItem('token')}`,
        },*/
    });
    console.log(response.data)
    return response.data;
};