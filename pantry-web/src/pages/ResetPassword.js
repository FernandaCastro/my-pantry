import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router';
import { Button } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';
import Form from 'react-bootstrap/Form';
import VariantType from '../components/VariantType.js';
import useAlert from '../hooks/useAlert.js';
import { postResetPassword, getResetPassword } from '../services/apis/mypantry/requests/AccountRequests';
import { useTranslation } from 'react-i18next';

export default function ResetPassword() {

    const { t } = useTranslation(['login', 'common']);

    const navigate = useNavigate();
    const { showAlert } = useAlert();
    const [valid, setValid] = useState({});
    const [errors, setErrors] = useState({});
    const [validForm, setValidForm] = useState(false);
    const { enteredEmail } = useParams();
    const [refresh, setRefresh] = useState(false);

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
                error = t("email-invalid");
                validated = false;
            }
        }

        else if (name === 'passwordAnswer') {
            if (!value || value.length === 0) {
                error = t("password-answer-invalid");
                validated = false;
            }
        }

        else if (name === 'password') {
            if (!value) {
                error = t("password-invalid-required");
                validated = false;
            } else if (value.length < 6) {
                error = t("password-invalid-length");
                validated = false;
            }
        }

        else if (name === 'confirmPassword') {
            if (!value) {
                error = t("confirm-password-invalid-required");
                validated = false;
            } else if (value != account.password) {
                error = t("confirm-password-invalid-not-match");
                validated = false;
            }
        }

        setErrors((errors) => {
            return { ...errors, [name]: error };
        });

        setValid((valid) => {
            return { ...valid, [name]: validated };
        });

        const copyValid = { ...valid, [name]: validated }
        isFormValid(copyValid);

        return validated;

    };

    function isFormValid(copyValid) {
        let isValid;
        if (Object.keys(copyValid).length >= 4) {
            let found = Object.keys(copyValid).find(key => !copyValid[key]);
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

    function clearAccount() {
        const tempAccount = {
            ...account,
            password: "",
            confirmPassword: "",
            passwordAnswer: ""
        }
        setAccount(tempAccount);
        setRefresh(!refresh);
    }

    function clearResetQuestion() {
        const tempAccount = { ...account, passwordQuestion: "" }
        setAccount(tempAccount);
        setRefresh(!refresh);
    }

    function clearResetAnswer() {
        const tempAccount = { ...account, passwordAnswer: "" };
        setAccount(tempAccount);
        setRefresh(!refresh);
    }

    async function handleSubmit(e) {
        if (!isProcessing) {
            // Prevent the browser from reloading the page
            e.preventDefault();
            const form = e.currentTarget;
            if (!isFormValid) {
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
            showAlert(VariantType.SUCCESS, t("update-password-success"))
            navigate('/login');
        } catch (error) {
            showAlert(VariantType.DANGER, error.message);
            clearResetAnswer();
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
                <h6 className='bigger-title'>{t("reset-password-title")}</h6>
            </div>
            <div className='centralized-box'>
                <Form key={refresh} onSubmit={handleSubmit} className='w-100'>
                    <Form.Group className="mb-2" controlId="formId">
                        <Form.Label size="sm" className="mb-0 title">{t("email")}</Form.Label>
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
                        <Form.Label size="sm" className="mb-0 title">{t("reset-question")}</Form.Label>
                        <Form.Control type="text" name="passwordQuestion" defaultValue={account.passwordQuestion} disabled={true}
                            className="reset-question-field" />
                    </Form.Group>

                    <Form.Group className="mb-2" controlId="formId">
                        <Form.Label size="sm" className="mb-0 title">{t("reset-answer")}</Form.Label>
                        <Form.Control type="password" name="passwordAnswer" defaultValue={account.passwordAnswer}
                            required
                            isInvalid={!account.passwordAnswer}
                            onChange={handleOnChange}
                            onBlur={handleOnBlurCapture}
                            className={`form-control ${!valid.passwordAnswer ? '' : valid.passwordAnswer ? 'is-valid' : 'is-invalid'}`} />
                        <Form.Control.Feedback type="invalid">
                            {errors.passwordAnswer}
                        </Form.Control.Feedback>
                    </Form.Group>

                    <Form.Group className="mb-2" controlId="formId">
                        <Form.Label size="sm" className="mb-0 title">{t("new-password")}</Form.Label>
                        <Form.Control type="password" name="password" defaultValue={account.password}
                            required
                            minLength={6}
                            isInvalid={!account.password || account.password.length < 6}
                            onChange={handleOnChange}
                            onBlur={handleOnBlurCapture}
                            className={`form-control ${!valid.password ? '' : valid.password ? 'is-valid' : 'is-invalid'}`} />
                        <Form.Control.Feedback type="invalid">
                            {errors.password}
                        </Form.Control.Feedback>
                    </Form.Group>

                    <Form.Group className="mb-2" controlId="formId">
                        <Form.Label size="sm" className="mb-0 title">{t("confirm-password")}</Form.Label>
                        <Form.Control type="password" name="confirmPassword" defaultValue={account.confirmPassword}
                            required
                            minLength={6}
                            pattern={account.password}
                            isInvalid={!account.confirmPassword || account.confirmPassword != account.password}
                            onChange={handleOnChange}
                            onBlur={handleOnBlurCapture}
                            className={`form-control ${!valid.confirmPassword ? '' : valid.confirmPassword ? 'is-valid' : 'is-invalid'}`} />
                        <Form.Control.Feedback type="invalid">
                            {errors.confirmPassword}
                        </Form.Control.Feedback>
                    </Form.Group>

                    <div className="d-flex justify-content-end gap-1 pt-2 pb-2">
                        <Button bsPrefix='btn-custom' size="sm" onClick={clearAccount} disabled={!Object.keys(valid).length > 0}><span>{t("btn-clear", { ns: "common" })}</span></Button>
                        <Button bsPrefix="btn-custom" size="sm" type="submit" variant="link" disabled={!validForm}><span>{t("btn-reset-password")}</span></Button>
                    </div>
                </Form>
            </div>
        </>

    )
}