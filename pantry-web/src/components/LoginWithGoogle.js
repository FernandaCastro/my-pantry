import { postLoginToken } from '../services/LoginService';
import { Button } from 'react-bootstrap';
import { useRef, useEffect, useContext } from 'react';
import useScript from '../hooks/useScript';
import { AlertContext } from '../services/context/AppContext.js';
import VariantType from '../components/VariantType.js';
//import { GoogleLogin, useGoogleLogin } from '@react-oauth/google';

export function LoginWithGoogle({ handlePostLogin }) {

    const GoogleSignInButton = useRef(null);
    const { setAlert } = useContext(AlertContext);

    useEffect(() => {
        window.google = undefined;
    }, []);

    // https://github.com/anthonyjgrove/react-google-login/issues/502
    // https://developers.google.com/identity/gsi/web/reference/js-reference#CredentialResponse
    const onGoogleSignIn = async res => {
        const { credential } = res;
        var profile;

        try {
            profile = await postLoginToken(credential);

        } catch (error) {
            setAlert({
                show: true,
                type: VariantType.DANGER,
                message: error
            })
        }
        handlePostLogin(profile);
    };

    useScript('https://accounts.google.com/gsi/client', () => {
        // https://developers.google.com/identity/gsi/web/reference/js-reference#google.accounts.id.initialize
        window.google.accounts.id.initialize({
            client_id: process.env.REACT_APP_GOOGLE_CLIENT_ID,
            callback: onGoogleSignIn,
        });
        // https://developers.google.com/identity/gsi/web/reference/js-reference#google.accounts.id.renderButton
        window.google.accounts.id.renderButton(
            GoogleSignInButton.current,
            { theme: 'outlined', size: 'large', text: 'signin' }, // customization attributes
        );
    });

    return <div ref={GoogleSignInButton}></div>;
}

export function LogoutFromGoogle({ handleLogout }) {

    useEffect(() => {
        window.google = undefined;
    }, []);

    useScript('https://accounts.google.com/gsi/client', () => {
        //https://developers.google.com/identity/gsi/web/reference/js-reference#google.accounts.id.disableAutoSelect
        window.google.accounts.id.disableAutoSelect();
    });

    return <Button bsPrefix='btn-custom' onClick={handleLogout}>Logout</Button>
}
