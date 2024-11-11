import { useEffect, useRef } from "react";
import { Fade, Overlay } from "react-bootstrap";
import useAlert from "../hooks/useAlert";
import Alert from 'react-bootstrap/Alert';

function CustomAlert() {

  const { alert, hideAlert } = useAlert();
  const target = useRef(null);

  useEffect(() => {
    if (alert.show) {
      const timeout = setTimeout(() => hideAlert(), 10000);
      return () => clearTimeout(timeout); // Clean up the timeout on unmount
    }
  }, [alert.show]);

  return (

    <div ref={target} className='alert-box' >
      <Overlay target={target.current} show={alert.show} placement="bottom" transition={Fade}>
        <Alert variant={alert.type} show={alert.show} dismissible onClose={() => hideAlert()} transition={Fade}>{alert.message}</Alert>
      </Overlay>
    </div>
  )
}

export default CustomAlert;