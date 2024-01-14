// ============================================================
// API Docs: http://localhost:8082/account
// ============================================================
import { FetchAccount, FetchAccountHeader } from '../FetchAccount';

export async function getUserInfo() {
    return FetchAccountHeader(`oauth/user-info`);
}

export async function postLogin(idToken) {
    return FetchAccount('oauth/login', 'POST', idToken);
}

export async function postLogout() {
    return FetchAccount('oauth/logout', 'GET');
}
