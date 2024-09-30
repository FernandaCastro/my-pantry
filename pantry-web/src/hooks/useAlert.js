import { AlertContext } from '../context/AppContext.js';
import { useContext } from 'react';

function useAlert() {

    const { setAlert } = useContext(AlertContext);

    function showAlert(type, message) {
        if (type && type.length > 0 && message && message.length > 0) {
            setAlert({
                show: true,
                type: type,
                message: message
            })
        }
    }

    return { showAlert };
}

export default useAlert;