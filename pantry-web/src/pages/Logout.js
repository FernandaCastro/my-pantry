import { PantryContext, ProfileContext } from '../services/context/AppContext.js';
import { useEffect, useContext } from 'react';
import { Link } from 'react-router-dom';

export default function Logout() {

    const { pantryCtx, setPantryCtx } = useContext(PantryContext);
    const { profileCtx, setProfileCtx } = useContext(ProfileContext);


    useEffect(() => {
        setPantryCtx({});
        setProfileCtx({});
    }, []);

    return (
        <h6 className="mt-3 title">
            <br /> You have been logged out, or your session is not valid anymore. <br /> 
            <br /> Please <Link to={`/login`} >log in</Link>  to continue...
        </h6>
    )
}