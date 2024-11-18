import { useContext } from "react";
import { CookieContext } from "../context/CookieProvider"
import { Button, Modal } from "react-bootstrap";
import { t } from "i18next";
import { useTranslation } from "react-i18next";

export default function EssentialCookiesAllowed() {

    const { cookieCtx, setCookieCtx } = useContext(CookieContext);
    const { t } = useTranslation(['common']);

    return (
        <>
            {!cookieCtx &&
                <Modal className="p-0" size='sm' show={true} onHide={() => setCookieCtx(true)}>
                    <Modal.Body className="p-2">
                        {/* <span className='title text-center'>
                    {t('delete-item-alert', { item: camelCase(itemToDelete?.product.code) })}
                </span> */}

                        <p> {t('essentail-cookie-msg')}</p>
                        <Button bsPrefix='btn-custom' style={{ width: "100%" }} size='sm' onClick={() => setCookieCtx(true)}><span>{t("btn-got-it", { ns: "common" })}</span></Button>
                    </Modal.Body>

                </Modal >
            }
        </>

    )
}