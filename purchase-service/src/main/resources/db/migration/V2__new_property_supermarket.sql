INSERT INTO PROPERTIES VALUES
('rewe.supermarket.categories',
'["Fruit and vegetables", "Refrigerated", "Meat", "Bakery", "Dairy", "Grocery", "Beverages", "Cookies", "Cleaning", "Frozen", "Personal Hygiene", ""]')
on conflict do nothing;

INSERT INTO PROPERTIES VALUES
('aldi.supermarket.categories',
'["Bakery", "Cookies", "Fruit and vegetables", "Refrigerated", "Meat", "Dairy", "Beverages", "Grocery", "Frozen",  "Cleaning", "Personal Hygiene", ""]')
on conflict do nothing;