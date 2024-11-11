import { Button, Card, Col, Collapse, Form, FormCheck, Image, Row } from "react-bootstrap";
import food from '../assets/images/healthy-food.png'
import { useTranslation } from 'react-i18next';
import { useEffect, useState } from "react";
import i18n from 'i18next';
import { BsArrow90DegRight } from "react-icons/bs";
import { useLoading } from "../hooks/useGlobalLoading";

function NewPantryReviewWizard({ pantry, productList, setFinalProductList, expandAll, setExpandAll, analysePantry, setAnalysePantry }) {

    const { t } = useTranslation(['pantry', 'categories', 'common']);
    const [categories, setCategories] = useState(() => { return populateCategories() });
    const [finalList, setFinalList] = useState(() => { return populateFinalList() });

    useEffect(() => {
        setFinalProductList(finalList);
    }, [finalList])

    useEffect(() => {
        updateCodes();
    }, [i18n.language])

    function populateFinalList() {
        const list = [];

        if (productList) {
            const filteredList = productList.filter(p => p.selected); //only the selected items

            filteredList.forEach(p =>
                list.push(
                    {
                        code: getCode(p),
                        size: p.size,
                        category: p.category,
                        idealQty: p.idealQty,
                        currentQty: p.currentQty
                    })
            );
        }

        return list;
    }

    function populateCategories() {
        var list = [{}];

        if (productList) {
            let category = "";
            const filteredList = productList.filter(p => p.selected); //only the selected items

            filteredList.forEach((i) => {
                if (category !== i.category) {

                    category = i.category;
                    list = [...list,
                    {
                        id: i.category,
                        isOpen: true,
                    }
                    ]
                }
            });
        }

        return list;
    }

    function handleExpansion(id) {

        var newList = categories.map((c) => {
            return (c.id === id) ?
                c = { ...c, isOpen: !c.isOpen } : c;
        });

        setCategories(newList);
    }

    function getOpen(id) {
        const found = categories.find((c) => c.id === id);
        var isOpen = found ? found.isOpen : false;

        return isOpen;
    }

    function handleExpandCollapseAll(expand) {
        var newList = categories.map((c) => {
            return (c = { ...c, isOpen: expand });
        });

        setCategories(newList);
        setExpandAll(expand)
    }

    function updateCodes() {
        var newList = finalList.map(i => {
            return (i = { ...i, code: getChangeCode(i) }) // <<<<< there is no translation here
        });
        setFinalList(newList);
    }

    function getCode(item) {
        var code = "";
        switch (i18n.language) {
            case "en-GB":
                code = item.product_en;
                break;
            case "pt-BR":
                code = item.product_pt;
                break;
            default:
                code = item.product_en;
                break;
        }
        return code;
    }

    function getChangeCode(item) {
        var code = item.code;
        const found = productList.find((i) => i.product_en === item.code || i.product_pt === item.code)
        if (found) {
            switch (i18n.language) {
                case "en-GB":
                    code = found.product_en;
                    break;
                case "pt-BR":
                    code = found.product_pt;
                    break;
                default:
                    code = found.product_en;
                    break;
            }
        }
        return code;
    }

    function renderCards() {
        var elements = [];
        var index = 0;
        if (finalList?.length > 0) {
            var found = finalList.at(index);
            var category = found ? found.category : "";

            while (category !== "" && index < finalList.length) {
                var filteredCategory = finalList.filter(i => i.category === category);

                elements.push(renderCategoryCard(category, filteredCategory))

                index = index + filteredCategory.length;
                found = finalList.at(index);
                category = found ? found.category : "";
            }
        }
        return elements;
    }

    function renderCategoryCard(category, filteredCategory) {
        return (
            <div className="flex-column pt-2 pb-2" key={category}>
                <div className="category" onClick={() => handleExpansion(category)}>
                    <Button variant="link" aria-controls={category} onClick={() => handleExpansion(category)}><BsArrow90DegRight className='small-icon' /></Button>
                    <h6 className='title'>{!category || category === "" ? t("other") : t(category, { ns: 'categories' })}</h6>
                </div>
                <Collapse in={getOpen(category)}>
                    <Row xs={1} md={2} lg={3} xl={4} className='m-0'>
                        {filteredCategory?.map(item => renderCard(item))}
                    </Row>
                </Collapse>
            </div>
        )
    }

    function renderCard(item) {
        if (item) {
            return (
                <Col key={item.code} className="d-flex flex-column g-2">
                    <Card className="card1 flex-fill">
                        <Card.Body className="d-flex  flex-column h-100">

                            <div className="d-flex justify-content-between" >
                                <div className='d-flex gap-2'>
                                    <Image src={food} width={20} height={20} rounded />
                                    <Card.Title as="h6" className='mb-0'><span className='text-wrap'>{item.code}</span></Card.Title>
                                </div>
                            </div>

                            <div className='d-flex gap-2 mb-2'>
                                <span className="small">{item.size}</span>
                            </div>

                            <div className="d-flex justify-content-evenly align-items-end mt-auto">
                                <div className="d-flex flex-column align-items-center">
                                    <h6 className='simple-title'>{t('ideal', { ns: 'common' })}</h6>
                                    <span>{item?.idealQty}</span>
                                </div>
                                <div className="d-flex flex-column align-items-center ">
                                    <h6 className='simple-title'>{t('current', { ns: 'common' })}</h6>
                                    <span>{item?.currentQty}</span>
                                </div>

                            </div>
                        </Card.Body>
                    </Card>
                </Col>
            )
        }
    }

    return (
        <div>
            <h5 className="title pt-2 pb-2">{t('pantry-title')}</h5>
            <Col>
                <span className="title">{t('name', { ns: 'common' })}: </span>
                <span>{pantry.name}</span>
            </Col>
            <Col>
                <span className="title">{t('type')}: </span>
                <span>{pantry.type.label}</span>
            </Col>
            <Col>
                <span className="title">{t('account-group', { ns: 'common' })}: </span>
                <span>{pantry.accountGroup.label}</span>
            </Col>

            <Col className="pt-4">
                <FormCheck label={t('analyse-pantry')}
                    defaultChecked={analysePantry}
                    onChange={() => setAnalysePantry(!analysePantry)} />
            </Col>

            <h5 className="title pt-5 pb-2">{t('wizard-product-details')}</h5>

            {finalList?.length > 0 ?
                <>
                    <div className="d-flex justify-content-end pe-1">
                        <FormCheck label={t('label-switch-expandAll')}
                            className='form-switch'
                            defaultChecked={expandAll}
                            onChange={() => handleExpandCollapseAll(!expandAll)} />
                    </div>
                    {renderCards()}
                </>
                :
                <h6 className="title pt-1 pb-4">{t('wizard-info-empty-pantry')}</h6>
            }
        </div>

    )
}

export default NewPantryReviewWizard;