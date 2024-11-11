import { useMemo } from 'react';
import { useCachedState } from './useCustomQuery';

export function useGlobalLoading() {

    const GLOBAL_LOADING_KEY = "globalLoading";

    const { cachedState: isLoading, setCachedState } = useCachedState(GLOBAL_LOADING_KEY, false);

    function setIsLoading(value) {

        setCachedState(GLOBAL_LOADING_KEY, value);
    }

    // Memoize to prevent unnecessary re-renders
    // const isLoading = useMemo(() => cachedState, cachedState);

    return { isLoading, setIsLoading };
}

export default useGlobalLoading;






