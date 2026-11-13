-- Sólo contenido editorial de la guía. No renombra instituciones ni registros operativos.
-- Se conserva el historial de migraciones y sus checksums.
DO $contenido$
DECLARE campo RECORD;
BEGIN
 FOR campo IN SELECT * FROM (VALUES
  ('guia_proceso','titulo'),('guia_proceso','resumen'),
  ('guia_proceso','requisitos'),('guia_proceso','ejemplo'),
  ('guia_proceso_paso','titulo'),('guia_proceso_paso','perfil'),
  ('guia_proceso_paso','modulo'),('guia_proceso_paso','instrucciones'),
  ('guia_proceso_paso','ejemplo'),('guia_proceso_paso','resultado'),
  ('guia_proceso_paso','precaucion')
 ) AS campos(tabla,columna)
 LOOP
  EXECUTE format(
   'UPDATE %I SET %I=regexp_replace(regexp_replace(%I,$1,$2,''gi''),$3,$4,''gi'') WHERE %I ~* $3',
   campo.tabla,campo.columna,campo.columna,campo.columna
  ) USING 'instituto[ _-]+ra[ií]ces','Institución de ejemplo','ra[ií]ces','Ejemplo';
 END LOOP;
END
$contenido$;
