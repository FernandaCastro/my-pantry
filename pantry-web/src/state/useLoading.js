import { useLoadingState } from './loading';

export function useGlobalLoading() {

    const { data: isLoading, setData } = useLoadingState();

    function setIsLoading(value) {

        setData(value);
    }

    return { isLoading, setIsLoading };
}

export default useGlobalLoading;






