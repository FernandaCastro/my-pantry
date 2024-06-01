-- Update properties.property_value to lowercase and replace 'space' by 'dash'
UPDATE properties
SET property_value = (
    SELECT jsonb_agg(
               CASE
                   WHEN element::text = '""' THEN '"other"'
                   ELSE lower(regexp_replace(element::text, '\s', '-', 'g'))::jsonb
               END
           )
    FROM jsonb_array_elements(property_value) AS element
);

-- Update product.category to lowercase and replace 'space' by 'dash'
UPDATE product SET category = lower(regexp_replace(category, '\s', '-', 'g'))
WHERE category is not null;


-- Create Supermarket table
CREATE TABLE IF NOT EXISTS purchase.SUPERMARKET(
    ID BIGSERIAL PRIMARY KEY,
    NAME VARCHAR(50) NOT NULL,
    CATEGORIES JSONB NOT NULL
);