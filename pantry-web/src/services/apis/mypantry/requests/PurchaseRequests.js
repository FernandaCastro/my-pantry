// ============================================================
// API Docs: http://localhost:8081/purchases
// ============================================================
import FetchPurchase from '../FetchPurchase';

export async function getAllPurchaseOrders(pantryIds) {
    return FetchPurchase(`purchases?pantryIds=${pantryIds}`, "GET");
}

export async function getOpenPurchaseOrder(pantryIds) {
    return FetchPurchase(`purchases/open?pantryIds=${pantryIds}`, "GET");
}

export async function getPendingPurchaseItems(pantryIds, supermarket) {
    return !supermarket || supermarket === '' || supermarket === '-' ?
        FetchPurchase(`purchases/items?pantryIds=${pantryIds}`, "GET") :
        FetchPurchase(`purchases/items?pantryIds=${pantryIds}&supermarketId=${supermarket}`, "GET");
}

export async function getPurchaseItems(id, pantryIds, supermarket) {
    return !supermarket || supermarket === '' || supermarket === '-' ?
        FetchPurchase(`purchases/${id}/items?pantryIds=${pantryIds}`, "GET") :
        FetchPurchase(`purchases/${id}/items?pantryIds=${pantryIds}&supermarketId=${supermarket}`, "GET");
}

export async function postNewPurchaseOrder(pantryIds) {
    return FetchPurchase(`purchases/new`, "POST", pantryIds);
}

export async function postClosePurchaseOrder(purchasedItems) {
    return FetchPurchase(`purchases/close`, "POST", purchasedItems);
}

export async function getProperty(key) {
    return FetchPurchase(`properties/${key}`, "GET");
}

export async function getAllProperty(key) {
    return FetchPurchase(`properties?key=${key}`, "GET");
}

export async function getAllSupermarkets() {
    return FetchPurchase(`supermarkets/all`, "GET");
}

export async function getSupermarketsByGroup(groupId) {
    return FetchPurchase(`supermarkets?groupId=${groupId}`, "GET");
}

export async function updateSupermarket(supermaketId, body) {
    return FetchPurchase(`supermarkets/${supermaketId}`, "PUT", body);
}
export async function createSupermarket(body) {
    return FetchPurchase(`supermarkets`, "POST", body);
}
export async function deleteSupermarket(supermaketId) {
    return FetchPurchase(`supermarkets/${supermaketId}`, "DELETE");
}