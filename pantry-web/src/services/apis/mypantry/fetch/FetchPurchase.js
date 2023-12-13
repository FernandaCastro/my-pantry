import getResponseContent from '../requests/getResponseContent';
import RequestError from '../requests/RequestError';

const FetchPurchase = async function (endpoint, method, data) {

    const response = await fetch(process.env.REACT_APP_API_URL_PURCHASE + '/' + endpoint, {
        method,
        "headers": {
            'Content-Type': 'application/json',
            'Accept': 'application/json',
        },
        body: JSON.stringify(data)
    })

    const content = await getResponseContent(response)

    if (response.ok) return content;

    const errorMsg = response.statusText === '' && content ? content.errorMessage : response.statusText;
    console.log("Fetch API Purchase: " + response.status + " - " + errorMsg);
    throw new RequestError(errorMsg, response.status, content)
}

export default FetchPurchase;