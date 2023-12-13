import Stack from 'react-bootstrap/Stack';
import { BsCaretDown, BsCaretUp } from "react-icons/bs";
import Button from 'react-bootstrap/Button';
import { useState } from 'react';


function NumericField({ object, attribute, onValueChange }) {

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
            <div><Button variant='link' onClick={handleDecrease} className='m-0 p-0 d-flex align-items-start'><BsCaretDown /></Button></div>
            <div><span className='ms-1 me-1 ps-1 pe-1'>{value}</span></div>
            <div><Button variant='link' onClick={handleIncrease} className='m-0 p-0 d-flex align-items-start'><BsCaretUp /></Button></div>
        </Stack>
    )
}
export default NumericField;