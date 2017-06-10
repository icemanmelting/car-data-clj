-- :name get-car-settings :query :one
SELECT * FROM car_settings WHERE id=:id;

-- :name create-settings :execute :affected
INSERT INTO car_settings(id, constant_kilometers, trip_kilometers) VALUES (:id, :cnst_km, :trip_km);

-- :name update-carsettings :execute :affected
UPDATE car_settings
SET constant_kilometers = :constant_km,
  trip_kilometers = :trip_km
  --~ (when (contains? params :trip_init_f) ",trip_initial_fuel_level=:trip_init_f")
  --~ (when (contains? params :avg_fuel_c) ",average_fuel_consumption=:avg_fuel_c")
  --~ (when (contains? params :dashboard_type) ",dashboard_type=:dashboard_type")
  --~ (when (contains? params :tyre_offset) ",tyre_offset=:tyre_offset")
  --~ (when (contains? params :next_oil_change) ",next_oil_change=:next_oil_change")
  WHERE id = :id;
