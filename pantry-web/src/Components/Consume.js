import Navbar from './Navbar.js';
import { useState, useContext, useEffect } from 'react';
import { PantryContext } from './PantryContext.js';

export default function Consume() {

  const pantry = useContext(PantryContext);
  const [pantryItems, setPantryItems] = useState([]);
  const [consumedItems, setConsumedItems] = useState([]);
  const [hasConsume, setHasConsume] = useState(false);

  useEffect(() => {

    fetch("http://localhost:8080/pantry/" + pantry.id + "/items")
      .then((response) => response.json())
      .then((data) => {
        setPantryItems(data);
        loadConsumedItemsList(data);
      })
      .catch((error) => { console.log(error) })
  }, []);


  function loadConsumedItemsList(data) {
    let copy = [];

    data.forEach((item) => {
      copy = [...copy,
      {
        pantryId: item.pantryId,
        productId: item.productId,
        qty: 0
      }
      ]
    })
    setHasConsume(false)
    return setConsumedItems(copy);
  }

  function handleDecrease(index, qty) {
    if (qty > 0) {
      const array = consumedItems.map((c, i) => {
        if (i === index) {

          return c =
          {
            ...c,
            qty: qty - 1
          };
        } else {

          return c;
        }
      });
      return setConsumedItems(array);
    }
  }

  function handleIncrease(index, qty) {
    let notZero = false;

    const array = consumedItems.map((c, i) => {
      if (c.qty > 0) {
        notZero = true;
      }

      if (i === index) {

        return c =
        {
          ...c,
          qty: qty + 1
        };
      } else {
        return c;
      }
    });
    setHasConsume(notZero);
    return setConsumedItems(array);
  }

  function handleSave() {
    fetch('http://localhost:8080/pantry/' + pantry.id + '/consume',
      {
        method: 'POST',
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(consumedItems)
      })

      .then((response) => response.json())
      .then((response) => {
        setPantryItems(response);
        loadConsumedItemsList(response)
      })
      .catch((error) => { console.log(error) })

    alert("Pantry updated successfully!");
  }

  function renderItem(index, item) {

    let consumedItem = consumedItems[index];

    return (
      <li className="collection-item avatar" key={item.productId}>
        <img src="./healthy-food.png" alt="" className="circle" />

        <div className="col s4"><span className="title">{item.product.code}</span></div>
        <div className="col s2"><span className="teal-text text-lighten-2" >Current</span></div>
        <div className="col s2"><span className="teal-text text-lighten-2" >Provisioned</span></div>
        <div className="col s2"><span className="teal-text text-lighten-2" >Last Provisionig</span></div>

        <div className="row">

          <div className="col s4">
            <p>
              {item.product.description} <br />
              {item.product.size}
            </p>
          </div>

          <div className="col s2">
            <span>{item.currentQty}</span>
          </div>

          <div className="col s2">
            <span>{item.provisionedQty}</span>
          </div>

          <div className="col s2">
            <span>{item.lastProvisioning}</span>
          </div>

          <div className="col s2">
            <center>
              <a href="#!" className="left waves-effect waves-circle waves-light btn-floating secondary-content-content"
                style={consumedItem.qty > 0 ? { pointerEvents: "auto" } : { pointerEvents: "none" }}
                onClick={() => handleDecrease(index, consumedItem.qty)} >
                <i className="material-icons" >remove</i></a>
              <span>{consumedItem.qty}</span>
              <a href="#!" className="right waves-effect waves-circle waves-light btn-floating secondary-content-content"
                style={consumedItem.qty > 0 ? { pointerEvents: "auto" } : { pointerEvents: "none" }}
                onClick={() => handleIncrease(index, consumedItem.qty)}>
                <i className="material-icons" >add</i></a>
            </center>
          </div>
        </div>
      </li>
    )
  }

  function renderItems() {
    let index = 0;
    return (pantryItems.map((item) => renderItem(index++, item)))
  }

  return (
    <>
      <Navbar />
      <div className='row'>
        <div className="col s12">
          <ul className="collection with-header">
            <li className="collection-header teal-text text-lighten-2"><h5>Pantry {pantry.name}</h5></li>
            {renderItems()}
          </ul>
        </div>
      </div>
      <div className='row'>
        <div className='col 3 offset-s9'>
          <a href="#!" className='waves-effect waves-light btn-small'
            style={hasConsume ? { pointerEvents: "auto" } : { pointerEvents: "none" }}
            onClick={() => loadConsumedItemsList(pantryItems)}>
            <i className="material-icons left">clear</i>
            Clear
          </a>
          &nbsp;&nbsp;
          <a href="#!" className="right waves-effect waves-light btn-small"
            style={hasConsume ? { pointerEvents: "auto" } : { pointerEvents: "none" }}
            onClick={handleSave}>
            <i className="material-icons left">done_all</i>
            Save
          </a>
        </div>
      </div>
    </>
  )
}