import { Routes, useNavigate } from "react-router-dom";
import CustomRoutes from "../routes/CustomRoutes.js";
import Header from '../components/Header.js';
import React, { useState, useEffect } from 'react';
import Container from 'react-bootstrap/Container';
import Alert from 'react-bootstrap/Alert';
import '../assets/styles/App.scss';
import VariantType from '../components/VariantType.js';
import Image from 'react-bootstrap/Image';
import Stack from 'react-bootstrap/Stack';
//import { getUserProfile } from '../services/apis/google/GoogleRequests.js';
//import { googleLogout, useGoogleLogin, GoogleLogin, GoogleOAuthProvider } from '@react-oauth/google';
//import { getUserInfo, postLogin, postLogout } from '../services/apis/mypantry/requests/AccountRequests.js';
import GoogleLogin from "../components/GoogleLogin.js";
import { initLogin, postLoginToken, logout } from '../services/LoginService.js'

import { PantryContext, AlertContext, ProfileContext } from '../services/context/AppContext.js';

export default function App() {

  const [profileCtx, setProfileCtx] = useState(() => {
    const data = localStorage.getItem("profile-context");
    return JSON.parse(data)
  });

  const [pantryCtx, setPantryCtx] = useState(() => {
    const data = localStorage.getItem("pantry-context");
    return JSON.parse(data) ||
    {
      id: 0,
      name: "",
      type: "",
      isActive: false
    }
  });

  const [alert, setAlert] = useState({
    show: false,
    message: "",
    type: ""
  });

  useEffect(() => {
    if (!profileCtx || Object.keys(profileCtx).length === 0) {
      var profile = initLogin();
      setProfileCtx(profile);
    }
  }, []);

  React.useEffect(() => {
    localStorage.setItem("pantry-context", JSON.stringify(pantryCtx));
  }, [pantryCtx]);

  React.useEffect(() => {
    localStorage.setItem("profile-context", JSON.stringify(profileCtx));
  }, [profileCtx]);

  // async function initLogin() {
  //   try {
  //     const res = await getUserInfo();
  //     setProfileCtx(res);
  //   } catch (error) {
  //     console.log(`initLogin Failed: ${error}`);
  //   }
  // };

  // const handleLogin = useGoogleLogin({
  //   onSuccess: (credential) => {
  //     postLoginToken(credential);
  //   },

  //   onError: (error) => {
  //     setAlert({
  //       show: true,
  //       type: VariantType.DANGER,
  //       message: error.message
  //     })
  //     console.log(`Login Failed: ${error}`);
  //   }
  // });

  // const onGoogleSignIn = async res => {
  //   const { credential } = res;
  //   console.log(credential);
  //   var profile = postLoginToken(credential);
  //   setProfileCtx(profile);
  // };

  // async function postLoginToken(credential) {
  //   try {
  //     const res = await postLogin(credential);
  //     setProfileCtx(res);
  //   } catch (error) {
  //     setAlert({
  //       show: true,
  //       type: VariantType.DANGER,
  //       message: error.message
  //     })
  //     console.log(`Login Failed: ${error}`);
  //   }
  // }

  // const handleLogout = async () => {
  //   await logout();
  //   setProfileCtx(null);
  // };

  return (
    <ProfileContext.Provider value={{ profileCtx, setProfileCtx }}>
      <PantryContext.Provider value={{ pantryCtx, setPantryCtx }}>
        <AlertContext.Provider value={{ alert, setAlert }}>
          <Header />
          <Alert variant={alert.type} show={alert.show} onClose={() => setAlert((a) => a = { ...a, show: !alert.show })} dismissible >{alert.message}</Alert>
          <Container>
            {profileCtx ? (
              <CustomRoutes />
            ) : (
              <h6 className="title">Please Log in to continue</h6>
            )}
          </Container>
        </AlertContext.Provider>
      </PantryContext.Provider >
    </ProfileContext.Provider>
  )
}

//<button className='btn-custom' onClick={login}>Sign in</button>

// const login = useGoogleLogin({
//   onSuccess: (response) => {
//     setUserInfo(response);
//   },

//   onError: (error) => {
//     setAlert({
//       show: true,
//       type: VariantType.DANGER,
//       message: error.message
//     })
//     console.log(`Login Failed: ${error}`);
//   }
// });

// const logout = () => {
//   googleLogout();

//   cleanStorage();
//   console.log('Logged out!');

//   // When Logout: Delete Cookie, Profile and UserData from LocalStorage/Cookie
// };

// function cleanStorage() {
//   setUserInfo(null);
//   setProfileInfo(null);
//   localStorage.removeItem("profile-context");
//   //cookies.remove('access-token');
// }

// React.useEffect(() => {
//   // Cookie for access-token exists, then:
//   var accessToken = cookies.get('access-token');
//   if (accessToken) {
//     //get ExternalProfile and save (localStorage?)
//     fetchUserProfile(accessToken);

//     //get InternalUser + UserGroup and save in the context

//   } else {
//     // access-token not in the cookie, then enable Login button

//   }
// }, []);

// React.useEffect(() => {
//   if (userInfo && Object.keys(userInfo).length > 0) {
//     fetchUserProfile();
//   }
// }, [userInfo]);

// async function fetchUserProfile(accessToken) {
//   try {
//     const res = await getUserProfile(accessToken);
//     if (res) {
//       setProfileInfo(res);
//       setProfileCtx(res);
//       localStorage.setItem("profile-context", JSON.stringify(res));
//     }
//   } catch (error) {
//     console.log(error);
//     if (error.status === '401') { //Unauthorized
//       cleanStorage();
//     }
//   }
// }