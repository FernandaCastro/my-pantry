// ============================================================
// API Docs: http://localhost:8081/purchases
// ============================================================
import { FetchPurchase, PostPurchase, PutPurchase, DeletePurchase } from '../FetchPurchase';

export async function getAllPurchaseOrders(pantryIds, signal) {
    return FetchPurchase(`purchases?pantryIds=${pantryIds}`, signal);
}

export async function getOpenPurchaseOrder(pantryIds) {
    return FetchPurchase(`purchases/open?pantryIds=${pantryIds}`);
}

export async function getPendingPurchaseItems(pantryIds, supermarket, signal) {
    return !supermarket || supermarket === '' || supermarket === '-' ?
        FetchPurchase(`purchases/items?pantryIds=${pantryIds}`, signal) :
        FetchPurchase(`purchases/items?pantryIds=${pantryIds}&supermarketId=${supermarket}`, signal);
}

export async function getPurchaseItems(id, pantryIds, supermarket, signal) {
    return !supermarket || supermarket === '' || supermarket === '-' ?
        FetchPurchase(`purchases/${id}/items?pantryIds=${pantryIds}`, signal) :
        FetchPurchase(`purchases/${id}/items?pantryIds=${pantryIds}&supermarketId=${supermarket}`, signal);
}

export async function postNewPurchaseOrder(pantryIds) {
    return PostPurchase(`purchases/new`, pantryIds);
}

export async function postClosePurchaseOrder(purchasedItems) {
    return PostPurchase(`purchases/close`, purchasedItems);
}

export async function getProperty(key) {
    return FetchPurchase(`properties/${key}`);
}

export async function getAllProperty(key) {
    return FetchPurchase(`properties?key=${key}`);
}

export async function getAllSupermarkets(signal) {
    return FetchPurchase(`supermarkets/all`, signal);
}

export async function getSupermarketsByGroup(groupId) {
    return FetchPurchase(`supermarkets?groupId=${groupId}`);
}

export async function updateSupermarket(supermaketId, body) {
    return PutPurchase(`supermarkets/${supermaketId}`, body);
}
export async function createSupermarket(body) {
    return PostPurchase(`supermarkets`, body);
}
export async function deleteSupermarket(supermaketId) {
    return DeletePurchase(`supermarkets/${supermaketId}`);
}