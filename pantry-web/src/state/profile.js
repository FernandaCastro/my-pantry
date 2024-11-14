import { createGlobalState } from "./globalStateManager";

export const PROFILE_KEY = "profile";
export const DEFAULT_THEME = 'theme-mono-light';

const INITIAL_STATE = { theme: DEFAULT_THEME }

function readLocalStorage() {

    const localData = localStorage.getItem(PROFILE_KEY);

    if (localData === undefined || localData === "{}" || Object.keys(localData).length === 0) {
        localStorage.setItem(PROFILE_KEY, JSON.stringify(INITIAL_STATE));
        return INITIAL_STATE;
    }

    return JSON.parse(localData);
}

export const useProfileState = createGlobalState(PROFILE_KEY, readLocalStorage());