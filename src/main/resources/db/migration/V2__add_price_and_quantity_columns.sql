-- First add columns as nullable
ALTER TABLE item ADD COLUMN IF NOT EXISTS price FLOAT;
ALTER TABLE item ADD COLUMN IF NOT EXISTS quantity INTEGER;

-- Update existing rows with default values
UPDATE item SET price = 0.0 WHERE price IS NULL;
UPDATE item SET quantity = 0 WHERE quantity IS NULL;

-- Now alter columns to be NOT NULL
ALTER TABLE item ALTER COLUMN price SET NOT NULL;
ALTER TABLE item ALTER COLUMN quantity SET NOT NULL;