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


