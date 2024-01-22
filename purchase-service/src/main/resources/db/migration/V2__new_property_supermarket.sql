UPDATE PROPERTIES SET PROPERTY_VALUE = '["", "Bakery", "Beverages", "Cleaning", "Coockies", "Dairy", "Frozen", "Fruit and vegetables", "Grocery", "Meat", "Personal Hygiene", "Refrigerated"]'
WHERE PROPERTY_KEY = 'product.categories';

INSERT INTO PROPERTIES VALUES
('rewe.supermarket.categories',
'["Fruit and vegetables", "Refrigerated", "Meat", "Bakery", "Dairy", "Grocery", "Beverages", "Coockies", "Cleaning", "Frozen", "Personal Hygiene", ""]');

INSERT INTO PROPERTIES VALUES
('aldi.supermarket.categories',
'["Bakery", "Coockies", "Fruit and vegetables", "Refrigerated", "Meat", "Dairy", "Beverages", "Grocery", "Frozen",  "Cleaning", "Personal Hygiene", ""]');