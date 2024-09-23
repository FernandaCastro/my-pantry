// ============================================================
// API Docs: http://localhost:8080/pantries
// ============================================================
import {FetchPantry, PostPantry, PutPantry, DeletePantry} from '../FetchPantry';

export async function getPantryList() {
    return FetchPantry(`pantries/all`);
}

export async function getPantryListWithPermission(permission) {
    return FetchPantry(`pantries/all-with-permission?permission=${permission}`);
}

export async function getPantry(pantryId) {
    return FetchPantry(`pantries/${pantryId}`);
}

export async function getPantryItems(pantryId) {
    return FetchPantry(`pantries/${pantryId}/items`)
}

export async function getPantryItemsConsume(pantryIds, signal) {
    return FetchPantry(`pantries/items/consume?pantryIds=${pantryIds}`, signal)
}

export async function updatePantry(pantryId, body) {
    return PutPantry(`pantries/${pantryId}`, body);
}
export async function createPantry(body) {
    return PostPantry(`pantries`, body);
}
export async function deletePantry(pantryId) {
    return DeletePantry(`pantries/${pantryId}`);
}

export async function getPantryRebalance(pantryId) {
    return FetchPantry(`pantries/${pantryId}/items/balancing`);
}

export async function postPantryConsume(pantryId, body) {
    return PostPantry(`pantries/${pantryId}/items/consume`, body);
}

export async function postPantryConsumeItem(body) {
    return PostPantry(`pantries/items/consume`, body);
}

export async function createPantryItem(pantryId, body) {
    return PostPantry(`pantries/${pantryId}/items`, body);
}
export async function updatePantryItem(pantryId, productId, body) {
    return PutPantry(`pantries/${pantryId}/items/${productId}`, body);
}
export async function deletePantryItem(pantryId, productId) {
    return DeletePantry(`pantries/${pantryId}/items/${productId}`);
}

export async function getFilteredProductList(groupId, text) {
    // const newText = text.replace(/%/g, "%25");
    // console.log(newText);
    return FetchPantry(`products?groupId=${groupId}&searchParam=${text}`);
}

export async function getProductList() {
    return FetchPantry(`products`);
}

export async function createProduct(body) {
    return PostPantry(`products`, body);
}
export async function updateProduct(productId, body) {
    return PutPantry(`products/${productId}`, body);
}
export async function deleteProduct(productId) {
    return DeletePantry(`products/${productId}`);
}

export async function getAssociatedPantries(groupId) {
    return FetchPantry(`pantries?groupId=${groupId}`);
}

export async function createPantryWizard(pantryWizardDto) {
    return PostPantry('pantries/wizard', pantryWizardDto);
}

export async function getPantryChartData() {
    return FetchPantry(`pantries/charts-data`);
}



