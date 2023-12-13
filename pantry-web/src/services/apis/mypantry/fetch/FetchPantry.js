import getResponseContent from '../requests/getResponseContent.js';
import RequestError from '../requests/RequestError.js';

const FetchPantry = async function (endpoint, method, data) {

    const response = await fetch(process.env.REACT_APP_API_URL_PANTRY + '/' + endpoint, {
        method,
        //mode: 'no-cors',
        "headers": {
            'Content-Type': 'application/json',
            'Accept': 'application/json',
        },
        body: JSON.stringify(data)
    })

    const content = await getResponseContent(response)

    if (response.ok) return content;

    const errorMsg = response.statusText === '' && content ? content.errorMessage : response.statusText;
    console.log("Fetch API Pantry: $s - $s", response.status, errorMsg);
    throw new RequestError(errorMsg, response.status, content)
}

export default FetchPantry;