import getResponseContent from '../getResponseContent.js';
import RequestError from '../RequestError.js';
import History from '../../../util/History.js';
import Translator from '../../../util/Translator.js';

const language = localStorage.getItem('i18nextLng');
const headers = {
    'Content-Type': 'application/json',
    'Accept': 'application/json',
    'language': language,
}

export async function Get(endpoint, signal) {
    var redirecting = false;
    try {
        const res = await fetch(`${process.env.REACT_APP_API_URL_PANTRY}/${endpoint}`, {
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
            redirecting = true;
        }
        throw new RequestError(error.message, error.status)
    } finally {
        if (redirecting) { History.navigate("/logout") }
    }
}

export async function Post(endpoint, body) {
    var redirecting = false;
    try {
        const res = await fetch(`${process.env.REACT_APP_API_URL_PANTRY}/${endpoint}`, {
            method: "POST",
            headers: headers,
            credentials: 'include',
            body: JSON.stringify(body)
        })

        return await processResponse(res);

    } catch (error) {
        if (error.status === 401) {
            redirecting = true;
        }

        throw new RequestError(error.message, error.status)
    } finally {
        if (redirecting) { History.navigate("/logout") }
    }

}

export async function Put(endpoint, body) {
    var redirecting = false;
    try {
        const res = await fetch(`${process.env.REACT_APP_API_URL_PANTRY}/${endpoint}`, {
            method: "PUT",
            headers: headers,
            credentials: 'include',
            body: JSON.stringify(body)
        })

        return await processResponse(res);

    } catch (error) {
        if (error.status === 401) {
            redirecting = true;
        }
        throw new RequestError(error.message, error.status)
    } finally {
        if (redirecting) { History.navigate("/logout") }
    }
}

export async function Delete(endpoint) {
    var redirecting = false;
    try {
        const res = await fetch(`${process.env.REACT_APP_API_URL_PANTRY}/${endpoint}`, {
            method: "DELETE",
            headers: headers,
            credentials: 'include',
        })

        return await processResponse(res);

    } catch (error) {
        if (error.status === 401) {
            redirecting = true;
        }
        throw new RequestError(error.message, error.status)
    } finally {
        if (redirecting) { History.navigate("/logout") }
    }
}

async function processResponse(res) {

    if (!res) {
        console.log("Fetch API PantryService: response is null");
        return;
    }

    if (res.ok) {
        const content = await getResponseContent(res)
        return content;
    }

    if (res.status === 401) {
        const error = Translator.translate('status-401')
        throw new RequestError(error, res.status);
    }

    if (res.status === 403) {
        const error = Translator.translate('status-403');
        throw new RequestError(error, res.status);
    }

    const content = await getResponseContent(res);
    const errorMsg = res.statusText === '' && content ? content.errorMessage : res.statusText;
    console.log("PantryService api response: $s - $s", res.status, errorMsg);
    throw new RequestError(errorMsg, res.status, content)
}