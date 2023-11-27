import { useState, useContext, useEffect } from 'react';
import { PantryContext } from '../../../components/PantryContext.js';
import { getPantryItems, postPantryConsume } from '../../../services/apis/mypantry/fetch/requests/PantryRequests.ts';

export default function Consume() {

  const pantry = useContext(PantryContext);
  const [pantryItems, setPantryItems] = useState([]);
  const [isLoading, setIsLoading] = useState(true)

  const [consumedItems, setConsumedItems] = useState([]);
  const [isPantryEmpty, setIsPantryEmpty] = useState(true);
  const [hasConsumed, setHasConsumed] = useState(false);

  async function fetchPantryItems() {
    const res = await getPantryItems(pantry.id);
    setPantryItems(res);
    loadConsumedItems(res);
    setIsLoading(false);
  }

  useEffect(() => {
    fetchPantryItems()
    setIsLoading(true)
  }, [])

  async function fetchPantryConsume() {
    const res = await postPantryConsume(pantry.id, consumedItems);
    setPantryItems(res);
    loadConsumedItems(res);
    setIsLoading(false);
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

  function handleSave() {
    fetchPantryConsume();

    alert("Pantry updated successfully!");
  }

  function handleClear() {
    loadConsumedItems();
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
      <li className="collection-item avatar" key={item.productId}>
        <div className="row">
          <div className="col s4">
            <img src="./healthy-food.png" alt="" className="circle" />
            <span className="title">{item.product.code}</span>
          </div>
          <div className="col s2"><span className="teal-text text-lighten-2" >Current</span></div>
          <div className="col s2"><span className="teal-text text-lighten-2" >Provisioned</span></div>
          <div className="col s2"><span className="teal-text text-lighten-2" >Last Provisioning</span></div>
          <div className="col s2"><span className="teal-text text-lighten-2" >Consumed</span></div>
        </div>
        <div className="row">
          <div className="col s4">
            <p className="blue-grey-text text-darken-3">
              {item.product.description} <br />
              {item.product.size}
            </p>
          </div>

          <div className="col s2">
            <span className="blue-grey-text text-darken-3">{item.currentQty}</span>
          </div>

          <div className="col s2">
            <span className="blue-grey-text text-darken-3">{item.provisionedQty}</span>
          </div>

          <div className="col s2">
            <span className="blue-grey-text text-darken-3">{item.lastProvisioning}</span>
          </div>
          <div className="col s2">
            <center>
              <a href="#!" className="left"
                style={consumedItem.qty > 0 ? { pointerEvents: "auto" } : { pointerEvents: "none" }}
                onClick={() => handleDecrease(index)} >
                <i className="material-icons" >remove</i></a>
              <span>{consumedItem.qty}</span>
              <a href="#!" className="right"
                style={item.currentQty > 0 ? { pointerEvents: "auto" } : { pointerEvents: "none" }}
                onClick={() => handleIncrease(index)}>
                <i className="material-icons" >add</i></a>
            </center>
          </div>
        </div>
      </li>
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
    <>
      <div className='section'>
        <ul className="collection with-header">
          <li className="collection-header teal-text text-lighten-2"><h6>{pantry.name}</h6></li>
          {renderItems()}
        </ul>
      </div>
      <div className='section'>

        <a href="#!" className="right waves-effect waves-light btn-small"
          style={isPantryEmpty || !hasConsumed ? { pointerEvents: "none" } : { pointerEvents: "auto" }}
          onClick={handleSave}>
          <i className="material-icons left">done_all</i>
          Save
        </a>
        <a href="#!" className='right waves-effect waves-light btn-small'
          style={hasConsumed ? { pointerEvents: "auto" } : { pointerEvents: "none" }}
          onClick={handleClear}>
          <i className="material-icons left">clear</i>
          Clear
        </a>
      </div>
    </>
  )
}