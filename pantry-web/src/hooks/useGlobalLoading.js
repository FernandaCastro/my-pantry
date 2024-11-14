import { useGlobalLoadingState } from '../state/loading';

export function useGlobalLoading() {

    const { data: isLoading, setData } = useGlobalLoadingState()

    function setIsLoading(value) {

        setData(value);
    }

    return { isLoading, setIsLoading };
}

export default useGlobalLoading;






