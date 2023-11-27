import getResponseContent from '../requests/getResponseContent.js';
import RequestError from '../requests/RequestError.js';
import config from './config_mypantry.js';

const FetchPantry = async function (endpoint, method, data) {

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