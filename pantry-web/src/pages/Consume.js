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

export default function Consume() {

  let { id } = useParams();

  const [selectedPantries, setSelectedPantries] = useState([]);
  const [pantryItems, setPantryItems] = useState([]);
  const [filteredItems, setFilteredItems] = useState([]);
  const [consumedItems, setConsumedItems] = useState([]);

  const [searchText, setSearchText] = useState("");
  const [isPantryEmpty, setIsPantryEmpty] = useState(true);

  const [isLoading, setIsLoading] = useState(true);
  const [reload, setReload] = useState(false);
  const { showAlert } = useAlert();

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
      setIsLoading(true);

      const res = await getPantryItemsConsume(selectedPantries);
      if (res != null && Object.keys(res).length === 0) {
        return showAlert(VariantType.INFO, "Pantry is empty. There is no item to consume.");
      }

      setPantryItems(res);
    } catch (error) {
      showAlert(VariantType.DANGER, error.message);
    } finally {
      setIsLoading(false);
    }
  }

  async function fetchSaveConsumeItem(consumedItem) {
    try {
      setIsLoading(true);
      const res = await postPantryConsumeItem(consumedItem);
      showAlert(VariantType.SUCCESS, "Item consumed successfully!");
    } catch (error) {
      showAlert(VariantType.DANGER, error.message);
    } finally {
      setIsLoading(false);
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
        <td>
          <Stack direction="horizontal" gap={2}>
            <Image src={food} width={20} height={20} rounded />
            <span>{camelCase(item.product.code)}</span>
          </Stack>
          <span className='d-none d-md-block' hidden={item.product.description === ''}>
            {item.product.description}  {item.product.size}
          </span>
        </td>
        <td><span>{item.pantry.name}</span></td>
        <td><span>{item.currentQty}</span></td>
        <td><span className='d-none d-md-block align-center'>{item.provisionedQty}</span></td>
        <td><span className='d-none d-md-block'>{item.lastProvisioning}</span></td>
        <td><NumericField key={reload} object={consumedItem} attribute="qty" onValueChange={updateConsumedItem} disabled={isPantryEmpty} /></td>
      </tr>
    )
  }

  function isNull(object) {
    if (!object || (Object.keys(object).length === 0 && object.constructor === Object)) return true;
    return false;
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
    //hidden={isNull(pantryItems) || pantryItems.length === 0}
    <Stack gap={3} >
      <div>
      </div>
      <div>
        <div className="me-auto"><h6 className="text-start fs-6 lh-lg title">Consume Itens</h6></div>
      </div>
      <div><PantrySelect handleSelectedPantryList={handleSelectedPantries} /></div>
      <div>
        <Form.Control size="sm" type="text" id="search" className="form-control mb-1" placeholder="Seacrh for items here" value={searchText} onChange={(e) => filter(e.target.value)} />
        <div className='scroll-consume'>
          <Table>
            <thead>
              <tr key={0} className="align-middle">
                <th><h6 className="title"> Code/Desc.</h6></th>
                <th><h6 className="title">Pantry</h6></th>
                <th><h6 className="title">Current</h6></th>
                <th><h6 className="title d-none d-md-block">Prov.</h6></th>
                <th><h6 className="title d-none d-md-block">Prov. on</h6></th>
                <th><h6 className="title">Consume</h6></th>
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