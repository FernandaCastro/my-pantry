// ============================================================
// API Docs: http://localhost:8080/pantries
// ============================================================
import { Get, Post, Put, Delete } from './pantryApiClient';

export async function getPantryList() {
    return Get(`pantries/all`);
}
export async function getPantryListWithPermission(permission) {
    return Get(`pantries/all-with-permission?permission=${permission}`);
}
export async function getPantry(pantryId, signal) {
    return Get(`pantries/${pantryId}`, signal);
}
export async function getPantryItems(pantryId, signal) {
    return Get(`pantries/${pantryId}/items`, signal)
}
export async function getPantryItemsConsume(pantryIds, signal) {
    return Get(`pantries/items/consume?pantryIds=${pantryIds}`, signal)
}
export async function getAnalysePantry(pantryId, signal) {
    return Get(`pantries/${pantryId}/items/balancing`, signal);
}
export async function getFilteredProductList(groupId, text) {
    // const newText = text.replace(/%/g, "%25");
    // console.log(newText);
    return Get(`products?groupId=${groupId}&searchParam=${text}`);
}
export async function getProductList() {
    return Get(`products`);
}
export async function getAssociatedPantries(groupId) {
    return Get(`pantries?groupId=${groupId}`);
}
export async function getPantryChartData() {
    return Get(`pantries/charts-data`);
}


export async function createPantry(body) {
    return Post(`pantries`, body);
}
export async function postPantryConsume(pantryId, body) {
    return Post(`pantries/${pantryId}/items/consume`, body);
}
export async function postPantryConsumeItem(body) {
    return Post(`pantries/items/consume`, body);
}
export async function createPantryItem(pantryId, body) {
    return Post(`pantries/${pantryId}/items`, body);
}
export async function createProduct(body) {
    return Post(`products`, body);
}
export async function createPantryWizard(pantryWizardDto) {
    return Post('pantries/wizard', pantryWizardDto);
}


export async function updatePantry(pantryId, body) {
    return Put(`pantries/${pantryId}`, body);
}
export async function updatePantryItem(pantryId, productId, body) {
    return Put(`pantries/${pantryId}/items/${productId}`, body);
}
export async function updateProduct(productId, body) {
    return Put(`products/${productId}`, body);
}


export async function deletePantry(pantryId) {
    return Delete(`pantries/${pantryId}`);
}
export async function deletePantryItem(pantryId, productId) {
    return Delete(`pantries/${pantryId}/items/${productId}`);
}
export async function deleteProduct(productId) {
    return Delete(`products/${productId}`);
}