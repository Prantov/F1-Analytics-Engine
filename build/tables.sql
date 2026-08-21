
CREATE TABLE IF NOT EXISTS Teams(
	id SERIAL PRIMARY KEY,
	teamname TEXT UNIQUE,
	team_principal TEXT,
	founded DATE,
	wins INTEGER DEFAULT 0,
	contructor_wins INTEGER,
	headquarters TEXT,
	engine_supplier TEXT,
	driver_championship INTEGER DEFAULT 0);

CREATE TABLE IF NOT EXISTS Drivers(
	id SERIAL PRIMARY KEY,
	drivername TEXT,
	wins INTEGER DEFAULT 0,
	podiums INTEGER DEFAULT 0,
	country TEXT,
	birthdate DATE,
	current_team_id INTEGER REFERENCES Teams(id));

CREATE TABLE IF NOT EXISTS Tracks(
	id SERIAL PRIMARY KEY,
	trackname TEXT,
	country TEXT,
	record NUMERIC(8,3),
	recyear DATE,
	driver_id INTEGER REFERENCES Drivers(id));

CREATE TABLE IF NOT EXISTS Wdc(
id SERIAL PRIMARY KEY,
cyear DATE UNIQUE,
winnerdriver_id INTEGER,
pointsw NUMERIC(6,2) DEFAULT 0,
runnnerup_id INTEGER,
pointsr NUMERIC(6,2) DEFAULT 0
);

CREATE TABLE IF NOT EXISTS Wcc(
id SERIAL PRIMARY KEY,
cyear DATE UNIQUE,
winnerteam_id INTEGER,
pointsw NUMERIC(6,2) DEFAULT 0,
runnerup_id INTEGER,
pointsr NUMERIC(6,2) DEFAULT 0
);

CREATE TABLE raw_constructors(
	constructor_id INT PRIMARY KEY,
	constructor_ref VARCHAR(255),
	name VARCHAR(255),
	nationality VARCHAR(255),
	url TEXT
);

CREATE TABLE IF NOT EXISTS raw_drivers(
    driver_id INT PRIMARY KEY,
    driver_ref VARCHAR(255),
	number VARCHAR(255),
    code VARCHAR(255),
    forename VARCHAR(255),
    surname VARCHAR(255),
    dob DATE,
    nationality VARCHAR(255),
    url TEXT
)

CREATE TABLE IF NOT EXISTS raw_circuits(
    circuit_id INT PRIMARY KEY,
    circuit_ref VARCHAR(255),
    name TEXT,
    location VARCHAR(255),
    country VARCHAR(255),
    lat DOUBLE PRECISION,
    lng DOUBLE PRECISION,
    alt DOUBLE PRECISION,
    url TEXT 
);

CREATE TABLE IF NOT EXISTS raw_races(
race_id INT PRIMARY KEY,
year INT,
round INT,
circuit_id INT,
name VARCHAR(255),
date DATE,
time VARCHAR(255),
url TEXT);

CREATE TABLE IF NOT EXISTS raw_results(
result_id INT PRIMARY KEY,
race_id INT,
driver_id INT,
constructor_id INT,
number INT,
grid INT,
position VARCHAR(255),
points DOUBLE PRECISION);

CREATE TABLE IF NOT EXISTS raw_laptimes(
race_id INT,
driver_id INT,
lap INT,
position INT,
time VARCHAR(255),
millisecond INT);

CREATE TABLE IF NOT EXISTS raw_pitstops(
race_id INT,
driver_id INT,
stop INT,
lap INT,
time VARCHAR(255),
duration VARCHAR(255),
millisecond INT);

CREATE TABLE IF NOT EXISTS raw_constructorresults(
constructor_results_id INTEGER PRIMARY KEY,
race_id INT,
constructor_id INT,
points INT
);

CREATE TABLE IF NOT EXISTS raw_driver_standings(
driver_standing_id INTEGER PRIMARY KEY,
race_id INT,
driver_id INT,
points INT,
position INT,
wins INT
);

CREATE TABLE IF NOT EXISTS raw_constructor_standings(
constructor_standing_id INTEGER PRIMARY KEY,
race_id INT,
constructor_id INT,
points DOUBLE PRECISION,
position INT,
wins INT
);

CREATE TABLE IF NOT EXISTS seasons(
year INT,
url TEXT
);

CREATE TABLE IF NOT EXISTS status(
status_id INTEGER PRIMARY KEY,
status VARCHAR(255));

CREATE TABLE IF NOT EXISTS raw_qualifying(
qualify_id INTEGER PRIMARY KEY,
race_id INT,
driver_id INT,
constructor_id INT,
number INT,
position INT
);

CREATE TABLE IF NOT EXISTS raw_sprint_results(
result_id INTEGER PRIMARY KEY,
race_id INT,
driver_id INT,
constructor_id INT,
number INT,
grid INT,
position INT,
position_order INT,
points DOUBLE PRECISION,
laps INT,
fastest_lap INT);