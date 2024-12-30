import axios from "axios";
import React from 'react';

export const UserContext = React.createContext({});

export async function getUserContext() {
    let response;
    
    axios.defaults.withCredentials = true;
    await axios.post('http://localhost:8080/api/v1/get-user-context', {}, {})
        .then((res) => {
            response = JSON.parse(res.request.responseText)
        }).catch((err)=>{
            console.log(err)
    })
    return response
}

