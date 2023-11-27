import getResponseContent from '../requests/getResponseContent';
import RequestError from '../requests/RequestError';
import config from './config_mypantry';

const FetchPurchase = async function (endpoint, method, data) {

    const response = await fetch(`${config.API_URL_PURCHASE}/${endpoint}`, {
        method,
        "headers": {
            'Content-Type': 'application/json',
            'Accept': 'application/json',
        },
        body: JSON.stringify(data)
    })

    const content = await getResponseContent(response)

    if (response.ok) return content;
    throw new RequestError(response.statusText, response.status, content)
}

export default FetchPurchase;