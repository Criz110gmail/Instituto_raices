DO $$
DECLARE caso record; resultado boolean;
BEGIN
 FOR caso IN SELECT * FROM (VALUES
  ('2026-01-01'::date,'2026-12-31'::date,20,'2026-10-20'::date,'2026-10-20'::date,true),
  ('2026-01-01'::date,'2026-12-31'::date,20,'2026-10-21'::date,'2026-11-19'::date,false),
  ('2026-01-01'::date,'2026-12-31'::date,31,'2026-02-28'::date,'2026-02-28'::date,true),
  ('2028-01-01'::date,'2028-12-31'::date,31,'2028-02-29'::date,'2028-02-29'::date,true),
  ('2026-10-14'::date,'2026-12-10'::date,5,'2026-10-14'::date,'2026-10-14'::date,true),
  ('2026-10-14'::date,'2026-12-10'::date,20,'2026-12-10'::date,'2026-12-10'::date,true),
  ('2026-10-14'::date,'2026-12-10'::date,20,'2026-10-15'::date,'2026-10-19'::date,false),
  ('2026-10-14'::date,'2026-12-10'::date,20,'2026-12-11'::date,null::date,false),
  ('2026-10-14'::date,'2026-12-10'::date,20,null::date,'2026-10-13'::date,false),
  ('2026-10-14'::date,'2026-12-10'::date,20,null::date,'2026-10-20'::date,true),
  ('2026-10-14'::date,'2026-12-10'::date,20,'2026-12-10'::date,null::date,true),
  ('2026-10-14'::date,'2026-12-10'::date,20,'2026-11-21'::date,'2026-12-09'::date,false)
 ) AS casos(inicio,fin,dia,desde,hasta,esperado)
 LOOP
  resultado:=cuota_vencimiento_en_rango(caso.inicio,caso.fin,caso.dia,caso.desde,caso.hasta);
  IF resultado IS DISTINCT FROM caso.esperado THEN RAISE EXCEPTION 'Vencimiento incorrecto: %',caso; END IF;
 END LOOP;
 IF date(timezone('America/Mexico_City','2026-10-09 05:59:59+00'::timestamptz))<>'2026-10-08'::date
 OR date(timezone('America/Mexico_City','2026-10-09 06:00:00+00'::timestamptz))<>'2026-10-09'::date THEN
  RAISE EXCEPTION 'Límite local incorrecto';
 END IF;
END $$;
