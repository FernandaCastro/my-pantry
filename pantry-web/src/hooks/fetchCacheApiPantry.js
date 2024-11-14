import { useQueryClient } from "@tanstack/react-query";
import { createPantryItem, fetchPantry, fetchAnalysePantry, fetchPantryChartData } from "../api/mypantry/pantry/pantryService";
import { useMutationWithCallbacks, useQueryWithCallbacks } from "./fetchCacheApi";

const PANTRY_KEY = 'pantry';
const ANALYSE_PANTRY_KEY = 'analysePantry';
const PANTRY_ITEMS_KEY = 'pantryItems'
const PANTRY_CHARTS_KEY = 'pantryCharts'

export function useGetPantry({ id }, callbacks = {}) {
    return useQueryWithCallbacks(
        [PANTRY_KEY, id],
        ({ signal }) => fetchPantry(id, signal),
        {
            enabled: !!id,
            staleTime: 1000 * 60 * 3
        },
        callbacks
    );
}

export function useAnalysePantry({ id, runQuery }, callbacks = {}) {

    return useQueryWithCallbacks(
        [ANALYSE_PANTRY_KEY, id],
        ({ signal }) => fetchAnalysePantry(id, signal),
        { enabled: !!id && runQuery },
        callbacks
    );
}

export function useCreateNewPantryItem(callbacks = {}) {

    return useMutationWithCallbacks(
        ({ pantryId, newItem }) => createPantryItem(pantryId, newItem),
        { invalidateQueriesKey: PANTRY_ITEMS_KEY },
        callbacks
    );
}

export function useGetPantryCharts({ email }, callbacks = {}) {

    return useQueryWithCallbacks(
        [PANTRY_CHARTS_KEY, email],
        ({ signal }) => fetchPantryChartData(signal),
        {
            enabled: !!email,
            staleTime: 1000 * 60 * 3, // Keeps data fresh for 3 minutes
            //gcTime: 1000 * 60 * 30, // Keeps data cached for 30 minutes
            gcTime: Infinity
        },
        callbacks
    );
}