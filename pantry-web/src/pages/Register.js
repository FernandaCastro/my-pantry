import React, { useState, useContext, useEffect } from 'react';
import { Button, Modal, OverlayTrigger, Tooltip } from 'react-bootstrap';
import { register } from '../services/LoginService';
import { useNavigate } from 'react-router-dom';
import Form from 'react-bootstrap/Form';
import VariantType from '../components/VariantType.js';
import useAlert from '../hooks/useAlert.js';
import { ProfileContext } from '../services/context/AppContext';
import { getAccount, updateAccount } from '../services/apis/mypantry/requests/AccountRequests';
import { useTranslation } from 'react-i18next';
import useEncrypt from '../hooks/useRSAEncrypt';
import { BsChatDots } from 'react-icons/bs';
import { TbMessageCircleOff } from 'react-icons/tb'
import { useLoading } from '../hooks/useLoading';

export default function Register({ mode }) {

    const { t } = useTranslation(['login', 'common']);
    const { encrypt } = useEncrypt();

    const navigate = useNavigate();
    const { showAlert } = useAlert();
    const { profileCtx, setProfileCtx } = useContext(ProfileContext);


    const [validateForm, setValidateForm] = useState(false);

    const [refresh, setRefresh] = useState(false);

    const [formFields, setFormFields] = useState({});

    const [isFormValid, setIsFormValid] = useState(false);
    const [isChangePaswordFormValid, setIsChangePasswordFormValid] = useState(false);
    const [showResetAnswer, setShowResetAnswer] = useState(mode === "new");

    const [showChangePassword, setShowChangePassword] = useState(false);

    const [account, setAccount] = useState({
        name: "",
        email: "",
        passwordQuestion: ""
    });

    const { isLoading, setIsLoading } = useLoading();

    useEffect(() => {
        if (mode === 'edit') {
            fetchAccount(profileCtx.id);
        }

    }, [])

    useEffect(() => {
        if (validateForm) {
            Object.entries(account).forEach(entry => validateFields(entry[0], entry[1]));
            setValidateForm(!validateForm);
            setRefresh(!refresh);
        }

    }, [validateForm])

    async function fetchAccount(id) {
        setIsLoading(true);
        try {
            const res = await getAccount(id);

            setAccount({
                id: res.id,
                name: res.name,
                email: res.email,
                passwordQuestion: res.passwordQuestion
            });

            setValidateForm(true);


        } catch (error) {
            showAlert(VariantType.DANGER, error.message);
        } finally {
            setIsLoading(false);
        }
    }

    function refreshProfileCtx(updatedAccount) {
        const profile = {
            id: updatedAccount.id,
            name: updatedAccount.name,
            email: updatedAccount.email,
            pictureUrl: updatedAccount.pictureUrl,
            initials: getInitialsAttribute(updatedAccount.name),
            theme: profileCtx?.theme
        }
        setProfileCtx(profile);
    }

    function getInitialsAttribute(name) {
        const _name = name.split(" ");
        const initials = _name.length > 1 ? _name[0][0] + _name[_name.length - 1][0] : _name[0][0];
        return initials;
    }

    const validatePasswordFields = (name, value) => {
        let error;
        let isValid = true;

        if (name === 'password') {
            if (!value) {
                error = t("password-invalid-required");
                isValid = false;
            } else if (value.length < 6) {
                error = t("password-invalid-length")
                isValid = false;
            }
        }


        else if (name === 'confirmPassword') {
            if (!value) {
                error = t("confirm-password-invalid-required");
                isValid = false;
            } else if (value.length < 6) {
                error = t("password-invalid-length")
                isValid = false;
            } else if (value !== account.password) {
                error = t("confirm-password-invalid-not-match");
                isValid = false;
            }
        }

        setFormFields((prev) => (
            {
                ...prev,
                [name]: {
                    isValid: isValid,
                    error: error,
                },
            }));

        setIsChangePasswordFormValid(isValid);

        return [isValid, error];
    }

    const validateFields = (name, value) => {
        let error;
        let isValid = true;

        if (name === 'name') {
            if (!value || value.length === 0) {
                error = t("name-invalid");
                isValid = false;
            }
        }

        else if (name === 'email') {
            if (!value || !/\S+@\S+\.\S+/.test(value)) {
                error = t("email-invalid");
                isValid = false;
            }
        }

        else if (name === 'passwordQuestion') {
            if (!value || value.length === 0) {
                error = t("password-question-invalid");
                isValid = false;
            }
        }

        else if ((showResetAnswer || mode === "new") && name === 'passwordAnswer') {
            if (!value || value.length === 0) {
                error = t("password-answer-invalid");
                isValid = false;
            }
        }

        else if (name === 'id') {
            isValid = true;
        }

        else if (mode === "new") {
            [isValid, error] = validatePasswordFields(name, value);
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
        if ((mode === "new" && Object.keys(copyFormFields).length >= 6) || //new account
            (mode === "edit" && showResetAnswer && Object.keys(copyFormFields).length >= 4) || //changing Reset Answer
            (mode === "edit" && !showResetAnswer && Object.keys(copyFormFields).length >= 3)) // not changing Reset Answer
        {
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
        setAccount((prev) => ({ ...prev, [name]: value }));
        validateFields(name, value);
        showChangePassword ? validatePasswordFields(name, value) : validateFields(name, value);
    };

    const handleSubmit = (event) => {
        event.preventDefault();
        if (!isFormValid) {
            event.stopPropagation();
            return;
        }

        var encryptAcc = { ...account };
        if (account?.passwordAnswer && account.passwordAnswer.length > 0) {
            encryptAcc = {
                ...encryptAcc,
                passwordAnswer: encrypt.encrypt(encryptAcc.passwordAnswer)
            };

        }
        mode === 'new' ? handleCreateAccount(encryptAcc) : handleUpdateAccount(encryptAcc);

    };

    function wait(time) {
        return new Promise(resolve => {
            setTimeout(resolve, time);
        });
    }

    async function handleCreateAccount(accountData) {
        if (!isLoading) {
            setIsLoading(true);

            try {
                const encryptAcc = {
                    ...accountData,
                    password: encrypt.encrypt(accountData.password),
                    confirmPassword: encrypt.encrypt(accountData.password)
                };
                await register(encryptAcc);
                showAlert(VariantType.SUCCESS, t("create-account-success"));
                //await wait(3000);
                navigate('/login');
            } catch (error) {
                showAlert(VariantType.DANGER, error.message);
            } finally {

                setIsLoading(false);
            }
        }
    }

    const handleChangePasswordSubmit = (event) => {
        event.preventDefault();
        const isValid = validatePasswordFields();
        if (!isValid) {
            event.stopPropagation();
        } else {
            const encryptAccount = {
                id: account.id,
                password: encrypt.encrypt(account.password),
                confirmPassword: encrypt.encrypt(account.password),
            };
            handleUpdateAccount(encryptAccount);
        }
    };

    async function handleUpdateAccount(encryptAccount) {
        if (!isLoading) {
            setIsLoading(true);
            try {
                const updatedAccount = await updateAccount(encryptAccount);
                setAccount({
                    ...account,
                    password: null,
                    confirmPassword: null
                });
                setShowChangePassword(false);
                setIsChangePasswordFormValid(false);
                showAlert(VariantType.SUCCESS, t("update-account-success"));

                refreshProfileCtx(updatedAccount);

            } catch (error) {
                showAlert(VariantType.DANGER, error.message);
            } finally {
                setIsLoading(false);
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

    function handlePasswordAnswer() {
        setAccount((prev) => ({ ...prev, passwordAnswer: null }));
        const { passwordAnswer: _, ...newFormFields } = formFields;
        setFormFields(newFormFields);
        setShowResetAnswer(!showResetAnswer);
        checkFormValidation(newFormFields);
    }

    function renderChangePassword() {
        setAccount({
            ...account,
            password: null,
            confirmPassword: null
        });
        const { password: _, confirmPassword: __, ...newFormFields } = formFields;
        setFormFields(newFormFields);
        setShowChangePassword(!showChangePassword);
        setIsChangePasswordFormValid(false);
    }

    function renderPasswordFields() {
        return (
            <div>
                <Form.Group className="mb-2" controlId="formId">
                    <Form.Label size="sm" className="mb-0 title">{t("password")}</Form.Label>
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
                    <Form.Label size="sm" className="mb-0 title">{t("confirm-password")}</Form.Label>
                    <Form.Control type="password" name="confirmPassword" defaultValue={account.confirmPassword}
                        required
                        minLength={6}
                        pattern={account.password}
                        isInvalid={!account.confirmPassword || account.confirmPassword !== account.password}
                        onChange={handleOnChange}
                        onBlur={handleOnBlur}
                        className={`form-control ${!formFields?.confirmPassword ? '' : formFields.confirmPassword.isValid ? 'is-valid' : 'is-invalid'}`} />
                    <Form.Control.Feedback type="invalid">
                        {getError('confirmPassword')}
                    </Form.Control.Feedback>
                </Form.Group>
            </div>
        )
    }

    return (
        <div>
            <div className='centralized-header-box'>
                <h6 className='bigger-title'>{mode === 'new' ? t("new-account-title") : t("edit-account-title")}</h6>
            </div>
            <div className="centralized-box align-items-end pt-0 pb-1" style={{ display: mode === "edit" ? "flex" : "none" }}>
                <Button bsPrefix='btn-custom' onClick={renderChangePassword}><span>{t("btn-change-password")}</span></Button>
            </div>
            <div className='centralized-box'>
                <Form key={refresh} onSubmit={handleSubmit} className='w-100' noValidate>

                    <Form.Group className="mb-2" controlId="formId" hasValidation>
                        <Form.Label size="sm" className="mb-0 title">{t("name")}</Form.Label>
                        <Form.Control key={formFields?.name?.isValid} type="text" name="name" defaultValue={account.name}
                            required
                            isValid={formFields?.name?.isValid}
                            // isInvalid={!account.name || account.name.length === 0}
                            onChange={handleOnChange}
                            onBlur={handleOnBlur}
                            className={`form-control ${!formFields.name ? '' : formFields.name.isValid ? 'is-valid' : 'is-invalid'}`} />
                        <Form.Control.Feedback type="invalid">
                            {getError('name')}
                        </Form.Control.Feedback>
                    </Form.Group>

                    <Form.Group className="mb-2" controlId="formId">
                        <Form.Label size="sm" className="mb-0 title">{t("email")}</Form.Label>
                        <Form.Control key={formFields?.email?.isValid} type="text" name="email" defaultValue={account.email}
                            required
                            isValid={formFields?.email?.isValid}
                            // isInvalid={!account.email || !/\S+@\S+\.\S+/.test(account.email)}
                            onChange={handleOnChange}
                            onBlur={handleOnBlur}
                            className={`form-control ${!formFields?.email ? '' : formFields.email.isValid ? 'is-valid' : 'is-invalid'}`} />
                        <Form.Control.Feedback type="invalid">
                            {getError('email')}
                        </Form.Control.Feedback>
                    </Form.Group>
                    {mode === 'new' ? renderPasswordFields() : ""}
                    <Form.Group className="mt-4 mb-2 w-100" controlId="formId">
                        <div className="d-flex justify-content-between align-items-center" >
                            <Form.Label size="sm" className="mb-0 title">{t("title-reset-password")}</Form.Label>
                            <OverlayTrigger placement="bottom" delay={{ show: 250, hide: 350 }} overlay={<Tooltip className="custom-tooltip">{showResetAnswer ? t("btn-close-password-answer") : t("btn-change-password-answer")}</Tooltip>}>
                                <Button variant="link" onClick={handlePasswordAnswer} disabled={mode === "new"}>{showResetAnswer ? <TbMessageCircleOff className='icon' /> : <BsChatDots className='icon' />}</Button>
                            </OverlayTrigger>
                        </div>
                        <Form.Control type="text" name="passwordQuestion" defaultValue={account.passwordQuestion}
                            required
                            placeholder={t("placeholder-reset-question")}
                            isValid={formFields?.passwordQuestion?.isValid}
                            //isInvalid={!account.passwordQuestion || account.passwordQuestion.length === 0}
                            onChange={handleOnChange}
                            onBlur={handleOnBlur}
                            className={`form-control ${!formFields?.passwordQuestion ? '' : formFields.passwordQuestion.isValid ? 'is-valid' : 'is-invalid'}`} />
                        <Form.Control.Feedback type="invalid">
                            {getError('passwordQuestion')}
                        </Form.Control.Feedback>
                    </Form.Group>


                    <Form.Group className="mb-2 w-100" controlId="formId" style={{ display: showResetAnswer ? "block" : "none" }}>
                        <Form.Control key={showResetAnswer} type="password" name="passwordAnswer" defaultValue={account.passwordAnswer}
                            required
                            placeholder={t("placeholder-reset-answer")}
                            isValid={formFields?.passwordAnswer?.isValid}
                            //isInvalid={!account.passwordAnswer || account.passwordAnswer.length === 0}
                            onChange={handleOnChange}
                            onBlur={handleOnBlur}
                            className={`form-control ${!formFields?.passwordAnswer ? '' : formFields.passwordAnswer.isValid ? 'is-valid' : 'is-invalid'}`} />
                        <Form.Control.Feedback type="invalid">
                            {getError('passwordAnswer')}
                        </Form.Control.Feedback>
                    </Form.Group>

                    <div className="d-flex justify-content-end gap-1 pt-3 pb-2">
                        <Button bsPrefix='btn-custom' className="ms-auto" onClick={clearAccount} size="sm" ><span>{t("btn-clear", { ns: "common" })}</span></Button>
                        <Button key={isFormValid} bsPrefix='btn-custom' type="submit" size="sm" disabled={!isFormValid}><span>{mode === 'new' ? t("btn-create-account") : t("btn-save", { ns: "common" })} </span></Button>
                    </div>
                </Form>
            </div>
            <div>
                <Modal className='custom-alert' size='sm' show={showChangePassword} onHide={() => setShowChangePassword(false)} >
                    <Modal.Body className='custom-alert-body-no-footer pb-2'>
                        <Form key={refresh} onSubmit={handleChangePasswordSubmit} className='w-100' noValidate>
                            {renderPasswordFields()}
                            <div className="d-flex justify-content-end gap-3 pt-2 pb-2">
                                <Button bsPrefix='btn-custom' size='sm' onClick={() => setShowChangePassword(false)}><span>{t("btn-close", { ns: "common" })}</span></Button>
                                <Button bsPrefix='btn-custom' size='sm' onClick={handleChangePasswordSubmit} disabled={!isChangePaswordFormValid}><span>{t("btn-save", { ns: "common" })}</span></Button>
                            </div>
                        </Form>
                    </Modal.Body>
                </Modal >
            </div>
        </div>

    )
}