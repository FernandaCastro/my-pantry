import getResponseContent from '../getResponseContent.js';
import RequestError from '../RequestError.js';
import History from '../../../routes/History.js';

// function getCookie(name) {
//     const cookieValue = document.cookie.match('(^|;)\\s*' + name + '\\s*=\\s*([^;]+)');
//     return cookieValue ? cookieValue.pop() : '';
// }

// function isCSRFMethod(method) {
//     if (method === 'POST' || method === 'PUT' || method === 'PATCH')
//         return true;
//     return false;
// }

export const FetchAccountHeader = async function (endpoint) {
    const service = process.env.REACT_APP_API_URL_ACCOUNT;

    try {
        const response = await fetch(service + '/' + endpoint, {
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json',
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

    var headers = {
        'Content-Type': 'application/json',
        'Accept': 'application/json',
    }

    // if (isCSRFMethod(method)) {
    //     var tokenCSRF = getCookie('XSRF-TOKEN');
    //     headers = {
    //         ...headers,
    //         'X-XSRF-TOKEN': tokenCSRF
    //     };
    // }

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
            const content = getResponseContent(res)
            return content;
        }

        if (res.status === 401) {
            redirecting = true;
            const error = 'Status 401: User is not authorized.'
            throw new RequestError(error, res.status);
        }

        if (res.status === 403) {
            const error = 'Status 403: User is forbidden.'
            throw new RequestError(error, res.status);
        }

        const content = getResponseContent(res);
        const errorMsg = res.statusText === '' && content ? content.errorMessage : res.statusText;
        console.log("Fetch API Account-Service: $s - $s", res.status, errorMsg);
        throw new RequestError(errorMsg, res.status, content)
    } catch (error) {
        throw new RequestError(error.message, error.status, error.body)
    } finally {
        if (redirecting) { History.navigate("/logout") }
    }
}