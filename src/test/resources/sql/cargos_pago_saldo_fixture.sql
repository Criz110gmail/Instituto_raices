-- CTE de datos ficticios: no crea ni modifica registros reales.
WITH cargo AS (
 SELECT n::bigint AS id,1::bigint AS inscripcion_id,1::bigint AS concepto_cobro_id,
 DATE '2026-10-01' AS fecha_vencimiento, CASE WHEN n=15 THEN 1000 ELSE 500 END::numeric AS importe_original,
 CASE WHEN n=16 THEN 'CANCELADO' WHEN n=17 THEN 'CONVENIDO' ELSE 'EMITIDO' END AS estado_registro
 FROM generate_series(1,17) n
), inscripcion AS (SELECT 1::bigint AS id,1::bigint AS alumno_id),
alumno AS (SELECT 1::bigint AS id,1::bigint AS institucion_id,'prueba'::text AS busqueda_autocomplete),
alumno_tutor AS (SELECT 1::bigint AS alumno_id,2::bigint AS tutor_id,true AS activo,true AS es_responsable_financiero,
 CURRENT_DATE AS fecha_inicio,null::date AS fecha_fin),
concepto_cobro AS (SELECT 1::bigint AS id,'cuota'::text AS busqueda_autocomplete),
ajuste_cargo AS (SELECT * FROM (VALUES (13::bigint,'DISMINUCION',500::numeric),
 (15::bigint,'DISMINUCION',200::numeric),(15::bigint,'AUMENTO',80::numeric)) ajustes(cargo_id,efecto,monto)),
aplicacion_pago AS (
 SELECT n::bigint AS cargo_id,'APLICAR' AS operacion,500::numeric AS monto FROM generate_series(1,12) n
 UNION ALL SELECT 15,'APLICAR',880 UNION ALL SELECT 15,'REVERTIR',100
), opciones AS (
