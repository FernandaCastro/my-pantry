// ============================================================
// API Docs: http://localhost:8080/pantries
// ============================================================
import FetchPantry from '../FetchPantry';

export async function getPantryList() {
    return FetchPantry(`pantries/all`, "GET");
}

export async function getPantryListWithPermission(permission) {
    return FetchPantry(`pantries/all-with-permission?permission=${permission}`, "GET");
}

export async function getPantry(pantryId) {
    return FetchPantry(`pantries/${pantryId}`, "GET");
}

export async function getPantryItems(pantryId) {
    return FetchPantry(`pantries/${pantryId}/items`, "GET")
}

export async function getPantryItemsConsume(pantryIds) {
    return FetchPantry(`pantries/items/consume?pantryIds=${pantryIds}`, "GET")
}

export async function updatePantry(pantryId, body) {
    return FetchPantry(`pantries/${pantryId}`, "PUT", body);
}
export async function createPantry(body) {
    return FetchPantry(`pantries`, "POST", body);
}
export async function deletePantry(pantryId) {
    return FetchPantry(`pantries/${pantryId}`, "DELETE");
}

export async function getPantryRebalance(pantryId) {
    return FetchPantry(`pantries/${pantryId}/items/balancing`, "GET");
}

export async function postPantryConsume(pantryId, body) {
    return FetchPantry(`pantries/${pantryId}/items/consume`, "POST", body);
}

export async function postPantryConsumeItem(body) {
    return FetchPantry(`pantries/items/consume`, "POST", body);
}

export async function createPantryItem(pantryId, body) {
    return FetchPantry(`pantries/${pantryId}/items`, "POST", body);
}
export async function updatePantryItem(pantryId, productId, body) {
    return FetchPantry(`pantries/${pantryId}/items/${productId}`, "PUT", body);
}
export async function deletePantryItem(pantryId, productId) {
    return FetchPantry(`pantries/${pantryId}/items/${productId}`, "DELETE");
}

export async function getFilteredProductList(groupId, text) {
    // const newText = text.replace(/%/g, "%25");
    // console.log(newText);
    return FetchPantry(`products?groupId=${groupId}&searchParam=${text}`, "GET");
}

export async function getProductList() {
    return FetchPantry(`products`, "GET");
}

export async function createProduct(body) {
    return FetchPantry(`products`, "POST", body);
}
export async function updateProduct(productId, body) {
    return FetchPantry(`products/${productId}`, "PUT", body);
}
export async function deleteProduct(productId) {
    return FetchPantry(`products/${productId}`, "DELETE");
}

export async function getAssociatedPantries(groupId) {
    return FetchPantry(`pantries?groupId=${groupId}`, "GET");
}

export async function createPantryWizard(pantryWizardDto) {
    return FetchPantry('pantries/wizard', "POST", pantryWizardDto);
}


