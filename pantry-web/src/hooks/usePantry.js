import { getPantry, getAnalysePantry, createPantryItem } from "../api/mypantry/pantry/pantryService";
import { useMutationWithCallbacks, useQueryWithCallbacks } from "./useQueryWithCallbacks";

const PANTRY_KEY = 'pantry';
const ANALYSE_PANTRY_KEY = 'analysePantry';
const PANTRY_ITEMS_KEY = 'pantryItems'

export function useGetPantry({ id }, callbacks = {}) {
    
    return useQueryWithCallbacks(
        [PANTRY_KEY, id],
        ({ signal }) => getPantry(id, signal),
        { enabled: !!id },
        callbacks
    );
}

export function useAnalysePantry({ id, runQuery }, callbacks = {}) {

    return useQueryWithCallbacks(
        [ANALYSE_PANTRY_KEY, id],
        ({ signal }) => getAnalysePantry(id, signal),
        { enabled: !!id && runQuery },
        callbacks
    );
}

export function useCreatePantryItem(callbacks = {}) {

    return useMutationWithCallbacks(
        ({ pantryId, newItem }) => createPantryItem(pantryId, newItem),  
        { invalidateQueriesKey: PANTRY_ITEMS_KEY },  
        callbacks
    );
}