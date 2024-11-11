import { useQueryClient } from "@tanstack/react-query";
import { getPantry, getAnalysePantry, createPantryItem, getPantryChartData } from "../api/mypantry/pantry/pantryService";
import { useMutationWithCallbacks, useQueryWithCallbacks } from "./useCustomQuery";

const PANTRY_KEY = 'pantry';
const ANALYSE_PANTRY_KEY = 'analysePantry';
const PANTRY_ITEMS_KEY = 'pantryItems'
const PANTRY_CHARTS_KEY = 'pantryCharts'

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

export function useGetPantryCharts({ email}, callbacks = {}) {

    return useQueryWithCallbacks(
        [PANTRY_CHARTS_KEY, email],
        ({ signal }) => getPantryChartData(signal),
        {
            enabled: !!email,
            staleTime: 1000 * 60 * 5, // Keeps data fresh for 5 minutes
            gcTime: 1000 * 60 * 30, // Keeps data cached for 30 minutes
        },
        callbacks
    );

}