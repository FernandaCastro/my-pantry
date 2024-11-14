import { createGlobalState } from "./globalStateManager";

const ALERT_KEY = "alert";
const INITIAL_STATE = {
    show: false,
    type: "",
    message: ""
};


export const useAlertState = createGlobalState(ALERT_KEY, INITIAL_STATE);