import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { useEffect } from "react";

/**
 * Utility function to create useQuery hooks with success and error callbacks.
 * @param {string | array} queryKey - query unique key (or an array of keys)
 * @param {function} queryFn - function that retrieves data (it should retrieve a Promisse)
 * @param {object} options - aditional options of useQuery like `enabled`, `refetchInterval`, etc.
 * @param {object} callbacks - Object containing optional `onSuccessCallback` and `onErrorCallback` callbacks.
 */
export function useQueryWithCallbacks(queryKey, queryFn, options = {}, callbacks = {}, initialData) {

    const { onSuccess, onError } = callbacks;

    const { data, isLoading, isFetching, isSuccess, isError, error, refetch, status } = useQuery({
        queryKey,
        queryFn,
        initialData,
        ...options,
    });

    useEffect(() => {
        if (!isFetching && data) {
            if (onSuccess) onSuccess(data);
        }
    }, [isFetching, data])

    useEffect(() => {
        if (!isFetching && error) {
            if (onError) onError(error);
        }
    }, [isFetching, error])

    return { data, isLoading, isSuccess, isError, error, refetch, status };
}

/**
 * Utility function to create mutation hooks with success and error callbacks.
 * @param {function} mutationFn - The mutation function that performs the operation.
 * @param {object} options - Options for the mutation (like `onSuccess`, `onError`, etc.).
 * @param {object} callbacks - Object containing optional `onSuccessCallback` and `onErrorCallback` callbacks.
 */
export function useMutationWithCallbacks(mutationFn, options = {}, callbacks = {}) {

    const { onSuccess, onError } = callbacks;
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn,
        ...options,
        onSuccess: (data) => {
            if (options.invalidateQueriesKey) {
                queryClient.invalidateQueries(options.invalidateQueriesKey);
            }
            if (onSuccess) onSuccess(data);
        },
        onError: (error) => {
            if (onError) onError(error);
        }
    });
}