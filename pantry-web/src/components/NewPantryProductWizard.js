import { Button, Card, Col, Collapse, Form, FormCheck, Image, Row } from "react-bootstrap";
import food from '../assets/images/healthy-food.png'
import { useTranslation } from 'react-i18next';
import { useEffect, useState } from "react";
import i18n from 'i18next';
import WizardNumericField from "./WizardNumericField";
import { BsArrow90DegRight } from "react-icons/bs";
import productsJson from '../assets/products/product-list.json';

function NewPantryProductWizard({ pantrySize, productList, setProductList, selectAll, setSelectAll, expandAll, setExpandAll }) {

    const { t } = useTranslation(['pantry', 'categories']);
    const [suggestedProducts] = useState(productsJson);
    const [categories, setCategories] = useState([]);

    useEffect(() => {
        if (suggestedProducts) {
            populateCategories();

            if (productList?.length === 0) {
                //currentQty: 0 and selected: true
                const newArray = suggestedProducts.map(item => {
                    return { ...item, idealQty: getQty(item), selected: selectAll, currentQty: 0 };
                });
                setProductList(newArray);
            }
        }
        //handleExpandCollapseAll(expandAll);
    }, [])

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

    function populateCategories() {
        let list = [];
        let category = "";

        suggestedProducts.forEach((i) => {
            if (category !== i.category) {

                category = i.category;
                var found = categories.find(c => c.id === category);

                list = [...list,
                {
                    id: i.category,
                    isOpen: found != null ? found.isOpen : expandAll,
                }
                ]
            }
        });
        setCategories(list);
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

    function getQty(item) {
        var qty = 0;
        switch (pantrySize) {
            case "solo":
                qty = item.qty_solo;
                break;
            case "couple":
                qty = item.qty_couple;
                break;
            case "fam3":
                qty = item.qty_fam3;
                break;
            case "fam4":
                qty = item.qty_fam4;
                break;
            default:
                qty = item.qty_couple;
                break;
        }
        return qty;
    }

    function handleIdealQtyChanged(item, value) {
        let index = productList.findIndex(i => i.product_en === item.product_en);
        if (index !== -1) {
            productList[index].idealQty = value;
        }
    }

    function handleCurrentQtyChanged(item, value) {
        let index = productList.findIndex(i => i.product_en === item.product_en);
        if (index !== -1) {
            productList[index].currentQty = value;
        }
    }

    function handleSelected(item, value) {
        let index = productList.findIndex(i => i.product_en === item.product_en);
        if (index !== -1) {
            productList[index].selected = value;
        }
    }

    function handleSelectAll() {
        const newArray = productList.map(item => {
            return { ...item, selected: !selectAll };
        });
        setProductList(newArray);
        setSelectAll(!selectAll);
    }

    function renderCards() {
        var elements = [];

        var index = 0;
        if (productList?.length > 0) {

            var found = productList.at(index);
            var category = found ? found.category : "";

            while (category !== "" && index < productList.length) {
                var filteredCategory = productList.filter(i => i.category === category);

                elements.push(renderCategoryCard(category, filteredCategory))

                index = index + filteredCategory.length;
                found = productList.at(index);
                category = found ? found.category : "";
            }
        }
        return elements;
    }

    function renderCategoryCard(category, filteredCategory) {
        return (
            <div key={category} className="flex-column pt-2 pb-2">
                <div className="category" onClick={() => handleExpansion(category)}>
                    <Button variant="link" aria-controls={category} onClick={() => handleExpansion(category)}><BsArrow90DegRight className='small-icon' /></Button>
                    <h6 className='title'>{!category || category === "" ? t("other") : t(category, { ns: 'categories' })}</h6>
                </div>
                <Collapse in={getOpen(category)} >
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
                <Col key={item.product_en} className="d-flex flex-column g-2">
                    <Card className="card1 flex-fill">
                        <Card.Body className="d-flex  flex-column h-100">

                            <div className="d-flex justify-content-between" >
                                <div className='d-flex gap-2'>
                                    <Image src={food} width={20} height={20} rounded />
                                    <Card.Title as="h6" className='mb-0'><span className='text-wrap'>{getCode(item)}</span></Card.Title>
                                </div>
                                <Form.Check key={item?.selected} defaultChecked={item?.selected} variant="link" className='pt-0 pb-0 pe-0' onChange={(e) => handleSelected(item, e.currentTarget.checked)} />
                            </div>

                            <div className='d-flex gap-2 mb-2'>
                                <span className="small">{item.size}</span>
                            </div>

                            <div className="d-flex justify-content-evenly align-items-end mt-auto">
                                <div className="d-flex flex-column align-items-center">
                                    <h6 className='simple-title'>{t('ideal', { ns: 'common' })}</h6>
                                    <WizardNumericField initialValue={item?.idealQty ? item.idealQty : getQty(item)} item={item} onValueChange={handleIdealQtyChanged} />
                                </div>
                                <div className="d-flex flex-column align-items-center ">
                                    <h6 className='simple-title'>{t('current', { ns: 'common' })}</h6>
                                    <WizardNumericField initialValue={item?.currentQty} item={item} onValueChange={handleCurrentQtyChanged} />
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
            <div className="d-flex justify-content-end pe-3 gap-4">
                <FormCheck label={t('label-switch-expandAll')}
                    className='form-switch'
                    defaultChecked={expandAll}
                    onChange={() => handleExpandCollapseAll(!expandAll)} />

                <FormCheck label={t('label-switch-selectAll')}
                    className='form-switch'
                    defaultChecked={selectAll}
                    onChange={() => handleSelectAll()} />
            </div>
            {renderCards()}
        </div>
    )
}

export default NewPantryProductWizard;