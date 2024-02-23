import { useEffect } from 'react';

const useScript = (url, onload) => {
    useEffect(() => {
        if (typeof window.google === "undefined") {
            const script = document.createElement('script');

            script.src = url;
            script.onload = onload;

            document.head.appendChild(script);

            return () => {
                document.head.removeChild(script);
            };
        }
    }, [url, onload]);
};

export default useScript;