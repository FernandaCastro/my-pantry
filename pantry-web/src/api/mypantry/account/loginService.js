import {postGoogleLogin, postLogout, postLogin, postRegister, fetchUserInfo } from './accountService.js';

export async function initLogin() {
    try {
        return await fetchUserInfo();

    } catch (error) {
        console.log(`initLogin Failed: ${error}`);
        throw error;
    }
};

export async function postLoginToken(credential, rememberMe) {
    try {
        return await postGoogleLogin(credential, rememberMe);
    } catch (error) {
        console.log(`postLoginToken Failed: ${error}`);
        throw error;
    }
}

export async function login(account, rememberMe) {
    try {
        return await postLogin(account, rememberMe);
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

