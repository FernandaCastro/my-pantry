import { useState } from "react";
import { Button, Form } from "react-bootstrap";
import { BiShow, BiHide } from "react-icons/bi";
import { BsEye, BsEyeSlash } from "react-icons/bs";

export default function PasswordInput({ defaultValue, updatePassword }) {

    const [show, setShow] = useState(false);

    return (
        <div className="d-flex flex-row form-control p-0 m-0" >
            <Form.Control type={show ? "text" : "password"} name="password"
                className="form-control-password" defaultValue={defaultValue}
                onChange={(v) => updatePassword(v.target.value)} />
            <Button onClick={() => setShow(!show)} variant="link" className='pe-2'>
                {show ?
                    <BsEye className='small-icon'  style={{ width: '18px', height: '18px'}} /> :
                    <BsEyeSlash className='small-icon' style={{ width: '18px', height: '18px'}} />
                }
            </Button>
        </div>
    )
}