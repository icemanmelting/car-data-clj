-- :name get-speed-data :query :one
SELECT * FROM speed_data WHERE id=:id;

-- :name create-speed-data :execute :affected
INSERT INTO speed_data (id, trip_id, speed, rpm, gear, ts) VALUES (:id, :trip_id, :speed, :rpm, :gear, NOW())

-- :name get-temp-data :query :one
SELECT * FROM temperature_data WHERE id=:id;

-- :name create-temperature-data :execute :affected
INSERT INTO temperature_data (id, trip_id, value, ts) VALUES (:id, :trip_id, :val, NOW())
