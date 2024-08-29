import { useMemo, useState } from "react";
import { LoadingContext } from "./AppContext";

export const LoadingProvider = ({ children }) => {

    const [loading, setIsLoading] = useState(false);

    // Memoize the context value to prevent unnecessary re-renders
    const value = useMemo(() => ({ loading, setIsLoading }), [loading]);

    return (
        <LoadingContext.Provider value={value}>
            {loading &&
                <div className="loader-container" >
                    <div className="ripple"></div>
                </div>}
            {children}
        </LoadingContext.Provider>
    );
};