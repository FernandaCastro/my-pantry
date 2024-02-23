import getResponseContent from '../getResponseContent.js';
import RequestError from '../RequestError.js';

export async function getGoogleProfile(accessToken) {

    const endpoint = `https://www.googleapis.com/oauth2/v1/userinfo?access_token=${accessToken.access_token}`;
    const method = "GET";
    const authorization = `Bearer ${accessToken.access_token}`;

    const response = await fetch(
        endpoint, {
        method,
        headers: {
            Authorization: authorization,
            Accept: 'application/json'
        }
    })
        .catch(error => {
            console.log("Fetch API Google: $s - $s", 'getGoogleProfile', error);
        })

    console.log("response getGoogleProfile: " + response)

    return response;
}