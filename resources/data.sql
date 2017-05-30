-- :name create-speed-data :execute :affected
INSERT INTO speed_data (id, trip_id, speed, rpm, gear, ts) VALUES (:id, :trip_id, :speed, :rpm, :gear, NOW())

-- :name create-temperature-data :execute :affected
INSERT INTO temperature_data (id, trip_id, value, ts) VALUES (:id, :trip_id, :val, NOW())
