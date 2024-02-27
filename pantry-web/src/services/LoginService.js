import { getUserInfo, postGoogleLogin, postLogout, postLogin, postSignup } from '../services/apis/mypantry/requests/AccountRequests.js';

export async function initLogin() {
    try {
        return await getUserInfo();

    } catch (error) {
        console.log(`initLogin Failed: ${error}`);
        throw error;
    }
};

export async function postLoginToken(credential) {
    try {
        return await postGoogleLogin(credential);
    } catch (error) {
        console.log(`postLoginToken Failed: ${error}`);
        throw error;
    }
}

export async function login(account) {
    try {
        return await postLogin(account);
    } catch (error) {
        console.log(`login Failed: ${error}`);
        throw error;
    }
}

export async function signup(account) {
    try {
        return await postSignup(account);
    } catch (error) {
        console.log(`signup Failed: ${error}`);
        throw error;
    }
}

export async function logout() {
    await postLogout();
    //call logout from AuthProvider - Google
    console.log('Logged out!');
}

