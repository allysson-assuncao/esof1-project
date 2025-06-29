import {WorkstationRegisterFormData} from "@/model/FormData";
import {workstation} from "@/services/index";

export const registerRequest = async (data: WorkstationRegisterFormData) => {
    const response = await workstation.post('/register', data, {});
    return response.data;
}