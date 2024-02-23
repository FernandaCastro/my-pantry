import { PantryContext, ProfileContext } from '../services/context/AppContext.js';
import { useEffect, useContext } from 'react';

export default function LogoutFromGoogle() {

    const { pantryCtx, setPantryCtx } = useContext(PantryContext);
    const { profileCtx, setProfileCtx } = useContext(ProfileContext);


    useEffect(() => {
        setPantryCtx({});
        setProfileCtx({});
    }, []);

    return (
        <h6 className="mt-3 title">You have been logged out, your session is not valid anymore. <br /> Please log in again to continue...</h6>
    )
}