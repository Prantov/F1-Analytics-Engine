
--Transform data into drivers table
INSERT INTO drivers(id,drivername,wins,podiums,country,birthdate,current_team_id)
SELECT 
rd.driver_id,
rd.forename||' '||rd.surname,
(SELECT COUNT(*)
FROM raw_results rr
WHERE rr.driver_id=rd.driver_id
AND rr.position='1'
),
(
SELECT COUNT(*)
FROM raw_results rr
WHERE rr.driver_id=rd.driver_id
AND rr.position IN('1','2','3')
),
rd.nationality,
rd.dob,
NULL
FROM raw_drivers rd
ON CONFLICT (id) DO NOTHING;

--insert data into tracks
WITH MinMilliseconds AS (
    
    SELECT r.circuit_id, MIN(lt.millisecond) as min_ms
    FROM raw_laptimes lt
    JOIN raw_races r ON lt.race_id = r.race_id
    GROUP BY r.circuit_id
),
RecordDetails AS (
    
    SELECT DISTINCT ON (mm.circuit_id)
        mm.circuit_id,
        (lt.millisecond / 1000.0) AS lap_record, 
        TO_DATE(r.year::TEXT, 'YYYY') AS record_year,
        lt.driver_id
    FROM MinMilliseconds mm
    JOIN raw_races r ON r.circuit_id = mm.circuit_id
    JOIN raw_laptimes lt ON lt.race_id = r.race_id AND lt.millisecond = mm.min_ms
    ORDER BY mm.circuit_id
)

INSERT INTO tracks (id, trackname, country, record, recyear, driver_id)
SELECT 
    c.circuit_id,
    c.name,
    c.country,
    rd.lap_record,
    rd.record_year,
    rd.driver_id
FROM raw_circuits c
LEFT JOIN RecordDetails rd ON c.circuit_id = rd.circuit_id
ON CONFLICT (id) DO NOTHING;


--insert data into wdc_table;
WITH DriverPoints AS (
SELECT ra.year,
re.driver_id,
SUM(re.points) AS total_points
FROM raw_results re
JOIN raw_races ra ON re.race_id=ra.race_id
GROUP BY ra.year,re.driver_id
),
RankedDrivers AS (
SELECT year,
driver_id,
total_points,
ROW_NUMBER() OVER(PARTITION BY year ORDER BY total_points DESC) AS  pos
FROM DriverPoints
)
INSERT INTO Wdc(cyear,winnerdriver_id,pointsw,runnerup_id,pointsr)
SELECT 
TO_DATE(w.year::text,'YYYY') AS cyear,
w.driver_id AS winnerdriver_id,
w.total_points AS pointsw,
r.driver_id AS runnerup_id,
COALESCE(r.total_points,0) AS pointsr
FROM RankedDrivers w
LEFT JOIN RankedDrivers r ON w.year=r.year and r.pos=2
WHERE w.pos=1
ON CONFLICT(cyear) DO NOTHING;