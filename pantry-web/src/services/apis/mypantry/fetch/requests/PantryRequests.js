// ============================================================
// API Docs: http://localhost:8080/pantries
// ============================================================
import FetchPantry from '../FetchPantry';

export async function getPantryList() {
    return FetchPantry(`pantries`, "GET");
}

export async function getPantry(pantryId) {
    return FetchPantry(`pantries/${pantryId}`, "GET");
}

export async function getPantryItems(pantryId) {
    return FetchPantry(`pantries/${pantryId}/items`, "GET")
}

export async function postPantryConsume(pantryId, consumedItems) {
    return FetchPantry(`pantries/${pantryId}/consume`, "POST", consumedItems);
}

