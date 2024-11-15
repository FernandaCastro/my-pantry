import { PROFILE_KEY, useProfileState } from "./profile";

function useProfile() {

    const { data: profile, setData, resetData } = useProfileState();

    function setProfile(profileData) {

        localStorage.setItem(PROFILE_KEY, JSON.stringify(profileData));
        setData(profileData);
    }

    function resetProfile() {
        const cleanProfile = { theme: profile.theme }
        localStorage.setItem(PROFILE_KEY, JSON.stringify(cleanProfile));
        setData(cleanProfile);

    }

    return { profile, setProfile, resetProfile }

}

export default useProfile;

