import React, { useState, useEffect, useContext } from 'react';
import { useParams } from 'react-router';
import { getPantry, getPantryItems, postPantryConsume } from '../services/apis/mypantry/fetch/requests/PantryRequests.js';
import Button from 'react-bootstrap/Button';
import Stack from 'react-bootstrap/Stack';
import Table from 'react-bootstrap/Table';
import Image from 'react-bootstrap/Image';
import food from '../assets/images/healthy-food.png'
import VariantType from '../components/VariantType.js';
import { AlertContext } from '../services/context/AppContext.js';
import Form from 'react-bootstrap/Form';
import NumericField from '../components/NumericField.js'
import { camelCase } from '../services/Utils.js';

export default function Consume() {

  let { id } = useParams();

  const [pantry, setPantry] = useState({});
  const [pantryItems, setPantryItems] = useState([]);
  const [filteredItems, setFilteredItems] = useState([]);
  const [consumedItems, setConsumedItems] = useState([]);

  const [searchText, setSearchText] = useState("");
  const [isPantryEmpty, setIsPantryEmpty] = useState(true);

  const [isLoading, setIsLoading] = useState(true);
  const [reload, setReload] = useState(false);
  const { alert, setAlert } = useContext(AlertContext);

  useEffect(() => {
    fetchPantryData();
  }, [])

  useEffect(() => {
    filter(searchText);
    loadConsumedItems(pantryItems);
    setReload(!reload);
  }, [pantryItems])

  async function fetchPantryData() {
    try {
      setIsLoading(true);
      let res = await getPantry(id);
      setPantry(res);

      res = await getPantryItems(res.id);
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

  async function fetchPantryConsume() {
    try {
      setIsLoading(true);
      const res = await postPantryConsume(pantry.id, consumedItems);
      setPantryItems(res);

      showAlert(VariantType.SUCCESS, "Pantry updated successfully!");
    } catch (error) {
      showAlert(VariantType.DANGER, error.message);
    } finally {
      setIsLoading(false);
    }
  }

  function loadConsumedItems(data) {
    let emptyPantry = true;
    let copy = [];

    data.forEach((item) => {
      copy = [...copy,
      {
        pantryId: item.pantryId,
        productId: item.productId,
        qty: 0
      }]
      if (emptyPantry && item.currentQty > 0) { emptyPantry = false }
    })
    setIsPantryEmpty(emptyPantry)
    return setConsumedItems(copy);
  }

  function showAlert(type, message) {
    setAlert({
      show: true,
      type: type,
      message: message
    })
  }

  function handleSave() {
    fetchPantryConsume();
  }

  function handleClear() {
    loadConsumedItems(consumedItems);
  }

  function getConsumedItem(pantryId, productId) {
    return consumedItems.find(item => item.pantryId === pantryId && item.productId === productId);
  }

  function updateConsumedItem(item) {
    const array = consumedItems.map((c) => {
      return (c.pantryId === item.pantryId && c.productId === item.productId) ?
        c = { ...c, qty: c.qty } : c;
    })
    setConsumedItems(array);
  }

  function renderItem(item) {

    let consumedItem = getConsumedItem(item.pantryId, item.productId);

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

  return (
    <Stack gap={3} hidden={isNull(pantryItems) || pantryItems.length === 0}>
      <div>
      </div>
      <div>
        <Stack direction="horizontal" gap={2} className='d-flex'>
          <div className="me-auto"><h6 className="text-start fs-6 lh-lg title">Consume</h6></div>
          <Button bsPrefix="btn-custom" size="sm" onClick={handleClear}>Clear</Button>
          <Button bsPrefix="btn-custom" size="sm" onClick={handleSave} >Save</Button>
        </Stack>
      </div>
      <div>
        <Form.Control size="sm" type="text" id="search" className="form-control mb-1" placeholder="Seacrh for items here" value={searchText} onChange={(e) => filter(e.target.value)} />
        <Table hover>
          <tbody>
            <tr key="0:0" className="align-middle">
              <th><h6 className="title"> Code/Desc.</h6></th>
              <th><h6 className="title">Current</h6></th>
              <th><h6 className="title d-none d-md-block">Prov.</h6></th>
              <th><h6 className="title d-none d-md-block">Prov. on</h6></th>
              <th><h6 className="title">Consume</h6></th>
            </tr>
            {renderItems()}
          </tbody>
        </Table>
        {isLoading ? <h6>Loading...</h6> : <span />}
      </div>
      <Stack direction="horizontal" gap={2} className="d-flex justify-content-end">
        <Button bsPrefix="btn-custom" size="sm" onClick={handleClear}>Clear</Button>
        <Button bsPrefix="btn-custom" size="sm" onClick={handleSave}>Save</Button>
      </Stack>
    </Stack>
  )
}