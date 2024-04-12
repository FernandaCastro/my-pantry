import Stack from 'react-bootstrap/Stack';
import { BsCaretDown, BsCaretUp } from "react-icons/bs";
import Button from 'react-bootstrap/Button';
import { useState } from 'react';
import { Form } from 'react-bootstrap';


function NumericField({ object, attribute, onValueChange, disabled }) {

    const [value, setValue] = useState(object[attribute]);

    function handleIncrease() {
        let v = value + 1;
        setValue(v);
        object[attribute] = v;
        onValueChange(object);
    }

    function handleDecrease() {
        if (value === 0) return;
        let v = value - 1;
        setValue(v);
        object[attribute] = v;
        onValueChange(object);
    }

    return (
        <Stack direction="horizontal" gap={1} >
            <div><Button className='m-0 p-0 d-flex align-items-start' variant='link' onClick={handleDecrease} disabled={disabled || value === 0} ><BsCaretDown className='icon' disabled={disabled || value === 0} /></Button></div>
            <div><span className='ms-1 me-1 ps-1 pe-1'>{value} </span></div>
            <div><Button className='m-0 p-0 d-flex align-items-start' variant='link' onClick={handleIncrease} disabled={disabled} ><BsCaretUp className='icon' disabled={disabled} /></Button></div>
        </Stack>
    )
}
export default NumericField;