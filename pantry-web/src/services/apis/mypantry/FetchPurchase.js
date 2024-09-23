import getResponseContent from '../getResponseContent';
import RequestError from '../RequestError';
import History from '../../../routes/History.js';
import Translator from '../../Translator.js';

const language = localStorage.getItem('i18nextLng');
const headers = {
    'Content-Type': 'application/json',
    'Accept': 'application/json',
    'language': language,
}

export async function FetchPurchase(endpoint, signal) {
    var redirecting = false;
    try {
        const res = await fetch(`${process.env.REACT_APP_API_URL_PURCHASE}/${endpoint}`, {
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

export async function PostPurchase(endpoint, body) {
    var redirecting = false;
    try {
        const res = await fetch(`${process.env.REACT_APP_API_URL_PURCHASE}/${endpoint}`, {
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

export async function PutPurchase(endpoint, body) {
    var redirecting = false;
    try {
        const res = await fetch(`${process.env.REACT_APP_API_URL_PURCHASE}/${endpoint}`, {
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

export async function DeletePurchase(endpoint) {
    var redirecting = false;
    try {
        const res = await fetch(`${process.env.REACT_APP_API_URL_PURCHASE}/${endpoint}`, {
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
        console.log("PurchaseService api response is null");
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
    console.log("PurchaseService api response: $s - $s", res.status, errorMsg);
    throw new RequestError(errorMsg, res.status, content)
}