import { useQuery, useQueryClient } from '@tanstack/react-query';

export function createGlobalState(
    queryKey,
    initialData = null,
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
            refetchInterval: false,
            refetchOnMount: false,
            refetchOnWindowFocus: false,
            refetchOnReconnect: false,
            refetchIntervalInBackground: false,
        });

        function setData(data) {
            queryClient.setQueryData([queryKey], data);
        }

        function resetData() {
            queryClient.invalidateQueries({
                queryKey: [queryKey],
            });
            queryClient.refetchQueries({
                queryKey: [queryKey],
            });
        }

        return { data, setData, resetData };
    };
}