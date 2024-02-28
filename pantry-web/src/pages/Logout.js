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
        <h6 className="mt-3 title"><br /> You have been logged out, or your session is not valid anymore. <br /> <br /> Please <b>log in</b> to continue...</h6>
    )
}