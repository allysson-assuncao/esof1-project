import axios from "axios";

/*const port = "8080";*/

const port = "8081";

export const auth = axios.create({
    baseURL: `http://localhost:${port}/app/auth`,
});

export const testURL = axios.create({
    baseURL: `http://localhost:${port}/app/auth`,
});

export const guestTab = axios.create({
    baseURL: `http://localhost:${port}/app/guest-tab`,
});
