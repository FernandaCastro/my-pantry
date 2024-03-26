![diagram](readme_images/MyPantry.png)

# My Pantry

My Pantry manages pantry inventories and maintains a shopping list as the products have been consumed from the
inventory. <br/> <br/>
You can also share your Pantry Inventories, Shopping Lists and Products with other users, by adding users to your
Account Group.

The frontend is developed in Reactjs and Javascript, while backend is written in Java 17, Spring Boot and Spring
Security. The Authentication is possible by either Google Signin (Google IDToken) or user/password.

Navigate to the root folder _<my-pantry>_ and run it as Docker containers: <br />
```docker compose up -d``` <br />
_**=> Populate the application.yml files before running it.**_

Screenshots: <br />
<img height="400" width="200" title="Login" src="./readme_images/login.png"/>
<img height="400" width="200" src="./readme_images/pantries.png"/>
<img height="400" width="200" src="./readme_images/pantry.png"/>
<img height="400" width="200" src="./readme_images/consume.png"/>
<img height="400" width="200" src="./readme_images/purchase.png"/>
<img height="400" width="200" src="./readme_images/products.png"/>
<img height="400" width="200" src="./readme_images/account-groups.png"/>
<img height="400" width="200" src="./readme_images/logout.png"/>

### pantry-web (localhost:3000) :

| Path | Description|
|:-------------|:-------------------------|
|/pantries <img height="30" width="30" src="./pantry-web/src/assets/images/cupboard-gradient.png" />| List Pantries and its related actions: new, edit and delete |
|/consume <img height="30" width="30" src="./pantry-web/src/assets/images/cook-gradient.png" /> | <b><u>After selecting a pantry</u></b>, you can consume items from it. <br /> Once the consumption of an item reaches 50%, an event to purchase more of that item is fired.
|/purchase <img height="30" width="30" src="./pantry-web/src/assets/images/shoppingcart-gradient.png" />| Lists items to be purchased. <br/> Items can be sorted by your favorite Supermarket category order. <br/> A shopping list is created <b>once you open a new Order</b>. <br/> When you're done with shopping and <b>close the Order</b>, then it updates your Pantry Inventory with the purchased items.
|/product <img height="30" width="30" src="./pantry-web/src/assets/images/food-gradient.png" />| List Products and its related actions: new, edit and delete

### account-service (localhost:8082) :

- Manages Accounts, Account Groups and Account Group Members
- Manages Roles and Permissions
- Acts as **_Authentication server_**:
  - validating the _Google IDToken_ or the user/password informed
  - issuing an _JWT Token_ embedded in a _Http Only Cookie_
- Acts as **_Authorization server_**:
  - retrieving the permissions associated to the user in a group

### pantry-service (localhost:8080) :

- Manages pantry, pantry items and products.
- Manages the consumption of the items in the pantry as well as their replenishment
- Sends events to purchase-service through a Kafka Topic (PurchaseCreateTopic) when PantryItem reaches the defined
  threshold (50%)
- Listens to Kafka Topic (PurchaseCompleteTopic) in order to update the Pantry Inventory once the Purchase Order is
  closed
- Stores data in Postgres

### purchase-service (localhost:8081) :

- Manages shopping lists
- Listens to Kafka Topic (PurchaseCreateTopic) to manage a list of items to be purchased
- Once the purchase is closed, an event is sent back to pantry-service through a Kafka Topic (PurchaseCompleteTopic)
- Stores data in Postgres





