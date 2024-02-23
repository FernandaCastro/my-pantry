// ============================================================
// API Docs: http://localhost:8081/purchases
// ============================================================
import FetchPurchase from '../FetchPurchase';

export async function getOpenPurchaseOrder() {
    return FetchPurchase(`purchases/open`, "GET");
}

export async function getPendingPurchaseItems(supermarket) {
    return !supermarket || supermarket === '' || supermarket === '-' ?
        FetchPurchase(`purchases/items`, "GET") :
        FetchPurchase(`purchases/items?supermarket=${supermarket}`, "GET");
}

export async function getPurchaseItems(id, supermarket) {
    return !supermarket || supermarket === '' || supermarket === '-' ?
        FetchPurchase(`purchases/${id}/items`, "GET") :
        FetchPurchase(`purchases/${id}/items?supermarket=${supermarket}`, "GET");
}

export async function postNewPurchaseOrder() {
    return FetchPurchase(`purchases/new`, "POST", {});
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