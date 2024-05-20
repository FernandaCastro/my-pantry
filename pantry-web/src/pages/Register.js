import React, { useState, useContext, useEffect } from 'react';
import { Button } from 'react-bootstrap';
import { register } from '../services/LoginService';
import { useNavigate } from 'react-router-dom';
import Form from 'react-bootstrap/Form';
import VariantType from '../components/VariantType.js';
import useAlert from '../hooks/useAlert.js';
import { ProfileContext } from '../services/context/AppContext';
import { getAccount, updateAccount } from '../services/apis/mypantry/requests/AccountRequests';

export default function Register({ mode }) {

    const navigate = useNavigate();
    const { showAlert } = useAlert();
    const [valid, setValid] = useState({});
    const [errors, setErrors] = useState({});
    const [isValidForm, setIsValidForm] = useState(false);
    const { profileCtx, setProfileCtx } = useContext(ProfileContext);
    const [validateAllFields, setValidateAllFields] = useState(false);
    const [refresh, setRefresh] = useState(false);

    const [account, setAccount] = useState({
        name: "",
        email: "",
        password: "",
        confirmPassword: "",
        passwordQuestion: "",
        passwordAnswer: ""
    });

    const [isProcessing, setIsProcessing] = useState(false);

    useEffect(() => {
        if (mode === 'edit') {
            fetchAccount(profileCtx.id);
        }

    }, [])

    useEffect(() => {
        if (validateAllFields) {
            Object.entries(account).forEach(entry => validateField(entry[0], entry[1]));
            setValidateAllFields(false);
            setIsValidForm(valid);
        }

    }, [validateAllFields])

    async function fetchAccount(id) {
        setIsProcessing(true);
        try {
            const res = await getAccount(id);

            setAccount((account) => {
                return {
                    ...account,
                    id: res.id,
                    name: res.name,
                    email: res.email,
                    password: res.password,
                    confirmPassword: res.password,
                    passwordQuestion: res.passwordQuestion,
                    passwordAnswer: res.passwordAnswer
                };
            });

            setValidateAllFields(true);

        } catch (error) {
            showAlert(VariantType.DANGER, error.message);
        } finally {
            setIsProcessing(false);
        }
    }

    const validateField = (name, value) => {
        let error;
        let validated = true;

        if (name === 'name') {
            if (!value || value.length === 0) {
                error = 'Please enter a valid name.';
                validated = false;
            }
        }

        else if (name === 'email') {
            if (!value || !/\S+@\S+\.\S+/.test(value)) {
                error = 'Please enter a valid email address.';
                validated = false;
            }
        }

        else if (name === 'password') {
            if (!value) {
                error = 'Password is required';
                validated = false;
            } else if (value.length < 6) {
                error = 'Password must be at least 6 characters long.';
                validated = false;
            }
        }


        else if (name === 'confirmPassword') {
            if (!value) {
                error = 'Repeat the password';
                validated = false;
            } else if (value != account.password) {
                error = 'Passwords do not match.';
                validated = false;
            }
        }

        else if (name === 'passwordQuestion') {
            if (!value || value.length === 0) {
                error = 'Please enter a question to be used when resetting your password.';
                validated = false;
            }
        }

        else if (name === 'passwordAnswer') {
            if (!value || value.length === 0) {
                error = ' Please enter the answer to be used when resetting your password.';
                validated = false;
            }
        }

        else if (name === 'id') {
            validated = true;
        }

        setErrors((errors) => {
            return { ...errors, [name]: error };
        });

        setValid((valid) => {
            return { ...valid, [name]: validated };
        });

        const copyValid = { ...valid, [name]: validated }
        checkForm(copyValid);

    };

    function checkForm(copyValid) {
        let isValid;
        if (Object.keys(copyValid).length >= 6) {
            let found = Object.keys(copyValid).find(key => !copyValid[key]);
            isValid = !found;
        } else {
            isValid = false;
        }
        setIsValidForm(isValid);
    }

    const handleOnBlur = (e) => {
        const { name, value } = e.target;
        validateField(name, value);
    };

    const handleSubmit = (event) => {
        event.preventDefault();
        const form = event.currentTarget;
        if (!isValidForm) {
            event.stopPropagation();
        } else {
            mode === 'new' ? handleCreateAccount() : handleUpdateAccount();
        }
    };

    function wait(time) {
        return new Promise(resolve => {
            setTimeout(resolve, time);
        });
    }

    async function handleCreateAccount() {
        if (!isProcessing) {
            setIsProcessing(true);

            try {
                await register(account);
                showAlert(VariantType.SUCCESS, "Your Account has been successfully created. Redirecting to Login page...");
                await wait(3000);
                navigate('/login');
            } catch (error) {
                showAlert(VariantType.DANGER, error.message);
            } finally {

                setIsProcessing(false);
            }
        }
    }

    async function handleUpdateAccount() {
        if (!isProcessing) {
            setIsProcessing(true);
            try {
                await updateAccount(account);
                showAlert(VariantType.SUCCESS, "Your Account has been successfully updated.");

            } catch (error) {
                showAlert(VariantType.DANGER, error.message);
            } finally {
                setIsProcessing(false);
            }
        }
    }

    const handleOnChange = (event) => {
        const { name, value } = event.target;
        setAccount((account) => {
            return { ...account, [name]: value };
        });
    };

    function clearAccount() {
        if (mode === 'new') {
            setAccount({
                ...account,
                name: "",
                email: "",
                password: "",
                confirmPassword: "",
                passwordQuestion: "",
                passwordAnswer: ""
            })
        } else {
            fetchAccount(profileCtx.id);
        }
        setRefresh(!refresh);
    }

    return (
        <div>
            <div className='centralized-header-box'>
                <h6 className='bigger-title'>{mode === 'new' ? 'New Account' : 'Edit Account'}</h6>
            </div>
            <div className='centralized-box'>
                <Form key={refresh} onSubmit={handleSubmit} className='w-100' noValidate>

                    <Form.Group className="mb-2" controlId="formId">
                        <Form.Label size="sm" className="mb-0 title">Your Name</Form.Label>
                        <Form.Control type="text" name="name" defaultValue={account.name}
                            required
                            isInvalid={!account.name || account.name.length === 0}
                            onChange={handleOnChange}
                            onBlur={handleOnBlur}
                            className={`form-control ${!valid.name ? '' : valid.name ? 'is-valid' : 'is-invalid'}`} />
                        <Form.Control.Feedback type="invalid">
                            {errors.name}
                        </Form.Control.Feedback>
                    </Form.Group>

                    <Form.Group className="mb-2" controlId="formId">
                        <Form.Label size="sm" className="mb-0 title">Email</Form.Label>
                        <Form.Control type="text" name="email" defaultValue={account.email}
                            required
                            isInvalid={!account.email || !/\S+@\S+\.\S+/.test(account.email)}
                            onChange={handleOnChange}
                            onBlur={handleOnBlur}
                            className={`form-control ${!valid.email ? '' : valid.email ? 'is-valid' : 'is-invalid'}`} />
                        <Form.Control.Feedback type="invalid">
                            {errors.email}
                        </Form.Control.Feedback>
                    </Form.Group>

                    <Form.Group className="mb-2" controlId="formId">
                        <Form.Label size="sm" className="mb-0 title">Password*</Form.Label>
                        <Form.Control type="password" name="password" defaultValue={account.password}
                            required
                            minLength={6}
                            isInvalid={!account.password || account.password.length < 6}
                            onChange={handleOnChange}
                            onBlur={handleOnBlur}
                            className={`form-control ${!valid.password ? '' : valid.password ? 'is-valid' : 'is-invalid'}`} />
                        <Form.Control.Feedback type="invalid">
                            {errors.password}
                        </Form.Control.Feedback>
                    </Form.Group>
                    <Form.Group className="mb-2" controlId="formId">
                        <Form.Label size="sm" className="mb-0 title">Confirm Password*</Form.Label>
                        <Form.Control type="password" name="confirmPassword" defaultValue={account.confirmPassword}
                            required
                            minLength={6}
                            pattern={account.password}
                            isInvalid={!account.confirmPassword || account.confirmPassword != account.password}
                            onChange={handleOnChange}
                            onBlur={handleOnBlur}
                            className={`form-control ${!valid.confirmPassword ? '' : valid.confirmPassword ? 'is-valid' : 'is-invalid'}`} />
                        <Form.Control.Feedback type="invalid">
                            {errors.confirmPassword}
                        </Form.Control.Feedback>
                    </Form.Group>
                    <Form.Group className="mb-2" controlId="formId">
                        <Form.Label size="sm" className="mb-0 title">Reset Question*</Form.Label>
                        <Form.Control type="text" name="passwordQuestion" defaultValue={account.passwordQuestion}
                            required
                            isInvalid={!account.passwordQuestion || account.passwordQuestion.length === 0}
                            onChange={handleOnChange}
                            onBlur={handleOnBlur}
                            className={`form-control ${!valid.passwordQuestion ? '' : valid.passwordQuestion ? 'is-valid' : 'is-invalid'}`} />
                        <Form.Control.Feedback type="invalid">
                            {errors.passwordQuestion}
                        </Form.Control.Feedback>
                    </Form.Group>
                    <Form.Group className="mb-2" controlId="formId">
                        <Form.Label size="sm" className="mb-0 title">Reset Answer*</Form.Label>
                        <Form.Control type="text" name="passwordAnswer" defaultValue={account.passwordAnswer}
                            required
                            isInvalid={!account.passwordAnswer || account.passwordAnswer.length === 0}
                            onChange={handleOnChange}
                            onBlur={handleOnBlur}
                            className={`form-control ${!valid.passwordAnswer ? '' : valid.passwordAnswer ? 'is-valid' : 'is-invalid'}`} />
                        <Form.Control.Feedback type="invalid">
                            {errors.passwordAnswer}
                        </Form.Control.Feedback>

                    </Form.Group>

                    <div className="d-flex justify-content-end gap-1 pt-2 pb-2">
                        <Button bsPrefix='btn-custom' onClick={clearAccount} size="sm" >Clear</Button>
                        <Button bsPrefix='btn-custom' type="submit" size="sm" disabled={!isValidForm}>{mode === 'new' ? 'Create Account' : 'Save'} </Button>
                    </div>
                </Form>
            </div>
        </div>

    )
}