import {localTable} from "@/services/index";
import {LocalTableRegisterFormData} from "@/model/FormData";

export const fetchLocalTables = async () => {
    const response = await localTable.get(`/select-all`, {
        headers: {
            'Authorization': `Bearer ${localStorage.getItem('token')}`,
        },
    });
    console.log(response.data)
    return response.data;
};

export const registerLocalTable = async (data: LocalTableRegisterFormData) => {
    const response = await localTable.post(`/register`, data, {});
    return response.data;
}