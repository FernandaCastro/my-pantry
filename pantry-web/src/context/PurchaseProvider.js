import { createContext, useEffect, useState } from "react";

export const PurchaseContext = createContext();

export function PurchaseProvider({ children }) {

  const [purchaseCtx, setPurchaseCtx] = useState(() => {

    const data = localStorage.getItem("purchase");
    return !data || data === 'undefined' || Object.keys(data).length === 0 ? [] : JSON.parse(data);
    
  });

  useEffect(() => {
    localStorage.setItem("purchase", JSON.stringify(purchaseCtx));
  }, [purchaseCtx]);

  return (
    <PurchaseContext.Provider value={{ purchaseCtx, setPurchaseCtx }}>
      {children}
    </PurchaseContext.Provider>
  );
}