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
    const { profileCtx } = useContext(ProfileContext);


    const [validateForm, setValidateForm] = useState(false);

    const [refresh, setRefresh] = useState(false);

    const [formFields, setFormFields] = useState({});

    const [isFormValid, setIsFormValid] = useState(false);

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
        if (validateForm) {
            Object.entries(account).forEach(entry => validateField(entry[0], entry[1]));
            setValidateForm(!validateForm);
            setRefresh(!refresh);
        }

    }, [validateForm])

    async function fetchAccount(id) {
        setIsProcessing(true);
        try {
            const res = await getAccount(id);

            setAccount((prev) => ({
                ...prev,
                id: res.id,
                name: res.name,
                email: res.email,
                password: res.password,
                confirmPassword: res.password,
                passwordQuestion: res.passwordQuestion,
                passwordAnswer: res.passwordAnswer
            }));

            setValidateForm(true);


        } catch (error) {
            showAlert(VariantType.DANGER, error.message);
        } finally {
            setIsProcessing(false);
        }
    }

    const validateField = (name, value) => {
        let error;
        let isValid = true;

        if (name === 'name') {
            if (!value || value.length === 0) {
                error = 'Please enter a valid name.';
                isValid = false;
            }
        }

        else if (name === 'email') {
            if (!value || !/\S+@\S+\.\S+/.test(value)) {
                error = 'Please enter a valid email address.';
                isValid = false;
            }
        }

        else if (name === 'password') {
            if (!value) {
                error = 'Password is required';
                isValid = false;
            } else if (value.length < 6) {
                error = 'Password must be at least 6 characters long.';
                isValid = false;
            }
        }


        else if (name === 'confirmPassword') {
            if (!value) {
                error = 'Repeat the password';
                isValid = false;
            } else if (value !== account.password) {
                error = 'Passwords do not match.';
                isValid = false;
            }
        }

        else if (name === 'passwordQuestion') {
            if (!value || value.length === 0) {
                error = 'Please enter a question to be used when resetting your password.';
                isValid = false;
            }
        }

        else if (name === 'passwordAnswer') {
            if (!value || value.length === 0) {
                error = ' Please enter the answer to be used when resetting your password.';
                isValid = false;
            }
        }

        else if (name === 'id') {
            isValid = true;
        }

        setFormFields((prev) => (
            {
                ...prev,
                [name]: {
                    isValid: isValid,
                    error: error,
                },
            }));

        const copyFormFields = {
            ...formFields,
            [name]: {
                isValid: isValid,
                error: error,
            }
        }

        checkFormValidation(copyFormFields);

    };

    function checkFormValidation(copyFormFields) {
        let formValid = false;
        if (Object.keys(copyFormFields).length >= 6) {
            let foundAnyInvalid = Object.keys(copyFormFields).find(key => !copyFormFields[key].isValid);
            formValid = !foundAnyInvalid;
        }
        setIsFormValid(formValid);
    }

    function getError(field) {
        if (!formFields[field]) return "";
        const { error } = formFields[field];
        return error;
    }

    const handleOnBlur = (e) => {
        const { name, value } = e.target;
        validateField(name, value);
    };

    const handleSubmit = (event) => {
        event.preventDefault();
        if (!isFormValid) {
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
        setAccount((prev) => ({ ...prev, [name]: value }));
    };

    function clearAccount() {
        setFormFields({});

        if (mode === 'new') {
            setAccount(prev => ({
                ...prev,
                name: "",
                email: "",
                password: "",
                confirmPassword: "",
                passwordQuestion: "",
                passwordAnswer: ""
            }))
            setRefresh(!refresh);

        } else {
            fetchAccount(profileCtx.id);
        }
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
                            className={`form-control ${!formFields.name ? '' : formFields.name.isValid ? 'is-valid' : 'is-invalid'}`} />
                        <Form.Control.Feedback type="invalid">
                            {getError('name')}
                        </Form.Control.Feedback>
                    </Form.Group>

                    <Form.Group className="mb-2" controlId="formId">
                        <Form.Label size="sm" className="mb-0 title">Email</Form.Label>
                        <Form.Control type="text" name="email" defaultValue={account.email}
                            required
                            isInvalid={!account.email || !/\S+@\S+\.\S+/.test(account.email)}
                            onChange={handleOnChange}
                            onBlur={handleOnBlur}
                            className={`form-control ${!formFields?.email ? '' : formFields.email.isValid ? 'is-valid' : 'is-invalid'}`} />
                        <Form.Control.Feedback type="invalid">
                            {getError('email')}
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
                            className={`form-control ${!formFields?.password ? '' : formFields.password.isValid ? 'is-valid' : 'is-invalid'}`} />
                        <Form.Control.Feedback type="invalid">
                            {getError('password')}
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
                            className={`form-control ${!formFields?.confirmPassword ? '' : formFields.confirmPassword.isValid ? 'is-valid' : 'is-invalid'}`} />
                        <Form.Control.Feedback type="invalid">
                            {getError('confirmPassword')}
                        </Form.Control.Feedback>
                    </Form.Group>
                    <Form.Group className="mb-2" controlId="formId">
                        <Form.Label size="sm" className="mb-0 title">Reset Question*</Form.Label>
                        <Form.Control type="text" name="passwordQuestion" defaultValue={account.passwordQuestion}
                            required
                            isInvalid={!account.passwordQuestion || account.passwordQuestion.length === 0}
                            onChange={handleOnChange}
                            onBlur={handleOnBlur}
                            className={`form-control ${!formFields?.passwordQuestion ? '' : formFields.passwordQuestion.isValid ? 'is-valid' : 'is-invalid'}`} />
                        <Form.Control.Feedback type="invalid">
                            {getError('passwordQuestion')}
                        </Form.Control.Feedback>
                    </Form.Group>
                    <Form.Group className="mb-2" controlId="formId">
                        <Form.Label size="sm" className="mb-0 title">Reset Answer*</Form.Label>
                        <Form.Control type="text" name="passwordAnswer" defaultValue={account.passwordAnswer}
                            required
                            isInvalid={!account.passwordAnswer || account.passwordAnswer.length === 0}
                            onChange={handleOnChange}
                            onBlur={handleOnBlur}
                            className={`form-control ${!formFields?.passwordAnswer ? '' : formFields.passwordAnswer.isValid ? 'is-valid' : 'is-invalid'}`} />
                        <Form.Control.Feedback type="invalid">
                            {getError('passwordAnswer')}
                        </Form.Control.Feedback>

                    </Form.Group>

                    <div className="d-flex justify-content-end gap-1 pt-2 pb-2">
                        <Button bsPrefix='btn-custom' onClick={clearAccount} size="sm" >Clear</Button>
                        <Button bsPrefix='btn-custom' type="submit" size="sm" disabled={!isFormValid}>{mode === 'new' ? 'Create Account' : 'Save'} </Button>
                    </div>
                </Form>
            </div>
        </div>

    )
}