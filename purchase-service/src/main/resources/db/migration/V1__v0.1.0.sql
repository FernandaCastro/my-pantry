-- Update properties.property_value to lowercase and replace 'space' by 'dash'
UPDATE properties
SET property_value = (
    SELECT jsonb_agg(
               lower(regexp_replace(element::text, '\s', '-', 'g'))::jsonb
           )
    FROM jsonb_array_elements(property_value) AS element
);

-- Update product.category to lowercase and replace 'space' by 'dash'
UPDATE product SET category = lower(regexp_replace(category, '\s', '-', 'g'))
WHERE category is not null