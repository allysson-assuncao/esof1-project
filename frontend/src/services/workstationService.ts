import {WorkstationRegisterFormData} from "@/model/FormData";
import {workstation} from "@/services/index";

export const registerRequest = async (data: WorkstationRegisterFormData) => {
    const response = await workstation.post('/register', data, {});
    return response.data;
}

export const fetchWorkstations = async () => {
    const response = await workstation.get("/select-all", {
        headers: {
            Authorization: `Bearer ${localStorage.getItem("token")}`,
        },
    });
    return response.data;
};

export const fetchSimpleWorkstationsByEmployee = async () => {
    const response = await workstation.get("/select-all-by-employee", {
        headers: {
            Authorization: `Bearer ${localStorage.getItem("token")}`,
        },
    });
    return response.data;
};
