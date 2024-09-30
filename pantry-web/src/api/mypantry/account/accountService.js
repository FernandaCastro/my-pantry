// ============================================================
// API Docs: http://localhost:8082/account
// ============================================================
import { Get, Post, Put, Delete, FetchAccountHeader } from './accountApiClient';

export async function getUserInfo() {
    return FetchAccountHeader(`auth/user-info`);
}

export async function postGoogleLogin(idToken) {
    return Post('auth/google-login', idToken);
}

export async function getPublicKey() {
    return Get(`auth/public-key`);
}

export async function postLogin(account) {
    return Post('auth/login', account);
}

export async function getResetPassword(email) {
    return Get(`auth/reset-password?email=${email}`);
}

export async function postResetPassword(account) {
    return Post('auth/reset-password', account);
}

export async function postRegister(account) {
    return Post('auth/register', account);
}

export async function getAccount(id) {
    return Get(`accounts/${id}`);
}

export async function updateAccount(account) {
    return Put(`accounts/${account.id}`, account);
}

export async function postLogout() {
    return Post('auth/logout');
}

export async function getAccountGroupList(signal) {
    return Get('accountGroups', signal);
}

export async function getFilteredAccountList(text) {
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

export async function getAccountGroupMemberList(groupId) {
    return Get(`accountGroupMembers?groupId=${groupId}`);
}

export async function addAccountMember(member) {
    return Post(`accountGroupMembers`, member);
}

export async function deleteAccountMember(groupId, accountId) {
    return Delete(`accountGroupMembers/${groupId}/${accountId}`,);
}

export async function getRoles() {
    return Get('roles');
}


