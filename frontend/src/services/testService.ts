import {testURL} from "@/services/index";

export const test = async (msg: string) => {
    const response = await testURL.post('/test', msg);
    console.log(response.data);
    return response.data;
}