import { postLoginToken } from '../services/LoginService';
import { ProfileContext } from '../services/context/AppContext';
import { Button } from 'react-bootstrap';
import { useContext, useRef, useEffect } from 'react';
import useScript from '../hooks/useScript';
//import { GoogleLogin, useGoogleLogin } from '@react-oauth/google';

export function LoginWithGoogle({ setShowLogin }) {

    const { profileCtx, setProfileCtx } = useContext(ProfileContext);

    const GoogleSignInButton = useRef(null);

    useEffect(() => {
        window.google = undefined;
    }, []);

    // https://github.com/anthonyjgrove/react-google-login/issues/502
    // https://developers.google.com/identity/gsi/web/reference/js-reference#CredentialResponse
    const onGoogleSignIn = async res => {
        const { credential } = res;
        var profile = await postLoginToken(credential);
        setProfileCtx(profile);
        setShowLogin(false);
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

export function LogoutFromGoogle({ onClick }) {

    useEffect(() => {
        window.google = undefined;
    }, []);

    useScript('https://accounts.google.com/gsi/client', () => {
        //https://developers.google.com/identity/gsi/web/reference/js-reference#google.accounts.id.disableAutoSelect
        window.google.accounts.id.disableAutoSelect();
    });

    return <Button bsPrefix='btn-custom' onClick={onClick}>Logout</Button>
}
