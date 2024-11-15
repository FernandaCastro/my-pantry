# Changelog

### version: [unreleased]:

- Delete provisioned products from the shopping list (on Shopping List page)
- CRUD for Role and Permissions

### version: [0.10.x] : 2024-11-15

- <b>pantry-web:</b>
    - [change]:
        - Big refactoring replacing React Context by react-query as a Global State Manager
        - Use of react-query cache to improve the dashbord graph generation, fetching fresh data only when it's stale.
        - Totally refactoring of App.js, removing context dependencies and isolating routes to avoid unnecessary
          re-rendering.
        - Use of Link component from react-router-dom to correctly navigate among pages, avoiding complete re-render on
          transitions.
        - Apply theme based on the user logging in. It's no more based on the last theme saved on the browser
          LocalStorage.

- <b>account-service:</b>
    - [added]:
        - Remember Me Logic. When it's active, JWT and AUTH Cookie are valid for 30 days, otherwise it's valid for only
          24 hours.
        - Store theme in the Account entity

### version: [0.9.x] : 2024-08-23

- <b>account-service:</b>
    - [added]:
        - Use Redis to cache authorization and access control requests

### version: [0.8.x] : 2024-08-14

- <b>pantry-service:</b>
- <b>purchase-service:</b>
    - [added]:
        - 'Analyse Pantry' button (on Pantry page) should not only create a provisioning, but also create new
          provisioning,
          increase or decrease already provisioned quantitty and delete unecessary provisionings.

### version: [0.7.x] : 2024-08-12

- <b>pantry-web:</b>
- <b>pantry-service:</b>
    - [added]:
        - Pantry Dashboard: the graph gives you a quick idea of how empty your pantries are. The Dashboard also lists
          the top 5 lowest level
          products.
        - Pantry Wizard: It can now create the shopping list with the missing items, if you choose so.

### version: [0.6.x] : 2024-08-06

- <b>pantry-web:</b>
- <b>pantry-service:</b>
    - [added]:
        - Pantry Wizard: A pre-defined list of basic products available in two different languages (en-pt) makes the
          Pantry Creation easier and faster. No more manually entering basic item.- Wizard to create pantries based on a
          suggested list of basic items, instead of manually enter them.

### version: [0.5.x] : 2024-06-21

- [change]:
    - Replacing Zookeeper and Confluent Kafka with Apache Kafka (KRaft)
    - Defining context for each service api call: /pantryservice, /purchaseservice, /accountservice
- [security]:
    - Enabling actuator/health
    - Enabling https (prod) / http (dev)
    - Encrypting using public/private keys to protect password

### version: [0.4.x] : 2024-06-17

- <b>pantry-web:</b>
    - [added]:
        - New Themes (mono-light, mono-dark, lila-dark)
    - [change]:
        - Replace Profile Menu by a Slide Menu

### version: [0.3.x] : 2024-06-14

- [added]:
    - Layout using Cards
    - Enter Ideal and Current quantity when adding new product direct to the pantry items list

### version: [0.2.x] : 2024-06-03

- [added]:
    - Internationalization (EN-PT) - (Backend)

### version: [0.1.0] : 2024-06-01

- [added]:
    - Internationalization (EN-PT) - (Frontend)
    - CRUD for Supermarkets and its particular order of categories
    - New Permissions: list_supermarket, create_supermarket, edit_supermarket and delete_supermarket
    - Add permissions to ROLE ADMIN: It has all ROLE OWNER permissions, except the delete permissions.
    - All Roles and its associated Permissions are listed on the Group & Members page.

### version: [0.0.x] : 2023-07-17

<b> Role Based Access Control (RBAC) </b>

- [added]:
    - List Roles and Permissions (Profile Icon -> Groups & Members -> View permissions)
    - New Permission: PURCHASE_PANTRY Permissions refactored: CONSUME_PANTRY, ANALYSE_PANTRY
    - Control the access to Purchases
    - Purchase by selecting specific pantries or all


- [change]:
- Access Control (AC) is now centralized in account_service, as part of the authorization logic.
    - Why? So far, Pantry and Product are the only relevant entities when it comes to Access Control (AC).
    - So instead of making <i>purchase-service</i> requests AC data to <i>pantry-service</i>, it requets AC data to a
      centralized RBAC managed on <i>account-service</i>.

