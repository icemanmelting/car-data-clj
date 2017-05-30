-- :name create-log :execute :affected
INSERT INTO car_logs (id, trip_id, message, ts, log_level)
VALUES (:id, :trip_id, :message, NOW(), :log_level);