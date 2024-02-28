import { getUserInfo, postGoogleLogin, postLogout, postLogin, postRegister } from '../services/apis/mypantry/requests/AccountRequests.js';

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

export async function register(account) {
    try {
        return await postRegister(account);
    } catch (error) {
        console.log(`postRegister Failed: ${error}`);
        throw error;
    }
}

export async function logout() {
    await postLogout();
    //call logout from AuthProvider - Google
    console.log('Logged out!');
}

