DROP TABLE IF EXISTS car_settings CASCADE;
CREATE TABLE IF NOT EXISTS car_settings (
  id BIGINT,
  constant_kilometers DOUBLE PRECISION,
  trip_kilometers DOUBLE PRECISION,
  trip_initial_fuel_level DOUBLE PRECISION,
  average_fuel_consumption DOUBLE PRECISION,
  dashboard_type TEXT,
  tyre_offset DOUBLE PRECISION,
  next_oil_change DOUBLE PRECISION,

  PRIMARY KEY (id)
);

DROP TABLE IF EXISTS car_trips CASCADE;
CREATE TABLE IF NOT EXISTS car_trips (
  id UUID,
  starting_km DOUBLE PRECISION,
  ending_km DOUBLE PRECISION,
  trip_length_km DOUBLE PRECISION,
  max_temperature DOUBLE PRECISION,
  max_speed DOUBLE PRECISION,
  start_time TIMESTAMP,
  end_time TIMESTAMP,
  trip_duration DOUBLE PRECISION,
  average_speed DOUBLE PRECISION,

  PRIMARY KEY (id)
);

DROP TABLE IF EXISTS speed_data CASCADE;
CREATE TABLE IF NOT EXISTS speed_data (
  id UUID,
  trip_id UUID,
  speed DOUBLE PRECISION,
  rpm DOUBLE PRECISION,
  gear INT,
  ts TIMESTAMP,

  FOREIGN KEY (trip_id) REFERENCES car_trips (id),
  PRIMARY KEY (id, ts)
);

DROP TABLE IF EXISTS temperature_data CASCADE;
CREATE TABLE IF NOT EXISTS temperature_data (
  id UUID,
  trip_id UUID,
  value DOUBLE PRECISION,
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
