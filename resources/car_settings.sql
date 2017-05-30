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