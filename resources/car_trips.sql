-- :name insert-car-trip :execute :affected
INSERT INTO car_trips (id, starting_km) VALUES (:id, :starting_km, NOW());

-- :name update-car-trip :execute :affected
UPDATE car_trips
SET ending_km = :ending_km
--~ (when (contains? params :trip_l) ",trip_length_km=:trip_l")
--~ (when (contains? params :max_temp) ",max_temperature=:max_temp")
--~ (when (contains? params :max_speed) ",max_speed=:max_speed")
--~ (when (contains? params :end_time) ",end_time=:end_time")
--~ (when (contains? params :trip_duration) ",trip_duration=:trip_duration")
--~ (when (contains? params :average_speed) ",average_speed=:average_speed")
WHERE id = :id;
