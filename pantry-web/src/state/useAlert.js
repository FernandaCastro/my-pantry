import { useAlertState } from './alert';

function useAlert() {

    const { data: alert, setData, resetData } = useAlertState();

    function showAlert(type, message) {
        if (type && type.length > 0 && message && message.length > 0) {
            const newAlert = {
                show: true,
                type: type,
                message: message
            }

            setData(newAlert);
        }
    }

    function hideAlert() {

        resetData();
    }

    return { alert, showAlert, hideAlert };
}

export default useAlert;