import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { useEffect } from "react";

/**
 * Utility function to create useQuery hooks with success and error callbacks.
 * @param {string | array} queryKey - query unique key (or an array of keys)
 * @param {function} queryFn - function that retrieves data (it should retrieve a Promisse)
 * @param {object} options - aditional options of useQuery like `enabled`, `refetchInterval`, etc.
 * @param {object} callbacks - Object containing optional `onSuccessCallback` and `onErrorCallback` callbacks.
 */
export function useQueryWithCallbacks(queryKey, queryFn, options = {}, callbacks = {}) {

    const { onSuccess, onError } = callbacks;  // Destructure the callbacks object

    const { data, isLoading, isFetching, isSuccess, isError, error } = useQuery({
        queryKey,
        queryFn,
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

    return { data: data, isLoading: isLoading, isSuccess: isSuccess, isError: isError, error: error };
}

/**
 * Utility function to create mutation hooks with success and error callbacks.
 * @param {function} mutationFn - The mutation function that performs the operation.
 * @param {object} options - Options for the mutation (like `onSuccess`, `onError`, etc.).
 * @param {object} callbacks - Object containing optional `onSuccessCallback` and `onErrorCallback` callbacks.
 */
export function useMutationWithCallbacks(mutationFn, options = {}, callbacks = {}) {

    const { onSuccess, onError } = callbacks;  // Destructure the callbacks object
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn,  // Mutation function (e.g., API call)
        ...options,  // Additional options like refetching on success
        onSuccess: (data) => {
            if (options.invalidateQueriesKey) {
                queryClient.invalidateQueries(options.invalidateQueriesKey);
            }
            if (onSuccess) onSuccess(data);  // Call success callback if provided
        },
        onError: (error) => {
            if (onError) onError(error);  // Call error callback if provided
        }
    });
}