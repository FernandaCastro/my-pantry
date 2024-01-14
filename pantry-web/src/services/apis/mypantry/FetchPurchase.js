import getResponseContent from '../getResponseContent';
import RequestError from '../RequestError';

const FetchPurchase = async function (endpoint, method, data) {

    try {
        const res = await fetch(process.env.REACT_APP_API_URL_PURCHASE + '/' + endpoint, {
            method,
            "headers": {
                'Content-Type': 'application/json',
                'Accept': 'application/json',
            },
            credentials: 'include',
            body: JSON.stringify(data)
        })

        if (!res) {
            console.log("Fetch API Purchase-Service: response is null");
            return;
        }

        if (res.ok) {
            const content = getResponseContent(res)
            return content;
        }

        if (res.status === 403) {
            const error = 'Status 403: User is not authorized.'
            throw new RequestError(error, res.status);
        }

        const content = getResponseContent(res);
        const errorMsg = res.statusText === '' && content ? content.errorMessage : res.statusText;
        console.log("Fetch API Purchase-Service: $s - $s", res.status, errorMsg);
        throw new RequestError(errorMsg, res.status, content)
    }
    catch (error) {
        throw new RequestError(error.message, error.status)
    }
}

export default FetchPurchase;