import React, { useState, useRef } from 'react';
import { Button } from 'react-bootstrap';
import { register } from '../services/LoginService';
import { useNavigate } from 'react-router-dom';
import Form from 'react-bootstrap/Form';
import VariantType from '../components/VariantType.js';
import useAlert from '../hooks/useAlert.js';

export default function Register() {

    const navigate = useNavigate();
    const { showAlert } = useAlert();
    const [valid, setValid] = useState({});
    const [errors, setErrors] = useState({});
    const [validForm, setValidForm] = useState(0); //false

    const [account, setAccount] = useState({
        name: "",
        email: "",
        password: "",
        confirmPassword: "",
        passwordQuestion: "",
        passwordAnswer: ""
    });
    const [isProcessing, setIsProcessing] = useState(false);

    const validateField = (name, value) => {
        let error;
        let validated = 1;

        if (name === 'name') {
            if (!value || value.length === 0) {
                error = 'Please enter a valid name.';
                validated = 0;
            }
        }

        else if (name === 'email') {
            if (!value || !/\S+@\S+\.\S+/.test(value)) {
                error = 'Please enter a valid email address.';
                validated = 0;
            }
        }

        else if (name === 'password') {
            if (!value) {
                error = 'Password is required';
                validated = 0;
            } else if (value.length < 6) {
                error = 'Password must be at least 6 characters long.';
                validated = 0;
            }
        }


        else if (name === 'confirmPassword') {
            if (!value) {
                error = 'Repeat the password';
                validated = 0;
            } else if (value != account.password) {
                error = 'Passwords do not match.';
                validated = 0;
            }
        }

        else if (name === 'passwordQuestion') {
            if (!value || value.length === 0) {
                error = 'Please enter a question to be used when resetting your password.';
                validated = 0;
            }
        }

        else if (name === 'passwordAnswer') {
            if (!value || value.length === 0) {
                error = ' Please enter the answer to be used when resetting your password.';
                validated = 0;
            }
        }

        setErrors({
            ...errors,
            [name]: error
        });

        const copy = {
            ...valid,
            [name]: validated
        }
        setValid(copy);

        isFormValid(copy);

    };

    function isFormValid(copyValid) {
        if (Object.keys(copyValid).length >= 6) {
            let isValid = Object.keys(copyValid).find(key => copyValid[key] === 0);
            !isValid ? setValidForm(v => v + 1) : setValidForm(v => v - v);

        } else {
            setValidForm(v => v - v);
        }
        console.log(validForm);
    }

    const handleOnBlur = (e) => {
        const { name, value } = e.target;
        validateField(name, value);
    };


    const handleSubmit = (event) => {
        event.preventDefault();

        const form = event.currentTarget;
        if (form.checkValidity() === false) {
            event.stopPropagation();
        } else {
            handleRegister();
        }
    };

    async function handleRegister() {
        if (!isProcessing) {
            setIsProcessing(true);

            try {
                await register(account);
                showAlert(VariantType.SUCCESS, "You've been successfully registered. Please login.");
                navigate('/login');
            } catch (error) {
                showAlert(VariantType.DANGER, error.message);
            }

            setIsProcessing(false);
        }
    }

    const handleOnChange = (event) => {
        const { name, value } = event.target;
        setAccount({
            ...account,
            [name]: value,
        });
    };

    function clearAccount() {
        setAccount({
            ...account,
            name: "",
            email: "",
            password: "",
            confirmPassword: "",
            passwordQuestion: "",
            passwordAnswer: ""
        })
    }

    return (
        <div>
            <div className='bigger-title-box'>
                <h6 className='bigger-title'>New Account</h6>
            </div>
            <div className='register'>
                <Form onSubmit={handleSubmit} className='w-100' noValidate>

                    <Form.Group className="mb-2" controlId="formId">
                        <Form.Label size="sm" className="mb-0 title">Your Name</Form.Label>
                        <Form.Control type="text" size="sm" name="name" defaultValue={account.name}
                            required
                            isInvalid={!account.name || account.name.length === 0}
                            onChange={handleOnChange}
                            onBlur={handleOnBlur}
                            className={`form-control ${!valid.name ? '' : valid.name === 1 ? 'is-valid' : 'is-invalid'}`} />
                        <Form.Control.Feedback type="invalid">
                            {errors.name}
                        </Form.Control.Feedback>
                    </Form.Group>

                    <Form.Group className="mb-2" controlId="formId">
                        <Form.Label size="sm" className="mb-0 title">Email</Form.Label>
                        <Form.Control type="text" size="sm" name="email" defaultValue={account.email}
                            required
                            isInvalid={!account.email || !/\S+@\S+\.\S+/.test(account.email)}
                            onChange={handleOnChange}
                            onBlur={handleOnBlur}
                            className={`form-control ${!valid.email ? '' : valid.email === 1 ? 'is-valid' : 'is-invalid'}`} />
                        <Form.Control.Feedback type="invalid">
                            {errors.email}
                        </Form.Control.Feedback>
                    </Form.Group>

                    <Form.Group className="mb-2" controlId="formId">
                        <Form.Label size="sm" className="mb-0 title">Password*</Form.Label>
                        <Form.Control type="password" size="sm" name="password" defaultValue={account.password}
                            required
                            minLength={6}
                            isInvalid={!account.password || account.password.length < 6}
                            onChange={handleOnChange}
                            onBlur={handleOnBlur}
                            className={`form-control ${!valid.password ? '' : valid.password === 1 ? 'is-valid' : 'is-invalid'}`} />
                        <Form.Control.Feedback type="invalid">
                            {errors.password}
                        </Form.Control.Feedback>
                    </Form.Group>
                    <Form.Group className="mb-2" controlId="formId">
                        <Form.Label size="sm" className="mb-0 title">Confirm Password*</Form.Label>
                        <Form.Control type="password" size="sm" name="confirmPassword" defaultValue={account.confirmPassword}
                            required
                            minLength={6}
                            pattern={account.password}
                            isInvalid={!account.confirmPassword || account.confirmPassword != account.password}
                            onChange={handleOnChange}
                            onBlur={handleOnBlur}
                            className={`form-control ${!valid.confirmPassword ? '' : valid.confirmPassword === 1 ? 'is-valid' : 'is-invalid'}`} />
                        <Form.Control.Feedback type="invalid">
                            {errors.confirmPassword}
                        </Form.Control.Feedback>
                    </Form.Group>
                    <Form.Group className="mb-2" controlId="formId">
                        <Form.Label size="sm" className="mb-0 title">Reset Question*</Form.Label>
                        <Form.Control type="text" size="sm" name="passwordQuestion" defaultValue={account.passwordQuestion}
                            required
                            isInvalid={!account.passwordQuestion || account.passwordQuestion.length === 0}
                            onChange={handleOnChange}
                            onBlur={handleOnBlur}
                            className={`form-control ${!valid.passwordQuestion ? '' : valid.passwordQuestion === 1 ? 'is-valid' : 'is-invalid'}`} />
                        <Form.Control.Feedback type="invalid">
                            {errors.passwordQuestion}
                        </Form.Control.Feedback>
                    </Form.Group>
                    <Form.Group className="mb-2" controlId="formId">
                        <Form.Label size="sm" className="mb-0 title">Reset Answer*</Form.Label>
                        <Form.Control type="text" size="sm" name="passwordAnswer" defaultValue={account.passwordAnswer}
                            required
                            isInvalid={!account.passwordAnswer || account.passwordAnswer.length === 0}
                            onChange={handleOnChange}
                            onBlur={handleOnBlur}
                            className={`form-control ${!valid.passwordAnswer ? '' : valid.passwordAnswer === 1 ? 'is-valid' : 'is-invalid'}`} />
                        <Form.Control.Feedback type="invalid">
                            {errors.passwordAnswer}
                        </Form.Control.Feedback>

                    </Form.Group>

                    <div key={validForm} className="d-flex justify-content-end gap-1 pt-2 pb-2">
                        <Button bsPrefix='btn-custom' onClick={clearAccount} size="sm" >Clear</Button>
                        <Button bsPrefix='btn-custom' type="submit" size="sm" disabled={validForm === 0}>Sign up</Button>
                    </div>
                </Form>
            </div>
        </div>

    )
}