INSERT INTO PROPERTIES VALUES
('rewe.supermarket.categories',
'["Fruit and vegetables", "Refrigerated", "Meat", "Bakery", "Dairy", "Grocery", "Beverages", "Coockies", "Cleaning", "Frozen", "Personal Hygiene", ""]')
on conflict do nothing;

INSERT INTO PROPERTIES VALUES
('aldi.supermarket.categories',
'["Bakery", "Coockies", "Fruit and vegetables", "Refrigerated", "Meat", "Dairy", "Beverages", "Grocery", "Frozen",  "Cleaning", "Personal Hygiene", ""]')
on conflict do nothing;