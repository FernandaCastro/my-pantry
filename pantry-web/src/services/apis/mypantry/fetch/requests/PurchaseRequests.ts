// ============================================================
// API Docs: http://localhost:8081/purchases
// ============================================================
import FetchPurchase from '../FetchPurchase';

export async function getOpenPurchaseOrder() {
    return FetchPurchase(`purchases/open`, "GET");
}

export async function getPendingPurchaseItems() {
    return FetchPurchase(`purchases/items`, "GET")
}

export async function postNewPurchaseOrder() {
    return FetchPurchase(`purchases/new`, "POST", {});
}

export async function postClosePurchaseOrder(purchasedItems) {
    return FetchPurchase(`purchases/close`, "POST", purchasedItems);
}