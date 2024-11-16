import { useNavigate } from 'react-router-dom'
import { history } from '../util/history.js'
import { useCallback, useEffect } from 'react';

const NavigateSetter = () => {

    const navigate = useCallback(useNavigate(), []); // Memoize navigate with useCallback

    useEffect(() => {
        history.navigate = navigate;
    }, [navigate]); // Update History.navigate only if navigate changes

    return null
};

export default NavigateSetter