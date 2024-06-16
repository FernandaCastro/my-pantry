import Stack from 'react-bootstrap/Stack';
import { BsCaretDown, BsCaretUp } from "react-icons/bs";
import Button from 'react-bootstrap/Button';
import { useState } from 'react';


function CurrentQuantityField({ object, attribute, onValueChange, disabled }) {

    const [value, setValue] = useState(object[attribute]);

    function handleDecrease() {
        if (value === 0) return;
        let v = value - 1;
        setValue(v);
        object[attribute] = v;
        onValueChange(object);
    }

    return (
        <Stack direction="horizontal" gap={1} className='align-items-start'>
            <div><h6 className='ms-1 me-1 ps-1 pe-1 default-text-color'>{value} </h6></div>
            <div><Button className='m-0 p-0' variant='link' onClick={handleDecrease} disabled={disabled || value === 0} ><BsCaretDown className='icon align-top' hidden={disabled} disabled={value === 0} /></Button></div>
        </Stack>
    )
}
export default CurrentQuantityField;