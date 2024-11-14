import { createGlobalState } from "./globalStateManager";

const GLOBAL_LOADING_KEY = "globalLoading";
const INITIAL_STATE = false;

export const useGlobalLoadingState = createGlobalState(GLOBAL_LOADING_KEY, INITIAL_STATE);