import { createContext, useEffect, useState } from "react";

export const CookieContext = createContext();
const COOKIES_ALLOWED = "cookies-allowed";

export function CookiesProvider({ children }) {

  const [cookieCtx, setCookieCtx] = useState(() => {

    const data = localStorage.getItem(COOKIES_ALLOWED);
    return !data || data === 'undefined' || Object.keys(data).length === 0 ? false : JSON.parse(data);

  });

  useEffect(() => {
    localStorage.setItem(COOKIES_ALLOWED, JSON.stringify(cookieCtx));
  }, [cookieCtx]);

  return (
    <CookieContext.Provider value={{ cookieCtx, setCookieCtx }}>
      {children}
    </CookieContext.Provider>
  );
}