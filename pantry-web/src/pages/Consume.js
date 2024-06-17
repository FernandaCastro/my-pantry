import React, { useState, useEffect } from 'react';
import { getPantryItemsConsume, postPantryConsumeItem } from '../services/apis/mypantry/requests/PantryRequests.js';
import Stack from 'react-bootstrap/Stack';
import Image from 'react-bootstrap/Image';
import food from '../assets/images/healthy-food.png'
import Form from 'react-bootstrap/Form';
import { camelCase } from '../services/Utils.js';
import VariantType from '../components/VariantType.js';
import useAlert from '../hooks/useAlert.js';
import PantrySelect from '../components/PantrySelect.js'
import { Card, Col, FormCheck, Row } from "react-bootstrap";
import { BsChevronDown } from "react-icons/bs";
import Collapse from 'react-bootstrap/Collapse';
import { useTranslation } from 'react-i18next';
import CurrentQuantityField from '../components/CurrentQuantityField.js'
import iconConsume from '../assets/images/cook-gradient.png';

export default function Consume() {

  const { t } = useTranslation(['consume', 'common']);

  const [selectedPantries, setSelectedPantries] = useState([]);
  const [pantryItems, setPantryItems] = useState([]);
  const [filteredItems, setFilteredItems] = useState([]);

  const [searchText, setSearchText] = useState("");
  const [isPantryEmpty, setIsPantryEmpty] = useState(true);

  const [reload, setReload] = useState(false);
  const { showAlert } = useAlert();
  const [expand, setExpand] = useState(false);
  const [showPantries, setShowPantries] = useState(false);
  const [showPantryCol, setShowPantryCol] = useState(false);

  useEffect(() => {
    if (selectedPantries && selectedPantries.length > 0) {
      fetchPantryItem();
    } else {
      setPantryItems([]);
    }
  }, [selectedPantries])

  useEffect(() => {
    filter(searchText);
    checkPantryEmpty();
    setReload(!reload);
  }, [pantryItems])

  async function fetchPantryItem() {
    try {
      const res = await getPantryItemsConsume(selectedPantries);
      setPantryItems(res);

      if (res != null && Object.keys(res).length === 0) {
        return showAlert(VariantType.INFO, t('fetch-pantry-empty-alert'));
      }

    } catch (error) {
      showAlert(VariantType.DANGER, error.message);
    }
  }

  async function fetchSaveConsumeItem(consumedItem) {
    try {
      const res = await postPantryConsumeItem(consumedItem);
      showAlert(VariantType.SUCCESS, t('consume-item-success'));
    } catch (error) {
      showAlert(VariantType.DANGER, error.message);
    }
  }

  function checkPantryEmpty() {
    const emptyPantry = pantryItems.every(item => item.currentQtd === 0);
    setIsPantryEmpty(emptyPantry);
  }

  async function updateConsumedItem(item) {
    const consumedItem = { pantryId: item.pantry.id, productId: item.product.id, qty: 1 }
    await fetchSaveConsumeItem(consumedItem);
    fetchPantryItem();
  }

  function renderCards() {
    return filteredItems.map(item => renderCard(item))
  }

  function renderCard(item) {
    return (
      <Col key={item.pantry.id + "-" + item.product.id} className="d-flex flex-column g-2">
        <Card className="card1 flex-fill">
          <Card.Body className="d-flex  flex-column h-100">

            <div className="d-flex justify-content-between" >
              <div className='d-flex gap-2'>
                <Image src={food} width={20} height={20} rounded />
                <Card.Title as="h6" className='mb-0'><span className='text-wrap'>{camelCase(item.product.code)}</span></Card.Title>
              </div>
              <CurrentQuantityField key={reload} object={item} attribute="currentQty" onValueChange={updateConsumedItem} disabled={isPantryEmpty} />
            </div>

            <div className="d-flex justify-content-between " >
              <div className='d-flex flex-column'>
                <span className="mt-0 small" hidden={!expand}>
                  {item.product.description} {item.product.size}
                </span>
                <span className='text-wrap small' hidden={!showPantryCol}>{item.pantry.name}</span>
              </div>

            </div>

            <div className="d-flex gap-3 mt-auto">
              <span className="small" hidden={item.lastProvisioning === null}>{t('provisioned', { ns: 'common' })}: {item.provisionedQty}</span>
              <span className="small" hidden={item.lastProvisioning === null}>{t('provisioned-on', { ns: 'common' })}: {item.lastProvisioning ? t('datetime', { ns: "common", date: new Date(item.lastProvisioning) }) : ""}</span>
            </div>

          </Card.Body>
        </Card>
      </Col>
    )
  }

  function filter(text) {
    if (text && text.length > 0) {
      setFilteredItems(pantryItems.filter(item => item.product.code.toUpperCase().includes(text.toUpperCase())));
    } else {
      setFilteredItems(pantryItems);
    }
    setSearchText(text);
  }

  function handleSelectedPantries(list) {
    setSelectedPantries(list);
  }

  return (
    <Stack gap={3}>
      <div className='mt-4'>
        <div className='d-flex justify-content-start align-items-end' onClick={() => setShowPantries(!showPantries)}>
          <h6 className="title">{t('consume-title')}</h6>
          <BsChevronDown className='small-icon  ms-3' />
          <Image src={iconConsume} width={40} height={40} className='ms-auto ms-3 me-3' />
        </div>

        <Collapse in={showPantries} >
          <div className='mt-3'><PantrySelect handleSelectedPantryList={handleSelectedPantries} permission='consume_pantry' /></div>
        </Collapse>
      </div>

      <div className='d-flex justify-content-evenly'>
        <FormCheck label={t('tooltip-switch-product-detail', { ns: 'common' })}
          className='form-switch'
          defaultChecked={expand}
          onChange={() => setExpand(!expand)} />

        <FormCheck label={t('tooltip-switch-pantry', { ns: 'common' })}
          className='d-block form-switch'
          defaultChecked={showPantryCol}
          onChange={() => setShowPantryCol(!showPantryCol)}
        />
      </div>
      <div>
        <Form.Control size="sm" type="text" id="search" className="form-control mb-1" placeholder={t('placeholder-search-items', { ns: 'common' })} value={searchText} onChange={(e) => filter(e.target.value)} />
        <Row xs={1} md={2} lg={3} xl={4} className='m-0'>
          {renderCards()}
        </Row>
      </div>
    </Stack >
  )
}