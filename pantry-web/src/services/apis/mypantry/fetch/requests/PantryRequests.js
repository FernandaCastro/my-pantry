// ============================================================
// API Docs: http://localhost:8080/pantries
// ============================================================
import FetchPantry from '../FetchPantry';

export async function getPantryList() {
    return FetchPantry(`pantries`, "GET");
}

export async function getPantry(id) {
    return FetchPantry(`pantries/${id}`, "GET");
}

export async function getPantryItems(id) {
    return FetchPantry(`pantries/${id}/items`, "GET")
}

export async function updatePantry(id, pantry) {
    return FetchPantry(`pantries/${id}`, "PUT", pantry);
}
export async function createPantry(pantry) {
    return FetchPantry(`pantries`, "POST", pantry);
}
export async function deletePantry(id) {
    return FetchPantry(`pantries/${id}`, "DELETE");
}

export async function postPantryConsume(id, consumedItems) {
    return FetchPantry(`pantries/${id}/consume`, "POST", consumedItems);
}

export async function createPantryItem(id, pantryItem) {
    return FetchPantry(`pantries/${id}/items`, "POST", pantryItem);
}
export async function updatePantryItem(id, productId, pantryItem) {
    return FetchPantry(`pantries/${id}/items/${productId}`, "PUT", pantryItem);
}
export async function deletePantryItem(id, productId) {
    return FetchPantry(`pantries/${id}/items/${productId}`, "DELETE");
}

export async function getProductList(type, text) {
    // const newText = text.replace(/%/g, "%25");
    // console.log(newText);
    return FetchPantry(`products?${type}=${text}`, "GET");
}

