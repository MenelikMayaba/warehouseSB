-- First, make the columns nullable temporarily
ALTER TABLE item
    ALTER COLUMN price DROP NOT NULL,
    ALTER COLUMN quantity DROP NOT NULL;

-- Update existing NULL values to default values
UPDATE item SET price = 0.0 WHERE price IS NULL;
UPDATE item SET quantity = 0 WHERE quantity IS NULL;

-- Now make the columns NOT NULL
ALTER TABLE item
    ALTER COLUMN price SET NOT NULL,
    ALTER COLUMN quantity SET NOT NULL;