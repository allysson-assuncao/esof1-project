import axios from "axios";

const port = "8080";

/*const port = "8081";*/

export const auth = axios.create({
    baseURL: `http://localhost:${port}/app/auth`,
});

export const user = axios.create({
    baseURL: `http://localhost:${port}/app/user`,
});

export const localTable = axios.create({
    baseURL: `http://localhost:${port}/app/local-table`,
});

export const guestTab = axios.create({
    baseURL: `http://localhost:${port}/app/guest-tab`,
});

export const order = axios.create({
    baseURL: `http://localhost:${port}/app/order`,
});
