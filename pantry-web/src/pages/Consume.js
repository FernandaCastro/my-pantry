import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router';
import { getPantryItemsConsume, postPantryConsumeItem } from '../services/apis/mypantry/requests/PantryRequests.js';
import Stack from 'react-bootstrap/Stack';
import Table from 'react-bootstrap/Table';
import Image from 'react-bootstrap/Image';
import food from '../assets/images/healthy-food.png'
import Form from 'react-bootstrap/Form';
import NumericField from '../components/NumericField.js'
import { camelCase } from '../services/Utils.js';
import VariantType from '../components/VariantType.js';
import useAlert from '../hooks/useAlert.js';
import PantrySelect from '../components/PantrySelect.js'
import { FormCheck } from "react-bootstrap";
import OverlayTrigger from 'react-bootstrap/OverlayTrigger';
import Tooltip from 'react-bootstrap/Tooltip';
import { BsChevronDown } from "react-icons/bs";
import Collapse from 'react-bootstrap/Collapse';

export default function Consume() {

  const { t } = useTranslation(['consume', 'common']);

  const [selectedPantries, setSelectedPantries] = useState([]);
  const [pantryItems, setPantryItems] = useState([]);
  const [filteredItems, setFilteredItems] = useState([]);
  const [consumedItems, setConsumedItems] = useState([]);

  const [searchText, setSearchText] = useState("");
  const [isPantryEmpty, setIsPantryEmpty] = useState(true);

  const [isLoading, setIsLoading] = useState(true);
  const [reload, setReload] = useState(false);
  const { showAlert } = useAlert();
  const [expand, setExpand] = useState(false);
  const [showPantries, setShowPantries] = useState(true);
  const [showPantryCol, setShowPantryCol] = useState(false);

  useEffect(() => {
    if (selectedPantries && selectedPantries.length > 0)
      fetchPantryItem();
    else {
      setPantryItems([]);
    }
  }, [selectedPantries])

  useEffect(() => {
    filter(searchText);
    loadConsumedItems(pantryItems);
    checkPantryEmpty();
    setReload(!reload);
  }, [pantryItems])

  async function fetchPantryItem() {
    try {
      const res = await getPantryItemsConsume(selectedPantries);
      if (res != null && Object.keys(res).length === 0) {
        return showAlert(VariantType.INFO, t('fetch-pantry-empty-alert'));
      }

      setPantryItems(res);
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

  function loadConsumedItems(pantryItemList) {
    let copy = [];
    pantryItemList.forEach((item) => {
      copy = [...copy,
      {
        pantryId: item.pantry.id,
        productId: item.product.id,
        qty: 0
      }]
    })
    setConsumedItems(copy);
  }

  function checkPantryEmpty() {
    const emptyPantry = pantryItems.every(item => item.currentQtd === 0);
    setIsPantryEmpty(emptyPantry);
  }

  function getConsumedItem(pantryId, productId) {
    return consumedItems.find(item => item.pantryId === pantryId && item.productId === productId);
  }

  async function updateConsumedItem(item) {

    const consumedItem = { pantryId: item.pantryId, productId: item.productId, qty: item.qty }
    await fetchSaveConsumeItem(consumedItem);
    fetchPantryItem();
  }

  function renderItem(item) {

    let consumedItem = getConsumedItem(item.pantry.id, item.product.id);

    return (
      <tr key={item.productId} className="align-middle">
        <td >
          <Stack direction="horizontal" gap={2}>
            <Image src={food} width={20} height={20} rounded />
            <span className='text-wrap'>{camelCase(item.product.code)}</span>
          </Stack>
          <div style={{ display: expand ? 'block' : 'none' }}>
            <span hidden={item.product.description === ''}>
              {item.product.description}  {item.product.size}
            </span>
          </div>
        </td>
        <td hidden={!showPantryCol}><span className='text-left text-wrap'>{item.pantry.name}</span></td>
        <td><span className='text-left'>{item.currentQty}</span></td>
        <td><span className='d-none d-md-block text-left ps-5'>{item.provisionedQty}</span></td>
        <td><span className='d-none d-md-block'>{t('datetime', { ns: "common", date: new Date(item.lastProvisioning) })}</span></td>
        <td >
          <div className='d-flex justify-content-end me-2'>
            <NumericField key={reload} object={consumedItem} attribute="qty" onValueChange={updateConsumedItem} disabled={isPantryEmpty} />
          </div>
        </td>

      </tr>
    )
  }

  function renderItems() {
    return filteredItems.map(item => renderItem(item))
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
    <Stack gap={3} >
      <div>
      </div>
      <div>
        <div className='d-flex justify-content-start align-items-center gap-2' onClick={() => setShowPantries(!showPantries)}>
          <h6 className="text-start fs-6 lh-lg title">{t('consume-title')}</h6>
          <BsChevronDown className='icon' />
        </div>

        <Collapse in={showPantries} >
          <div><PantrySelect handleSelectedPantryList={handleSelectedPantries} permission='consume_pantry' /></div>
        </Collapse>
      </div>
      <div>
        <Form.Control size="sm" type="text" id="search" className="form-control mb-1" placeholder={t('placeholder-search-items', { ns: 'common' })} value={searchText} onChange={(e) => filter(e.target.value)} />
        <div className='scroll-consume'>
          <Table size='sm'>
            <thead>
              <tr className="align-middle">
                <th className='d-flex flex-row align-items-center'>
                  <OverlayTrigger
                    placement="bottom"
                    delay={{ show: 250, hide: 250 }}
                    overlay={
                      <Tooltip className="custom-tooltip">
                        {t('tooltip-switch-product-detail', { ns: 'common' })}
                      </Tooltip>
                    }
                  >
                    <FormCheck
                      className='form-switch'
                      defaultChecked={expand}
                      onChange={() => setExpand(!expand)} />
                  </OverlayTrigger>
                  <h6 className="title">{t('code-description', { ns: 'common' })}</h6>
                </th>
                <th hidden={!showPantryCol}><h6 className="title text-left">{t('pantry', { ns: 'common' })}</h6></th>
                <th><h6 className="title">{t('quantity', { ns: 'common' })}</h6></th>
                <th><h6 className="title ps-5 d-none d-md-block">{t('provisioned', { ns: 'common' })}</h6></th>
                <th><h6 className="title text-left d-none d-md-block">{t('provisioned-on', { ns: 'common' })}</h6></th>
                <th>
                  <div className='d-flex justify-content-end align-items-center gap-2 pe-2'>
                    <OverlayTrigger
                      placement="bottom"
                      delay={{ show: 250, hide: 250 }}
                      overlay={
                        <Tooltip className="custom-tooltip">
                          {t('tooltip-switch-pantry', { ns: 'common' })}
                        </Tooltip>
                      }
                    >
                      <FormCheck
                        className='d-block form-switch'
                        defaultChecked={showPantryCol}
                        onChange={() => setShowPantryCol(!showPantryCol)}
                      />
                    </OverlayTrigger>
                  </div>
                </th>
              </tr>
            </thead>
            <tbody>
              {renderItems()}
            </tbody>
          </Table>
        </div>
      </div>
    </Stack >
  )
}