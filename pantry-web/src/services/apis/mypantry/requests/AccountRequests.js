// ============================================================
// API Docs: http://localhost:8082/account
// ============================================================
import { FetchAccount, FetchAccountHeader } from '../FetchAccount';

export async function getUserInfo() {
    return FetchAccountHeader(`auth/user-info`);
}

export async function postGoogleLogin(idToken) {
    return FetchAccount('auth/google-login', 'POST', idToken);
}

export async function postLogin(account) {
    return FetchAccount('auth/login', 'POST', account);
}

export async function getResetPassword(email) {
    return FetchAccount(`auth/reset-password?email=${email}`, 'GET');
}

export async function postResetPassword(account) {
    return FetchAccount('auth/reset-password', 'POST', account);
}

export async function postRegister(account) {
    return FetchAccount('auth/register', 'POST', account);
}

export async function getAccount(id) {
    return FetchAccount(`accounts/${id}`, "GET");
}

export async function updateAccount(account) {
    return FetchAccount(`accounts/${account.id}`, 'PUT', account);
}

export async function postLogout() {
    return FetchAccount('auth/logout', 'POST');
}

export async function getAccountGroupList() {
    return FetchAccount('accountGroups', 'GET');
}

export async function getFilteredAccountList(text) {
    return FetchAccount(`accounts?searchParam=${text}`, "GET");
}

export async function createAccount(account) {
    return FetchAccount(`accounts`, "POST");
}

export async function createAccountGroup(group) {
    return FetchAccount('accountGroups', 'POST', group);
}

export async function editAccountGroup(group) {
    return FetchAccount(`accountGroups/${group.id}`, 'PUT', group);
}

export async function deleteAccountGroup(groupId) {
    return FetchAccount(`accountGroups/${groupId}`, 'DELETE');
}

export async function getAccountGroupMemberList(groupId) {
    return FetchAccount(`accountGroups/${groupId}/members`, "GET");
}

export async function deleteAccountMember(groupId, accountId) {
    return FetchAccount(`accountGroups/${groupId}/members/${accountId}`, 'DELETE');
}


