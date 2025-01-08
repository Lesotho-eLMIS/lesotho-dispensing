-- Change Dose Column Type from Integer to Double

ALTER TABLE dispensing.PrescriptionLineItem
ALTER COLUMN dose TYPE DOUBLE PRECISION;