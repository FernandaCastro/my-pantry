import { useState, useEffect } from 'react';

function useBreakpoint(minWidth) {
  const [isAboveBreakpoint, setIsAboveBreakpoint] = useState(window.innerWidth >= minWidth);

  useEffect(() => {
    const mediaQuery = window.matchMedia(`(min-width: ${minWidth}px)`);
    
    const handleResize = (e) => {
      setIsAboveBreakpoint(e.matches);
    };

    // Adiciona o listener para o matchMedia
    mediaQuery.addEventListener('change', handleResize);

    // Verifica o breakpoint inicialmente
    setIsAboveBreakpoint(mediaQuery.matches);

    return () => {
      mediaQuery.removeEventListener('change', handleResize);
    };
  }, [minWidth]);

  return isAboveBreakpoint;
}

export default useBreakpoint;