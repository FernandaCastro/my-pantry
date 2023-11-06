import Navbar from './Navbar.js';
import { useState, useContext, useEffect } from 'react';
import { PantryContext } from './PantryContext.js';

export default function Consume() {

  const pantry = useContext(PantryContext);
  const [pantryItems, setPantryItems] = useState([]);
  const [consumedItems, setConsumedItems] = useState([]);

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
    let copy = [...consumedItems];

    data.forEach((item) => {
      copy = [...copy,
      {
        pantryId: item.pantryId,
        productId: item.productId,
        qty: 0
      }
      ]
    })
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
    const array = consumedItems.map((c, i) => {
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
    return setConsumedItems(array);
  }

  function handleSave() {
    //post("/pantry/" + pantry.id + "/consume", consumedItems);
  }

  function renderItem(index, item) {

    let consumedItem = consumedItems[index];

    return (
      <li className="collection-item avatar" key={item.productId}>
        <img src="../healthy-food.png" alt="" className="circle" />
        <span className="title">{item.product.code}</span>
        <div className="row">

          <div className="col s5">
            <p>
              {item.product.description} <br />
              {item.product.size}
            </p>
          </div>

          <div className="col s2">
            <span>{item.currentQty}</span>
          </div>

          <div className="col s2">
            <center>
              <a className="left waves-effect waves-circle waves-light btn-floating secondary-content-content"
                onClick={() => handleDecrease(index, consumedItem.qty)}>
                <i className="material-icons" >remove</i></a>
              <input type="text" value={consumedItem.qty} />
              <a className="right waves-effect waves-circle waves-light btn-floating secondary-content-content"
                onClick={() => handleIncrease(index, consumedItem.qty)}>
                <i className="material-icons" >add</i></a>
            </center>
          </div>

          <div className="col s3">
            <h6>Any other button</h6>
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
          <button onClick={handleSave}>Save</button>
        </div>
      </div>
    </>
  )
}