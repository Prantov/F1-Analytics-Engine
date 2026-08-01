--checks table names only
-- SELECT table_name
-- FROM information_schema.tables
-- WHERE table_schema = 'public';

--overview works with any lang
-- SELECT table_name, column_name, data_type
-- FROM information_schema.columns
-- WHERE table_schema = 'public'
-- ORDER BY table_name, ordinal_position;


--detailed view
-- SELECT c.table_name, c.column_name, c.data_type
-- FROM information_schema.columns c
-- JOIN information_schema.tables t
--   ON c.table_name = t.table_name
-- WHERE t.table_schema = 'public'
--   AND t.table_type = 'BASE TABLE';


--highly detailed
-- SELECT 
--     c.relname AS table_name,
--     a.attname AS column_name,
--     t.typname AS data_type,
--     CASE 
--         WHEN a.attnotnull THEN 'NOT NULL'
--         ELSE 'NULLABLE'
--     END AS nullability,
--     con.contype AS constraint_type
-- FROM pg_class c
-- JOIN pg_attribute a ON a.attrelid = c.oid
-- JOIN pg_type t ON a.atttypid = t.oid
-- LEFT JOIN pg_constraint con 
--        ON con.conrelid = c.oid 
--       AND a.attnum = ANY (con.conkey)
-- WHERE c.relkind = 'r'              
--   AND c.relnamespace = (SELECT oid FROM pg_namespace WHERE nspname = 'public')
--   AND a.attnum > 0                 
--   AND NOT a.attisdropped
-- ORDER BY c.relname, a.attnum;
