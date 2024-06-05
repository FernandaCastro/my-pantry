import getResponseContent from '../getResponseContent';
import RequestError from '../RequestError';
import History from '../../../routes/History.js';
import Translator from '../../Translator.js';

const FetchPurchase = async function (endpoint, method, data) {

    var redirecting = false;
    const language = localStorage.getItem('i18nextLng');

    try {
        const res = await fetch(process.env.REACT_APP_API_URL_PURCHASE + '/' + endpoint, {
            method,
            "headers": {
                'Content-Type': 'application/json',
                'Accept': 'application/json',
                'language': language,
            },
            credentials: 'include',
            body: JSON.stringify(data)
        })

        if (!res) {
            console.log("Fetch API Purchase-Service: response is null");
            return;
        }

        if (res.ok) {
            const content = await getResponseContent(res)
            return content;
        }

        if (res.status === 401) {
            redirecting = true;
            const error = Translator.translate('status-401')
            throw new RequestError(error, res.status);
        }

        if (res.status === 403) {
            const error = Translator.translate('status-403')
            throw new RequestError(error, res.status);
        }

        const content = await getResponseContent(res);
        const errorMsg = res.statusText === '' && content ? content.errorMessage : res.statusText;
        console.log("Fetch API Purchase-Service: $s - $s", res.status, errorMsg);
        throw new RequestError(errorMsg, res.status, content)
    }
    catch (error) {
        throw new RequestError(error.message, error.status)
    } finally {
        if (redirecting) { History.navigate("/logout") }
    }
}

export default FetchPurchase;