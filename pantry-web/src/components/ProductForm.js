import Button from 'react-bootstrap/Button';
import Stack from 'react-bootstrap/Stack';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';
import Form from 'react-bootstrap/Form';
import Select from './Select';
import { useState, useEffect } from 'react';
import { getProperty } from '../services/apis/mypantry/requests/PurchaseRequests.js';
import { useTranslation } from 'react-i18next';

export default function ProductForm({ product, accountGroupId, categories, accountGroupOptions, handleSave }) {

    const { t } = useTranslation(['product', 'common', 'categories']);

    const [categoryOption, setCategoryOption] = useState({ value: product.category, label: t(product.category, {ns: 'categories'}) });
    const [categoryList, setCategoryList] = useState([{}]);
    const [accountGroupOption, setAccountGroupOption] = useState({ value: 0, label: "" });

    useEffect(() => {
        if (!categories || Object.keys(categories).length === 0)
            fetchCategories();
        else
            setCategoryList(categories);
    }, []);

    useEffect(() => {
        const searchGroupId = (Object.keys(product).length > 0 && product.id > 0) ? product.accountGroup.id : accountGroupId;

        if (searchGroupId && searchGroupId > 0) {
            const found = accountGroupOptions.find(a => a.value === searchGroupId);
            setAccountGroupOption(() => found);
        }

    }, [product.id, accountGroupId]);

    async function fetchCategories() {
        try {
            const res = await getProperty("product.categories");

            const resCategories = JSON.parse(res.propertyValue);
            var list = [];
            resCategories.forEach(category => {
                list = [...list,
                {
                    value: category,
                    label: t(category, {ns: 'categories'})
                }]
            });

            setCategoryList(list);
        } catch (error) {
            console.error(t('fetch-categories-error') + error.message);
        }
    }

    function handleSubmit(e) {
        // Prevent the browser from reloading the page
        e.preventDefault();

        // Read the form data
        const form = e.target;
        const formData = new FormData(form);

        let formJson = Object.fromEntries(formData.entries());
        formJson = {
            ...formJson,
            category: categoryOption.value,
            accountGroup: { id: accountGroupOption.value }
        }

        handleSave(formJson);

        console.log(formJson);
    }

    return (
        <Form onSubmit={handleSubmit} className="ms-2">
            <Row>
                <Form.Group as={Col} className="mb-2" controlId="formAccountGroups" size="sm">
                    <Form.Label size="sm" className="title mb-1">{t('account-group', { ns: 'common' })}</Form.Label>
                    <Select key={accountGroupOption.value} name="accountGroup"
                        defaultValue={accountGroupOption}
                        options={accountGroupOptions}
                        onChange={setAccountGroupOption} />
                </Form.Group>
            </Row>
            <Row>
                <Form.Group className="w-25" controlId="formId">
                    <Form.Label size="sm" className="title mb-1">{t('id', { ns: 'common' })}</Form.Label>
                    <Form.Control size="sm" className="mb-1 input-custom" type="text" name="id" defaultValue={product.id} disabled />
                </Form.Group>
                <Form.Group as={Col} controlId="formCode">
                    <Form.Label size="sm" className="title mb-1">{t('code')}</Form.Label>
                    <Form.Control size="sm" className="mb-1 input-custom" type="text" name="code" placeholder={t('placeholder-code')} defaultValue={product.code} />
                </Form.Group>
            </Row>
            <Row>
                <Form.Group key={categoryOption.value} className="w-25" controlId="formSize">
                    <Form.Label size="sm" className="title mb-1">{t('size')}</Form.Label>
                    <Form.Control size="sm" className="mb-1 input-custom" type="text" name="size" placeholder={t('placeholder-size')}  defaultValue={product.size} />
                </Form.Group>
                <Form.Group as={Col} className="mb-2" controlId="formCategory" size="sm">
                    <Form.Label size="sm" className="title mb-1">{t('category')}</Form.Label>
                    <Select key={categoryList.length} name="category" defaultValue={categoryOption} options={categoryList}
                        onChange={setCategoryOption} />
                </Form.Group>
            </Row>
            <Row>
                <Form.Group as={Col} className="mb-2" controlId="formDescription" size="sm">
                    <Form.Label size="sm" className="title mb-1">{t('description')}</Form.Label>
                    <Form.Control size="sm" className="mb-1 input-custom" type="text" name="description" placeholder={t('placeholder-description')}  defaultValue={product.description} />
                </Form.Group>
            </Row>
            <Row>
                <Stack direction="horizontal" gap={2} className="mb-3 d-flex justify-content-end">
                    <div><Button bsPrefix='btn-custom' type="reset" size="sm"><span className="gradient-text">{t('btn-clear', { ns: 'common' })}</span></Button></div>
                    <div><Button bsPrefix='btn-custom' type="submit" size="sm"><span className="gradient-text">{t('btn-save', { ns: 'common' })}</span></Button></div>
                </Stack>
            </Row>
        </Form>
    )
}