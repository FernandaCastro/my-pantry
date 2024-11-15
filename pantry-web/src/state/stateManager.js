import { useQuery, useQueryClient } from '@tanstack/react-query';

export function createGlobalState(
    queryKey,
    initialData = null
) {

    return function () {
        const queryClient = useQueryClient();

        var resolvedInitialData = initialData;

        if (typeof initialData === 'function') {
            resolvedInitialData = initialData()
        }

        const { data, status, fetchStatus, refetch } = useQuery({
            queryKey: [queryKey],
            queryFn: () => Promise.resolve(resolvedInitialData),
            refetchOnMount: false,
            refetchOnWindowFocus: false,
            refetchOnReconnect: false,
            refetchInterval: false,
            refetchIntervalInBackground: false,
        });

        function setData(data) {
            queryClient.setQueryData([queryKey], data);

        }

        function resetData() {

            queryClient.refetchQueries({
                queryKey: [queryKey],
            });

            queryClient.invalidateQueries({
                queryKey: [queryKey],
            });
        }

        return { data, setData, resetData };
    };
}