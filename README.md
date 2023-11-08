# my-pantry

It manages pantry inventories and creates shopping lists.

Still under development.

### pantry-service (localhost:8080) :

- Manages pantry, pantry items and products.
- Manages the consumption of the items in the pantry as well as their replenishment
- Sends events to PurchaseCreateTopic when PantryItem reaches the defined threshold
- Listens to PurchaseCompleteTopic in order to update the pantry once the Purchase Order is closed
- Stores data in Postgres

| Request Type |  Path                    | Description          |
|:-------------|:-------------------------|:---------------------|
|GET/POST|/pantries|Pantry List & Create|
|GET/PUT/DELETE|/pantries/{pantryId}|Pantry CRUD|
|GET/POST|/pantries/{pantryId}/items|Pantry Items List & Create|
|GET/PUT/DELETE|/pantries/{pantryId}/items/{productId}| Pantry Items CRUD|
|POST|/pantries/{pantryId}/consume| Consume/Use an Item from a Pantry|

### purchase-service (localhost:8081) :

- Creates a shopping list to be purchases
- Listens to PurchaseCreateTopic to manage a list of items to be purchased
- Send events to PurchaseCompleteTopic once the purchase is closed
- Stores data in Postgres

| Request Type | Path                     | Description          |
|:-------------|:-------------------------|:---------------------|
| GET| /purchases | List Purchase Orders|
| POST| /purchases/open | Get an existing open Purchase Order or create one|
| POST| /purchases/close  | Close and complete a Purchase Order|

### pantry-web (localhost:3000) :

- Development is still in initial stages, while I learn `Reactjs`.
- Home Page and Consume Page available.

/Components/Home.js - Pantry List:
![img.png](Home-PantryList.png)

/Components/Consume.js
![img.png](Consume-ItemsList.png)  
