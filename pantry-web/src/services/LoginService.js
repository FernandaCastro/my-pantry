import { getUserInfo, postLogin, postLogout } from '../services/apis/mypantry/requests/AccountRequests.js';

export async function initLogin() {
    try {
        return await getUserInfo();

    } catch (error) {
        console.log(`initLogin Failed: ${error}`);
    }
};

export async function postLoginToken(credential) {
    try {
        return await postLogin(credential);
    } catch (error) {
        console.log(`postLoginToken Failed: ${error}`);
    }
}

export async function logout() {
    await postLogout();
    //call logout from AuthProvider - Google
    console.log('Logged out!');
}

