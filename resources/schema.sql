DROP TABLE IF EXISTS car_settings CASCADE;
CREATE TABLE IF NOT EXISTS car_settings (
  id BIGINT,
  constant_kilometers NUMERIC(2),
  trip_kilometers NUMERIC(2),
  trip_initial_fuel_level NUMERIC(2),
  average_fuel_consumption NUMERIC(2),
  dashboard_type TEXT,
  tyre_offset NUMERIC(2),
  next_oil_change NUMERIC(2),

  PRIMARY KEY (id)
);

DROP TABLE IF EXISTS car_trips CASCADE;
CREATE TABLE IF NOT EXISTS car_trips (
  id UUID,
  starting_km NUMERIC(2),
  ending_km NUMERIC(2),
  trip_length_km NUMERIC(2),
  max_temperature NUMERIC(2),
  max_speed NUMERIC(2),
  start_time TIMESTAMP,
  end_time TIMESTAMP,
  trip_duration NUMERIC(2),
  average_speed NUMERIC(2),

  PRIMARY KEY (id)
);

DROP TABLE IF EXISTS speed_data CASCADE;
CREATE TABLE IF NOT EXISTS speed_data (
  id UUID,
  trip_id UUID,
  speed NUMERIC(2),
  rpm NUMERIC(2),
  gear INT,
  ts TIMESTAMP,

  FOREIGN KEY (trip_id) REFERENCES car_trips (id),
  PRIMARY KEY (id, ts)
);

DROP TABLE IF EXISTS temperature_data CASCADE;
CREATE TABLE IF NOT EXISTS temperature_data (
  id UUID,
  trip_id UUID,
  value NUMERIC(2),
  ts TIMESTAMP,

  FOREIGN KEY (trip_id) REFERENCES car_trips (id),
  PRIMARY KEY (id)
);

DROP TABLE IF EXISTS car_logs CASCADE;
CREATE TABLE IF NOT EXISTS car_logs (
  id UUID,
  trip_id UUID,
  message TEXT,
  ts TIMESTAMP,
  log_level TEXT,

  FOREIGN KEY (trip_id) REFERENCES car_trips (id),
  PRIMARY KEY (id)
);
