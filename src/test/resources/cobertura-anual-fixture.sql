-- Datos FICTICIOS para una instancia PostgreSQL efímera, nunca para la BD del usuario.
CREATE TABLE pago(id bigint PRIMARY KEY,acuerdo_anticipado_id bigint,institucion_id bigint,plantel_registro_id bigint,moneda text,estado text);
CREATE TABLE alumno(id bigint PRIMARY KEY,institucion_id bigint,nombres text,primer_apellido text,segundo_apellido text,matricula text);
CREATE TABLE inscripcion(id bigint PRIMARY KEY,alumno_id bigint,plantel_id bigint);
CREATE TABLE concepto_cobro(id bigint PRIMARY KEY,nombre text,categoria text);
CREATE TABLE cargo(id bigint PRIMARY KEY,inscripcion_id bigint,concepto_cobro_id bigint,importe_original numeric,periodo_cobro_inicio date,periodo_cobro_fin date,estado_registro text,moneda text);
CREATE TABLE acuerdo_anticipado(id bigint PRIMARY KEY,estado text);
CREATE TABLE acuerdo_anticipado_cargo(acuerdo_id bigint,cargo_id bigint,beneficio numeric,pagar numeric);
CREATE TABLE solicitud_aplicacion_pago(pago_id bigint,cargo_id bigint);
CREATE TABLE aplicacion_pago(pago_id bigint,cargo_id bigint,operacion text,monto numeric);
CREATE TABLE ajuste_cargo(cargo_id bigint,efecto text,monto numeric);
INSERT INTO alumno VALUES(1,1,'Alumno','Prueba',NULL,'A-01'),(2,1,'Hermano','Prueba',NULL,'A-02');
INSERT INTO inscripcion VALUES(1,1,1),(2,2,1);
INSERT INTO concepto_cobro VALUES(1,'Colegiatura','COLEGIATURA'),(2,'Otra colegiatura','COLEGIATURA');
INSERT INTO acuerdo_anticipado SELECT n,CASE WHEN n IN(4,10) THEN 'CANCELADO' WHEN n=1 THEN 'PROPUESTO' ELSE 'APLICADO' END FROM generate_series(1,10)n WHERE n<>8;
INSERT INTO pago SELECT n,CASE WHEN n=8 THEN NULL ELSE n END,1,1,'MXN',CASE WHEN n=1 THEN 'PENDIENTE_VALIDACION' WHEN n=4 THEN 'CANCELADO' ELSE 'VALIDADO' END FROM generate_series(1,10)n;
INSERT INTO cargo SELECT p*100+m,CASE WHEN p=5 AND m>6 THEN 2 ELSE 1 END,CASE WHEN p=6 AND m>6 THEN 2 ELSE 1 END,1000,
 make_date(CASE WHEN p=9 AND m>6 THEN 2028 ELSE 2027 END,m,1),
 (make_date(CASE WHEN p=9 AND m>6 THEN 2028 ELSE 2027 END,m,1)+interval '1 month - 1 day')::date,'EMITIDO','MXN'
 FROM generate_series(1,10)p CROSS JOIN generate_series(1,12)m;
INSERT INTO cargo VALUES(713,1,1,1000,DATE '2027-01-01',DATE '2027-01-31','EMITIDO','MXN');
INSERT INTO acuerdo_anticipado_cargo SELECT p.id,c.id,CASE WHEN c.id%100=12 THEN 1000 ELSE 0 END,CASE WHEN c.id%100=12 THEN 0 ELSE 1000 END FROM pago p JOIN cargo c ON c.id/100=p.id WHERE p.id<>8;
INSERT INTO solicitud_aplicacion_pago SELECT id/100,id FROM cargo WHERE id%100<>12 OR id/100=8;
INSERT INTO aplicacion_pago SELECT id/100,id,'APLICAR',CASE WHEN id=301 THEN 900 ELSE 1000 END FROM cargo WHERE id/100 NOT IN(1,4,10) AND (id%100<>12 OR id/100=8);
INSERT INTO ajuste_cargo SELECT id,'DISMINUCION',1000 FROM cargo WHERE id%100=12 AND id/100 NOT IN(1,4,8,10);
