import React, { useState, useEffect, useContext } from 'react';
import { useParams } from 'react-router';
import { getPantry, getPantryItems, postPantryConsume } from '../../services/apis/mypantry/fetch/requests/PantryRequests.js';
import { ListGroup } from 'react-bootstrap';
import Button from 'react-bootstrap/Button';
import Stack from 'react-bootstrap/Stack';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';
import Image from 'react-bootstrap/Image';
import food from '../../images/healthy-food.png'
import VariantType from '../components/VariantType.js';
import { AlertContext } from '../../services/context/AppContext.js';
import Form from 'react-bootstrap/Form';
import NumericField from '../components/NumericField.js'


export default function Consume() {

  let { id } = useParams();

  const [pantry, setPantry] = useState({});
  const [pantryItems, setPantryItems] = useState([]);
  const [filteredItems, setFilteredItems] = useState([]);
  const [consumedItems, setConsumedItems] = useState([]);

  const [searchText, setSearchText] = useState("");
  const [isPantryEmpty, setIsPantryEmpty] = useState(true);

  const [isLoading, setIsLoading] = useState(true)
  const { alert, setAlert } = useContext(AlertContext);

  useEffect(() => {
    fetchPantryData();
  }, [])

  useEffect(() => {
    filter(searchText);
  }, [pantryItems])

  async function fetchPantryData() {
    try {
      setIsLoading(true);
      let res = await getPantry(id);
      setPantry(res);

      res = await getPantryItems(res.id);
      if (res != null && Object.keys(res).length === 0) return showAlert(VariantType.INFO, "Pantry is empty. There is no item to consume.");
      setPantryItems(res);
      loadConsumedItems(res);
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
      loadConsumedItems(res);
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
    showAlert(VariantType.SUCCESS, "Pantry updated successfully!");
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
      <ListGroup.Item variant="primary" key={item.productId}>
        <Row>
          <Col xs={5}>
            <Stack direction="horizontal" gap={3}>
              <div><Image src={food} width={20} height={20} rounded /></div>
              <div><span>{item.product.code}</span></div>
            </Stack>
          </Col>
          <Col><span>Current</span></Col>
          <Col className='d-none d-md-block'><span>Provisioned</span></Col>
          <Col className='d-none d-md-block'><span>Prov. on</span></Col>
          <Col><span>Consumed</span></Col>
        </Row>
        <Row>
          <Col xs={5}>
            <p className='pt-1 d-none d-md-block'>
              {item.product.description} - {item.product.size}
            </p>
          </Col>
          <Col><span>{item.currentQty}</span></Col>
          <Col className='d-none d-md-block'><span>{item.provisionedQty}</span></Col>
          <Col className='d-none d-md-block'><span>{item.lastProvisioning}</span></Col>
          <Col>
            <NumericField object={consumedItem} attribute="qty" onValueChange={updateConsumedItem} disabled={isPantryEmpty} />
          </Col>
        </Row>
      </ListGroup.Item>
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
      <Stack direction="horizontal" gap={2} className="d-flex justify-content-end">
        <div><Button variant="primary" size="sm" onClick={handleClear}>Clear</Button></div>
        <div><Button variant="primary" size="sm" onClick={handleSave} >Save</Button></div>
      </Stack>
      <div>
        <Form.Control size="sm" type="text" id="search" className="form-control mb-1" placeholder="Seacrh for items here" value={searchText} onChange={(e) => filter(e.target.value)} />
        <ListGroup>
          {isLoading ? <h6>Loading...</h6> : renderItems()}
        </ListGroup>
      </div>
      <Stack direction="horizontal" gap={2} className="d-flex justify-content-end">
        <div><Button variant="primary" size="sm" onClick={handleClear}>Clear</Button></div>
        <div><Button variant="primary" size="sm" onClick={handleSave}>Save</Button></div>
      </Stack>
    </Stack>
  )
}