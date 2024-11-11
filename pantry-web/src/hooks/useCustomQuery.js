import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { useEffect, useMemo } from "react";

/**
 * Utility function to create useQuery hooks with success and error callbacks.
 * @param {string | array} queryKey - query unique key (or an array of keys)
 * @param {function} queryFn - function that retrieves data (it should retrieve a Promisse)
 * @param {object} options - aditional options of useQuery like `enabled`, `refetchInterval`, etc.
 * @param {object} callbacks - Object containing optional `onSuccessCallback` and `onErrorCallback` callbacks.
 */
export function useQueryWithCallbacks(queryKey, queryFn, options = {}, callbacks = {}, initialData) {

    const { onSuccess, onError } = callbacks;  // Destructure the callbacks object

    const { data, isLoading, isFetching, isSuccess, isError, error, refetch } = useQuery({
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

    const memoData = useMemo(() => data, [data]);

    return { data: memoData, isLoading: isLoading, isSuccess: isSuccess, isError: isError, error: error, refetch: refetch };
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

export function useCachedState(key, initialValue = null) {

    const queryClient = useQueryClient();
    const initialDataValue = initialValue == null ? queryClient.getQueryData([key]) : initialValue;

    const { data: cachedState } = useQuery({
        queryKey: [key],
        queryF: () => queryClient.getQueryData([key]),
        initialData: initialDataValue,
        enabled: false
    });

    function setCachedState(key, newData) {

        queryClient.setQueryData([key], () => (
            newData
        ));

        // queryClient.setQueryData([key], (oldData) => ({
        //     ...oldData,
        //     ...newData
        // }));
    }

    return { cachedState, setCachedState }
}