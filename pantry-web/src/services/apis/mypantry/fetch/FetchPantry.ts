import getResponseContent from '../requests/getResponseContent';
import RequestError from '../requests/RequestError';
import config from './config_mypantry';

const FetchPantry = async function (endpoint: string, method: 'GET' | 'POST' | 'PUT' | 'DELETE', data?: any) {

    const response = await fetch(`${config.API_URL_PANTRY}/${endpoint}`, {
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

export default FetchPantry;