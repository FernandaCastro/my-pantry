import { useMemo, useState } from "react";
import { LoadingContext } from "./AppContext";

export const LoadingProvider = ({ children }) => {

    const [isLoading, setIsLoading] = useState(false);

    // Memoize the context value to prevent unnecessary re-renders
    const value = useMemo(() => ({ isLoading, setIsLoading }), [isLoading]);

    return (

        <LoadingContext.Provider value={value}>
            {/* Reactive Listening to isLoading  */}
            {isLoading && <RippleLoadingAnimation />}
            {children}
        </LoadingContext.Provider>
    );
};

export const RippleLoadingAnimation = () => {
    return (
        <div className="loader-container" >
            <div className="ripple"></div>
        </div>
    )
}