-- Step 1: Add the geo_zone_id column to the patient table (if not already added)
ALTER TABLE dispensing.patient 
ADD COLUMN geozoneid UUID;

-- Step 2: Populate the geo_zone_id for existing records
UPDATE dispensing.patient p
SET geozoneid = (
  SELECT f.geographicZoneId
  FROM referencedata.facilities f
  WHERE f.id = p.facilityId
)
WHERE p.geozoneid IS NULL;

-- Step 3: Set the geo_zone_id column to NOT NULL if required
-- ALTER TABLE dispensing.patient
-- ALTER COLUMN geozoneid SET NOT NULL;