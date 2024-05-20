import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router';
import { Button } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';
import Form from 'react-bootstrap/Form';
import VariantType from '../components/VariantType.js';
import useAlert from '../hooks/useAlert.js';
import { postResetPassword, getResetPassword, updateAccount } from '../services/apis/mypantry/requests/AccountRequests';

export default function ResetPassword() {

    const navigate = useNavigate();
    const { showAlert } = useAlert();
    const [valid, setValid] = useState({});
    const [errors, setErrors] = useState({});
    const [validForm, setValidForm] = useState(false);
    const { enteredEmail } = useParams();

    const [account, setAccount] = useState({
        email: enteredEmail,
        password: "",
        confirmPassword: "",
        passwordQuestion: "",
        passwordAnswer: ""
    });
    const [isProcessing, setIsProcessing] = useState(false);

    useEffect(() => {
        if (enteredEmail && validateField('email', enteredEmail)) {
            fetchPasswordQuestion();
        }
    }, [])

    const validateField = (name, value) => {
        let error;
        let validated = true;

        if (name === 'email') {
            if (!value || !/\S+@\S+\.\S+/.test(value)) {
                error = 'Please enter a valid email address.';
                validated = false;
            }
        }

        else if (name === 'passwordAnswer') {
            if (!value || value.length === 0) {
                error = ' Please enter the answer for the question above.';
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

        return validated;

    };

    function isFormValid(copyValid) {
        let isValid;
        if (Object.keys(copyValid).length >= 4) {
            let found = Object.keys(copyValid).find(key => copyValid[key] === 0);
            isValid = !found;
        } else {
            isValid = false;
        }
        setValidForm(isValid);
        console.log(validForm);
        return validForm;
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
            email: "",
            password: "",
            confirmPassword: "",
            passwordQuestion: "",
            passwordAnswer: ""
        })
    }

    const handleOnBlurCapture = (e) => {
        const { name, value } = e.target;
        validateField(name, value);
    };

    const handleOnValidEmail = (e) => {
        if (e.target.value && valid.email) {
            fetchPasswordQuestion(e.target.value);
        } else {
            clearResetQuestion();
        }
    }

    function clearResetQuestion() {
        setAccount({
            ...account,
            passwordQuestion: ""
        });
    }

    async function handleSubmit(e) {
        if (!isProcessing) {
            // Prevent the browser from reloading the page
            e.preventDefault();
            const form = e.currentTarget;
            if (form.checkValidity() === false) {
                e.stopPropagation();
            } else {
                await handleResetPassword();
            }

        }
    }

    async function handleResetPassword() {
        try {
            setIsProcessing(true);
            await postResetPassword(account);
            showAlert(VariantType.SUCCESS, "Password successfully updated. Please log in.")
            navigate('/login');
        } catch (error) {
            showAlert(VariantType.DANGER, error.message);
        } finally {
            setIsProcessing(false);
        }
    }

    async function fetchPasswordQuestion() {
        try {
            setIsProcessing(true);

            const res = await getResetPassword(account.email);
            setAccount({
                ...account,
                passwordQuestion: res.passwordQuestion
            });
        }
        catch (error) {
            showAlert(VariantType.DANGER, error.message);
            clearResetQuestion();
        }
        finally {
            setIsProcessing(false);
        }
    }

    return (
        <>
            <div className='centralized-header-box'>
                <h6 className='bigger-title'>Reset Password</h6>
            </div>
            <div className='centralized-box'>
                <Form onSubmit={handleSubmit} className='w-100'>
                    <Form.Group className="mb-2" controlId="formId">
                        <Form.Label size="sm" className="mb-0 title">Email</Form.Label>
                        <Form.Control type="text" name="email" defaultValue={account.email}
                            required
                            isInvalid={!account.email || !/\S+@\S+\.\S+/.test(account.email)}
                            onChange={handleOnChange}
                            onBlurCapture={handleOnBlurCapture}
                            onBlur={handleOnValidEmail}
                            className={`form-control ${!valid.email ? '' : valid.email ? 'is-valid' : 'is-invalid'}`} />
                        <Form.Control.Feedback type="invalid">
                            {errors.email}
                        </Form.Control.Feedback>
                    </Form.Group>

                    <Form.Group className="mb-2" controlId="formId">
                        <Form.Label size="sm" className="mb-0 title">Question</Form.Label>
                        <Form.Control type="text" name="passwordQuestion" defaultValue={account.passwordQuestion} disabled={true} 
                        className="reset-question-field"/>
                    </Form.Group>

                    <Form.Group className="mb-2" controlId="formId">
                        <Form.Label size="sm" className="mb-0 title">Answer</Form.Label>
                        <Form.Control type="password" name="passwordAnswer" defaultValue={account.passwordAnswer}
                            required
                            isInvalid={!account.passwordAnswer}
                            onChange={handleOnChange}
                            onBlurCapture={handleOnBlurCapture}
                            className={`form-control ${!valid.passwordAnswer ? '' : valid.passwordAnswer ? 'is-valid' : 'is-invalid'}`} />
                        <Form.Control.Feedback type="invalid">
                            {errors.passwordAnswer}
                        </Form.Control.Feedback>
                    </Form.Group>

                    <Form.Group className="mb-2" controlId="formId">
                        <Form.Label size="sm" className="mb-0 title">New Password</Form.Label>
                        <Form.Control type="password" name="password" defaultValue={account.password}
                            required
                            minLength={6}
                            isInvalid={!account.password || account.password.length < 6}
                            onChange={handleOnChange}
                            onBlurCapture={handleOnBlurCapture}
                            className={`form-control ${!valid.password ? '' : valid.password ? 'is-valid' : 'is-invalid'}`} />
                        <Form.Control.Feedback type="invalid">
                            {errors.password}
                        </Form.Control.Feedback>
                    </Form.Group>

                    <Form.Group className="mb-2" controlId="formId">
                        <Form.Label size="sm" className="mb-0 title">Repeat Password</Form.Label>
                        <Form.Control type="password" name="confirmPassword" defaultValue={account.confirmPassword}
                            required
                            minLength={6}
                            pattern={account.password}
                            isInvalid={!account.confirmPassword || account.confirmPassword != account.password}
                            onChange={handleOnChange}
                            onBlurCapture={handleOnBlurCapture}
                            className={`form-control ${!valid.confirmPassword ? '' : valid.confirmPassword ? 'is-valid' : 'is-invalid'}`} />
                        <Form.Control.Feedback type="invalid">
                            {errors.confirmPassword}
                        </Form.Control.Feedback>
                    </Form.Group>

                    <div className="d-flex justify-content-end gap-1 pt-2 pb-2">
                        <Button bsPrefix='btn-custom' size="sm" onClick={clearAccount} disabled={!Object.keys(valid).length > 0}>Clear</Button>
                        <Button bsPrefix="btn-custom" size="sm" type="submit" variant="link" disabled={!validForm}>Reset Password</Button>
                    </div>
                </Form>
            </div>
        </>

    )
}