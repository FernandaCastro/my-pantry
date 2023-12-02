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

