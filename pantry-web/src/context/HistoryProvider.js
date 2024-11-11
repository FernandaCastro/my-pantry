// import { useCallback, useEffect } from "react";
// import { useNavigate } from "react-router-dom";

// export default function HistoryProvider({ children }) {

//     const navigate = useCallback(useNavigate(), []); // Memoize navigate with useCallback

//     useEffect(() => {
//         History.navigate = navigate;
//     }, [navigate]); // Update History.navigate only if navigate changes

//     return ({ children })
// }