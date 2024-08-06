import Stack from 'react-bootstrap/Stack';
import { BsCaretDown, BsCaretUp } from "react-icons/bs";
import Button from 'react-bootstrap/Button';
import { useState } from 'react';

function WizardNumericField({ initialValue, item, onValueChange }) {

    const [value, setValue] = useState(initialValue);

    function handleIncrease() {
        let v = value + 1;
        setValue(v);
        onValueChange(item, v);
    }

    function handleDecrease() {
        if (value === 0) return;
        let v = value - 1;
        setValue(v);
        onValueChange(item, v);
    }

    return (
        <Stack direction="horizontal" gap={1} >
            <div><Button className='m-0 p-0 d-flex align-items-start' variant='link' onClick={handleDecrease} disabled={value === 0} ><BsCaretDown className='icon' disabled={value === 0} /></Button></div>
            <div><span className='ms-1 me-1 ps-1 pe-1'>{value} </span></div>
            <div><Button className='m-0 p-0 d-flex align-items-start' variant='link' onClick={handleIncrease} ><BsCaretUp className='icon' /></Button></div>
        </Stack>
    )
}
export default WizardNumericField;