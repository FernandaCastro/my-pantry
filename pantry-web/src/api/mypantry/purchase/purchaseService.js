// ============================================================
// API Docs: http://localhost:8081/purchases
// ============================================================
import { Get, Post, Put, Delete } from './purchaseApiClient';

export async function getAllPurchaseOrders(pantryIds, signal) {
    return Get(`purchases?pantryIds=${pantryIds}`, signal);
}

export async function getOpenPurchaseOrder(pantryIds) {
    return Get(`purchases/open?pantryIds=${pantryIds}`);
}

export async function getPendingPurchaseItems(pantryIds, supermarket, signal) {
    return !supermarket || supermarket === '' || supermarket === '-' ?
        Get(`purchases/items?pantryIds=${pantryIds}`, signal) :
        Get(`purchases/items?pantryIds=${pantryIds}&supermarketId=${supermarket}`, signal);
}

export async function getPurchaseItems(id, pantryIds, supermarket, signal) {
    return !supermarket || supermarket === '' || supermarket === '-' ?
        Get(`purchases/${id}/items?pantryIds=${pantryIds}`, signal) :
        Get(`purchases/${id}/items?pantryIds=${pantryIds}&supermarketId=${supermarket}`, signal);
}

export async function postNewPurchaseOrder(pantryIds) {
    return Post(`purchases/new`, pantryIds);
}

export async function postClosePurchaseOrder(purchasedItems) {
    return Post(`purchases/close`, purchasedItems);
}

export async function getProperty(key) {
    return Get(`properties/${key}`);
}

export async function getAllProperty(key) {
    return Get(`properties?key=${key}`);
}

export async function getAllSupermarkets(signal) {
    return Get(`supermarkets/all`, signal);
}

export async function getSupermarketsByGroup(groupId) {
    return Get(`supermarkets?groupId=${groupId}`);
}

export async function updateSupermarket(supermaketId, body) {
    return Put(`supermarkets/${supermaketId}`, body);
}
export async function createSupermarket(body) {
    return Post(`supermarkets`, body);
}
export async function deleteSupermarket(supermaketId) {
    return Delete(`supermarkets/${supermaketId}`);
}