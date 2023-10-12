import { API_AUTH_TOKEN, API_ROOT_URL } from "./config.js";
import http from 'k6/http';

const options = {

}

const getUsers = () => {
    return http.get(`https://localhost/health`, options);
}

export const events = [];

events.push({
    name:'getUsers',
    event:getUsers
})
