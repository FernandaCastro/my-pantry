import getResponseContent from '../getResponseContent.js';
import RequestError from '../RequestError.js';
import History from '../../../routes/History.js';
import Translator from '../../Translator.js';

export const FetchAccountHeader = async function (endpoint) {

    const service = process.env.REACT_APP_API_URL_ACCOUNT;
    const language = localStorage.getItem('i18nextLng');

    try {
        const response = await fetch(service + '/' + endpoint, {
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json',
                'language': language,
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

    var redirecting = false;
    const language = localStorage.getItem('i18nextLng');

    var headers = {
        'Content-Type': 'application/json',
        'Accept': 'application/json',
        'language': language,
    }

    try {
        const res = await fetch(process.env.REACT_APP_API_URL_ACCOUNT + '/' + endpoint, {
            method,
            headers: headers,
            credentials: 'include',
            body: JSON.stringify(data)
        })

        if (!res) {
            console.log("Fetch API Account-Service: response is null");
            return;
        }

        if (res.ok) {
            const content = await getResponseContent(res)
            return content;
        }

        if (res.status === 401) {
            redirecting = (endpoint === "auth/login") ? false : true;
            const error = Translator.translate('status-401')
            throw new RequestError(error, res.status);
        }

        if (res.status === 403) {
            const error = Translator.translate('status-403')
            throw new RequestError(error, res.status);
        }

        const content = await getResponseContent(res);
        const errorMsg = content ? content.errorMessage : res.statusText;
        console.log("Fetch API Account-Service: $s - $s", res.status, errorMsg);
        throw new RequestError(errorMsg, res.status, content)
    }
    catch (error) {
        throw new RequestError(error.message, error.status)
    } finally {
        if (redirecting) { History.navigate("/logout") }
    }
}