import { useQuery, useQueryClient } from '@tanstack/react-query';

export function createGlobalState(
    queryKey,
    initialData = null,
    invalidate = false
) {

    return function () {
        const queryClient = useQueryClient();

        var resolvedInitialData = initialData;

        if (typeof initialData === 'function') {
            resolvedInitialData = initialData()
        }

        const { data } = useQuery({
            queryKey: [queryKey],
            queryFn: () => Promise.resolve(resolvedInitialData),
            refetchOnMount: false,
            refetchOnWindowFocus: false,
            refetchOnReconnect: false,
            refetchInterval: false,
            refetchIntervalInBackground: false,
            initialData: resolvedInitialData
        });

        function setData(data) {
            queryClient.setQueryData([queryKey], data);

        }

        function resetData() {

            queryClient.refetchQueries({
                queryKey: [queryKey],
                type: 'all',
            });

            queryClient.invalidateQueries({
                queryKey: [queryKey],
                refetchType: 'all',
            });
        }

        return { data, setData, resetData };
    };
}