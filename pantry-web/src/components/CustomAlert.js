import { useEffect, useRef } from "react";
import { Overlay, Alert } from "react-bootstrap";
import useAlert from "../hooks/useAlert";

function CustomAlert() {

  const target = useRef(null);
  const { alert, hideAlert } = useAlert();

  useEffect(() => {
    if (alert?.show) {
      const timeout = setTimeout(() => hideAlert(), 10000);
      return () => clearTimeout(timeout); // Clean up the timeout on unmount
    }
  }, [alert?.show]);



  return (

    <div ref={target} className='alert-box'>
      {target.current && (
        <Overlay target={target.current} show={alert.show} placement="bottom" transition={false} >
          {({
            placement: _placement,
            arrowProps: _arrowProps,
            show: _show,
            popper: _popper,
            hasDoneInitialMeasure: _hasDoneInitialMeasure,
            ...props
          }) => (
            <Alert variant={alert.type} show={alert.show} onClose={hideAlert} dismissible transition={false} {...props} >{alert.message}</Alert>
          )}
        </Overlay>)}
    </div>
  )
}

export default CustomAlert;
