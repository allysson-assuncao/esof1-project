import axios from "axios";

export const auth = axios.create({
    baseURL: 'http://localhost:8080/app/auth',
});

export const testURL = axios.create({
    baseURL: 'http://localhost:8080/app/auth',
});

export const orders = axios.create({
    baseURL: 'http://localhost:8080/app/orders',
});
