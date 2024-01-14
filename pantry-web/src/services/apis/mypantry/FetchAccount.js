import getResponseContent from '../getResponseContent.js';
import RequestError from '../RequestError.js';

export const FetchAccountHeader = async function (endpoint) {
    const service = process.env.REACT_APP_API_URL_ACCOUNT;

    try {
        const response = await fetch(service + '/' + endpoint, {
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json',
            },
            credentials: 'include',
        })
        const content = await getResponseContent(response)
        if (response.ok) return content;

        const errorMsg = response.statusText === '' && content ? content.errorMessage : response.statusText;
        console.log("Fetch API Account: $s - $s", response.status, errorMsg);
        throw new RequestError(errorMsg, response.status, content)
    } catch (error) {
        throw new RequestError(error.message, error.status)
    }
}

export const FetchAccount = async function (endpoint, method, data) {
    try {
        const response = await fetch(process.env.REACT_APP_API_URL_ACCOUNT + '/' + endpoint, {
            method,
            "headers": {
                'Content-Type': 'application/json',
                'Accept': 'application/json',
            },
            credentials: 'include',
            body: JSON.stringify(data)
        })
        const content = await getResponseContent(response)
        if (response.ok) return content;

        const errorMsg = response.statusText === '' && content ? content.errorMessage : response.statusText;
        console.log("Fetch API Account-Service: $s - $s", response.status, errorMsg);
        throw new RequestError(errorMsg, response.status, content)
    } catch (error) {
        throw new RequestError(error.message, error.status)
    }
}