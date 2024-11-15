import { createGlobalState } from "./stateManager";

const GLOBAL_LOADING_KEY = "globalLoading";
const INITIAL_STATE = false;

export const useLoadingState = createGlobalState(GLOBAL_LOADING_KEY, INITIAL_STATE);