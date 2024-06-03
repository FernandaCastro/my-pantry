import { ProfileContext } from '../services/context/AppContext.js';
import { useEffect, useContext } from 'react';
import { Link } from 'react-router-dom';
import { useTranslation } from 'react-i18next';

export default function Logout() {

    const { t } = useTranslation(['login']);

    const { profileCtx, setProfileCtx } = useContext(ProfileContext);


    useEffect(() => {
        setProfileCtx({});
    }, []);

    return (
        <h6 className="mt-3 title">
            <br /> {t("logout-text")}<br /> 
            <br /> <Link to={`/login`} >{t("login-to-continue")}</Link>
        </h6>
    )
}