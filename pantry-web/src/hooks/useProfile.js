import { useMemo, useState } from 'react';
import { useCachedState, useQueryWithCallbacks } from './useCustomQuery';

const PROFILE_KEY = "profile";
const DEFAULT_THEME = 'theme-mono-light';

//const THEME_KEY = "theme";

function useProfile() {

    const { data: profile, isLoading, refetch: refetchProfile } = useQueryWithCallbacks(
        [PROFILE_KEY],
        () => getLocalStorageProfile(),
        {
            //enabled: refreshCacheProfile,
            //staleTime: Infinity,
            gcTime: Infinity,
        }
        , getLocalStorageProfile()
    );

    function getLocalStorageProfile() {

        const localData = localStorage.getItem(PROFILE_KEY);
        return !localData || localData === 'undefined' || Object.keys(localData).length === 0 ? { theme: DEFAULT_THEME } : JSON.parse(localData);
    }

    function setProfile(profileData) {

        localStorage.setItem(PROFILE_KEY, JSON.stringify(profileData));
        refetchProfile();
    }

    // Memoize to prevent unnecessary re-renders
    // const profile = useMemo(() => rawProfile, [rawProfile?.email]);
    // const theme = useMemo(() => rawTheme, [rawTheme]);

    return { DEFAULT_THEME, profile, isLoading, setProfile }

}

export default useProfile;

