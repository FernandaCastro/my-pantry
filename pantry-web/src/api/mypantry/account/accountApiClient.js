import getResponseContent from '../getResponseContent.js';
import RequestError from '../RequestError.js';
import { history } from '../../../util/history.js';
import { translator } from '../../../util/translator.js';

const language = localStorage.getItem('i18nextLng');
const headers = {
    'Content-Type': 'application/json',
    'Accept': 'application/json',
    'language': language,
}

export const FetchAccountHeader = async function (endpoint) {

    try {
        const response = await fetch(`${process.env.REACT_APP_API_URL_ACCOUNT}/${endpoint}`, {
            headers: headers,
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

export async function Get(endpoint, signal) {
    var redirecting = false;
    try {
        const res = await fetch(`${process.env.REACT_APP_API_URL_ACCOUNT}/${endpoint}`, {
            method: "GET",
            headers: headers,
            credentials: 'include',
            signal: signal
        })

        return await processResponse(res);

    } catch (error) {
        if (error.name === 'AbortError') {
            throw new RequestError("", error.name)
        }
        if (error.status === 401) {
            redirecting = (endpoint === "auth/login") ? false : true;
        }
        throw new RequestError(error.message, error.status)
    } finally {
        if (redirecting) { history.navigate("/logout") }
    }
}

export async function Post(endpoint, body) {
    var redirecting = false;
    try {
        const res = await fetch(`${process.env.REACT_APP_API_URL_ACCOUNT}/${endpoint}`, {
            method: "POST",
            headers: headers,
            credentials: 'include',
            body: JSON.stringify(body)
        })

        return await processResponse(res);

    } catch (error) {
        if (error.status === 401) {
            redirecting = (endpoint === "auth/login") ? false : true;
        }

        throw new RequestError(error.message, error.status)
    } finally {
        if (redirecting) { history.navigate("/logout") }
    }

}

export async function Put(endpoint, body) {
    var redirecting = false;
    try {
        const res = await fetch(`${process.env.REACT_APP_API_URL_ACCOUNT}/${endpoint}`, {
            method: "PUT",
            headers: headers,
            credentials: 'include',
            body: JSON.stringify(body)
        })

        return await processResponse(res);

    } catch (error) {
        if (error.status === 401) {
            redirecting = (endpoint === "auth/login") ? false : true;
        }
        throw new RequestError(error.message, error.status)
    } finally {
        if (redirecting) { history.navigate("/logout") }
    }
}

export async function Delete(endpoint) {
    var redirecting = false;
    try {
        const res = await fetch(`${process.env.REACT_APP_API_URL_ACCOUNT}/${endpoint}`, {
            method: "DELETE",
            headers: headers,
            credentials: 'include',
        })

        return await processResponse(res);

    } catch (error) {
        if (error.status === 401) {
            redirecting = (endpoint === "auth/login") ? false : true;
        }
        throw new RequestError(error.message, error.status)
    } finally {
        if (redirecting) { history.navigate("/logout") }
    }
}

async function processResponse(res) {

    if (!res) {
        console.log("AccountService api response is null.");
        return;
    }

    if (res.ok) {
        const content = await getResponseContent(res)
        return content;
    }

    if (res.status === 401) {
        const error = translator.translate('status-401')
        throw new RequestError(error, res.status);
    }

    if (res.status === 403) {
        const error = translator.translate('status-403');
        throw new RequestError(error, res.status);
    }

    const content = await getResponseContent(res);
    const errorMsg = res.statusText === '' && content ? content.errorMessage : res.statusText;
    console.log("AccountService api response: $s - $s", res.status, errorMsg);
    throw new RequestError(errorMsg, res.status, content)
}