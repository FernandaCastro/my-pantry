import { useMemo } from 'react';
import { useCachedState } from './useCustomQuery';

function useAlert() {

    const ALERT_KEY = "alert";
    const ALERT_DISABLED = {
        show: false,
        type: "",
        message: ""
    }

    const { cachedState: alert, setCachedState } = useCachedState(ALERT_KEY, ALERT_DISABLED);

    function showAlert(type, message) {
        if (type && type.length > 0 && message && message.length > 0) {
            const newAlert = {
                show: true,
                type: type,
                message: message
            }

            setCachedState(ALERT_KEY, newAlert)
        }
    }

    function hideAlert() {

        setCachedState(ALERT_KEY, ALERT_DISABLED)
    }

    // Memoize to prevent unnecessary re-renders
    //const alert = useMemo(() => cachedState, cachedState);

    return { alert, showAlert, hideAlert };
}

export default useAlert;