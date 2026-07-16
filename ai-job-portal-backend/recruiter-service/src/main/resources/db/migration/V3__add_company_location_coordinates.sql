-- DAY11 - Company Location Map: adds latitude/longitude to company_locations
-- so the frontend can render each office on a Leaflet map. Nullable since
-- existing rows and manually-entered addresses may not have coordinates yet.

ALTER TABLE company_locations
    ADD COLUMN latitude  NUMERIC(9, 6),
    ADD COLUMN longitude NUMERIC(9, 6);
