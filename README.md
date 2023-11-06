# my-pantry

It manages pantry inventories and creates shopping lists.

Still under development.

### pantry-service (localhost:8080) :

- Manages pantry, pantry items and products.
- Sends events to PurchaseCreateTopic when PantryItem reaches the defined threshold
- Listens to PurchaseCompleteTopic and update the pantry when the Purchase is complete
- Stores data in Postgres

| Request Type |  Path                    | Description          |
|:-------------|:-------------------------|:---------------------|
|GET/POST|/pantry|Pantry List & Create|
|GET/PUT/DELETE|/pantry/{pantryId}|Pantry CRUD|
|GET/POST|/pantry/{pantryId}/items|Pantry Items List & Create|
|GET/PUT/DELETE|/pantry/{pantryId}/items/{productId}| Pantry Items CRUD|
|POST|/pantry/{pantryId}/consume| Consume/Use an Item from a Pantry|

### purchase-service (localhost:8081) :

- Listens to PurchaseCreateTopic to manage a list of items to purchase
- Send events to PurchaseCompleteTopic once the purchase is complete
- Stores data in Postgres

| Request Type | Path                     | Description          |
|:-------------|:-------------------------|:---------------------|
| GET| /purchases | List Purchase Orders|
| POST| /purchase-create | Get an existing open Purchase Order or create one|
| POST| /purchase-close  | Close and complete a Purchase Order|

### pantry-web (localhost:3000) :

- Development is still in initial stages, while I learn `Reactjs`.
- Home Page and Consume Page available
  
