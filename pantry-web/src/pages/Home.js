import Pantries from './Pantries.js';
import Login from './Login.js';
import { ProfileContext } from '../services/context/AppContext';
import { useContext } from 'react';

export default function Home() {

    const { profileCtx } = useContext(ProfileContext);


    return (
        <>
            {profileCtx && Object.keys(profileCtx).length > 0 ? <Pantries /> : <Login />}
        </>
    )
}