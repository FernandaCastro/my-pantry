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
import { BsCaretDown, BsCaretUp } from "react-icons/bs";

export default function Consume() {

  let { id } = useParams();

  const [pantry, setPantry] = useState({});
  const [pantryItems, setPantryItems] = useState([]);
  const [isLoading, setIsLoading] = useState(true)

  const [consumedItems, setConsumedItems] = useState([]);
  const [isPantryEmpty, setIsPantryEmpty] = useState(true);
  const [hasConsumed, setHasConsumed] = useState(false);

  const { alert, setAlert } = useContext(AlertContext);

  useEffect(() => {
    setIsLoading(true);
    fetchPantryData();
    setIsLoading(false);
  }, [])

  async function fetchPantryData() {
    try {
      let res = await getPantry(id);
      setPantry(res);

      if (res != null && Object.keys(res).length > 0) { //Not null
        res = await getPantryItems(res.id);
        setPantryItems(res);

        loadConsumedItems(res);
      }
    } catch (error) {
      showAlert(VariantType.DANGER, error.message);
    }
  }

  async function fetchPantryConsume() {
    setIsLoading(true);
    try {
      const res = await postPantryConsume(pantry.id, consumedItems);
      setPantryItems(res);
      loadConsumedItems(res);
      setIsLoading(false);
    } catch (error) {
      showAlert(VariantType.DANGER, error.message);
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
      if (emptyPantry && item.currentQty > 0) {
        emptyPantry = false;

      }
    })
    setIsPantryEmpty(emptyPantry)
    setHasConsumed(false);
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

  function handleDecrease(index) {
    let consumed = false;
    const array = consumedItems.map((c, i) => {

      if (i === index) {

        if (!consumed && c.qty > 1)
          consumed = true;

        return c =
        {
          ...c,
          qty: c.qty - 1
        };

      } else {

        if (!consumed && c.qty > 0)
          consumed = true;

        return c;
      }
    });
    return setConsumedItems(array);
  }

  function handleIncrease(index) {
    let consumed = hasConsumed;
    const array = consumedItems.map((c, i) => {
      if (i === index) {
        consumed = true;
        return c =
        {
          ...c,
          qty: c.qty + 1
        };
      } else {
        return c;
      }
    });
    setHasConsumed(consumed);
    return setConsumedItems(array);
  }

  function renderItem(index, item) {

    if (isLoading) return;

    let consumedItem = consumedItems[index];

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
            <Stack direction="horizontal" gap={1} >
              <div><Button variant='link' disabled={consumedItem.qty === 0} onClick={() => handleDecrease(index)} className='m-0 p-0 d-flex align-items-start'><BsCaretDown /></Button></div>
              <div><span className='ms-1 me-1 ps-1 pe-1'>{consumedItem.qty}</span></div>
              <div><Button variant='link' disabled={item.currentQty === 0 || item.currentQty === consumedItem.qty} onClick={() => handleIncrease(index)} className='m-0 p-0 d-flex align-items-start'><BsCaretUp /></Button></div>
            </Stack>
          </Col>
        </Row>
      </ListGroup.Item>
    )
  }

  function renderItems() {
    if (isLoading) {
      return [...Array(1)].map((item) => renderItem(1, item))

    } else if (pantryItems && pantryItems.length) {
      let index = 0;
      return (pantryItems.map((item) => renderItem(index++, item)))

    }
    return "Not Found";
  }

  return (
    <Stack gap={3}>
      <div>
      </div>
      <div>
        <ListGroup>
          <ListGroup.Item variant="primary"><h6>{pantry.name}</h6></ListGroup.Item>
        </ListGroup>
      </div>
      <div>
        <ListGroup>
          {renderItems()}
        </ListGroup>
      </div>
      <Stack direction="horizontal" gap={2} className="d-flex justify-content-end">
        <div><Button variant="primary" size="sm" onClick={handleClear} active={hasConsumed}>Clear</Button></div>
        <div><Button variant="primary" size="sm" onClick={handleSave} active={!isPantryEmpty && hasConsumed}>Save</Button></div>
      </Stack>
    </Stack>
  )
}