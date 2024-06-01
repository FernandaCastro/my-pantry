-- Update product.category to lowercase and replace 'space' by 'dash'
UPDATE product SET category = lower(regexp_replace(category, '\s', '-', 'g'))
WHERE category is not null