# pantry-inventory

It manages the pantry inventory and creates shopping list

Pantry Service (localhost:8080) :
  - Manages pantry, pantry items and products. 
  - Sends events to PurchaseCreateTopic when PantryItem reaches the defined threshold
  - Listens to PurchaseCompleteTopic and update the pantry when the Purchase is complete
  - Stores data in Postgres

Purchase Service (localhost:8081) :
  - Listens to PurchaseCreateTopic to manage a list of items to purchase
  - Send events to PurchaseCompleteTopic once the purchase is complete
  - Stores data in Postgres

Pantry Web (localhost:3000) :
  - Development is still in initial stages, while I learn Reactjs.
    











Manage Kafka Topics:
docker run -p 80:80 -e xeotek_kadeck_free="fcastro.rj@gmail.com" -e xeotek_kadeck_port=80 xeotek/kadeck:4.3.5
http://localhost:80
with user & password:
admin
