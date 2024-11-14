// ============================================================
// API Docs: http://localhost:8082/account
// ============================================================
import { Get, Post, Put, Delete, FetchAccountHeader } from './accountApiClient';

export async function fetchUserInfo() {
    return FetchAccountHeader(`auth/user-info`);
}

export async function postGoogleLogin(idToken, rememberMe) {
    return Post(`auth/google-login?rememberMe=${rememberMe}`, idToken);
}

export async function fetchPublicKey() {
    return Get(`auth/public-key`);
}

export async function postLogin(account, rememberMe) {
    return Post(`auth/login?rememberMe=${rememberMe}`, account);
}

export async function fetchResetPassword(email) {
    return Get(`auth/reset-password?email=${email}`);
}

export async function postResetPassword(account) {
    return Post('auth/reset-password', account);
}

export async function postRegister(account) {
    return Post('auth/register', account);
}

export async function fetchAccount(id) {
    return Get(`accounts/${id}`);
}

export async function updateAccount(account) {
    return Put(`accounts/${account.id}`, account);
}

export async function updateTheme(id, theme) {
    return Put(`accounts/${id}/theme?theme=${theme}`);
}

export async function postLogout() {
    return Post('auth/logout');
}

export async function fetchAccountGroupList(signal) {
    return Get('accountGroups', signal);
}

export async function fetchFilteredAccountList(text) {
    return Get(`accounts?searchParam=${text}`);
}

export async function createAccount(account) {
    return Post(`accounts`, account);
}

export async function createAccountGroup(group) {
    return Post('accountGroups', group);
}

export async function editAccountGroup(group) {
    return Put(`accountGroups/${group.id}`, group);
}

export async function deleteAccountGroup(groupId) {
    return Delete(`accountGroups/${groupId}`);
}

export async function fetchAccountGroupMemberList(groupId) {
    return Get(`accountGroupMembers?groupId=${groupId}`);
}

export async function addAccountMember(member) {
    return Post(`accountGroupMembers`, member);
}

export async function deleteAccountMember(groupId, accountId) {
    return Delete(`accountGroupMembers/${groupId}/${accountId}`,);
}

export async function fetchRoles() {
    return Get('roles');
}


