-- Clasificación derivada; no actualiza pagos, cargos, becas ni movimientos.
CREATE VIEW pago_cobertura_anual AS
SELECT p.id AS pago_id,a.id AS alumno_id,a.matricula,
 concat_ws(' ',a.nombres,a.primer_apellido,a.segundo_apellido) AS alumno,
 cc.id AS concepto_id,cc.nombre AS concepto,
 extract(year FROM c.periodo_cobro_inicio)::integer AS anio,
 count(*)::integer AS meses,
 count(*) FILTER (WHERE d.pagar=0)::integer AS meses_bonificados,
 count(*) FILTER (WHERE neto.aplicado>0 AND neto.aplicado>=neto.total)::integer AS meses_pagados,
 sum(COALESCE(d.beneficio,0)) AS beneficio,
 sum(neto.aplicado) AS aplicado,
 p.moneda,
 CASE WHEN p.estado IN ('CANCELADO','RECHAZADO') OR ac.estado='CANCELADO' THEN 'NO_VIGENTE'
      WHEN p.estado='PENDIENTE_VALIDACION' THEN 'PENDIENTE'
      WHEN p.estado='VALIDADO' AND (ac.id IS NULL OR ac.estado='APLICADO')
       AND sum(neto.aplicado)>0
       AND bool_and(c.estado_registro='EMITIDO' AND neto.total>=0 AND neto.aplicado>=neto.total)
      THEN 'LIQUIDADO' ELSE 'NO_LIQUIDADO' END AS estado_cobertura
FROM pago p
JOIN LATERAL (
 SELECT s.cargo_id FROM solicitud_aplicacion_pago s WHERE s.pago_id=p.id
 UNION SELECT ap.cargo_id FROM aplicacion_pago ap WHERE ap.pago_id=p.id
 UNION SELECT ad.cargo_id FROM acuerdo_anticipado_cargo ad WHERE ad.acuerdo_id=p.acuerdo_anticipado_id
) origen ON true
JOIN cargo c ON c.id=origen.cargo_id
JOIN inscripcion i ON i.id=c.inscripcion_id
JOIN alumno a ON a.id=i.alumno_id AND a.institucion_id=p.institucion_id
JOIN concepto_cobro cc ON cc.id=c.concepto_cobro_id AND cc.categoria='COLEGIATURA'
LEFT JOIN acuerdo_anticipado ac ON ac.id=p.acuerdo_anticipado_id
LEFT JOIN acuerdo_anticipado_cargo d ON d.acuerdo_id=ac.id AND d.cargo_id=c.id
JOIN LATERAL (
 SELECT c.importe_original+COALESCE((SELECT sum(CASE WHEN j.efecto='AUMENTO' THEN j.monto ELSE -j.monto END) FROM ajuste_cargo j WHERE j.cargo_id=c.id),0) AS total,
 COALESCE((SELECT sum(CASE WHEN ap.operacion='APLICAR' THEN ap.monto ELSE -ap.monto END) FROM aplicacion_pago ap WHERE ap.cargo_id=c.id AND ap.pago_id=p.id),0) AS aplicado
) neto ON true
WHERE i.plantel_id=p.plantel_registro_id AND c.moneda=p.moneda
 AND date_trunc('month',c.periodo_cobro_inicio)=date_trunc('month',c.periodo_cobro_fin)
GROUP BY p.id,a.id,a.matricula,a.nombres,a.primer_apellido,a.segundo_apellido,
 cc.id,cc.nombre,extract(year FROM c.periodo_cobro_inicio),p.moneda,p.estado,ac.id,ac.estado
HAVING count(*)=12 AND count(DISTINCT extract(month FROM c.periodo_cobro_inicio))=12;

CREATE FUNCTION pago_coincide_cobertura(p_id bigint,p_anio integer,p_tipo text)
RETURNS boolean LANGUAGE sql STABLE AS $consulta$
 SELECT
 (p_anio IS NULL OR EXISTS (
   SELECT 1 FROM cargo c WHERE extract(year FROM c.periodo_cobro_inicio)=p_anio AND c.id IN (
    SELECT s.cargo_id FROM solicitud_aplicacion_pago s WHERE s.pago_id=p_id
    UNION SELECT ap.cargo_id FROM aplicacion_pago ap WHERE ap.pago_id=p_id
    UNION SELECT d.cargo_id FROM acuerdo_anticipado_cargo d JOIN pago p ON p.acuerdo_anticipado_id=d.acuerdo_id WHERE p.id=p_id
   )
 )) AND CASE WHEN p_tipo='TODOS' THEN true
 WHEN p_tipo='SIN_ANUAL' THEN NOT EXISTS(SELECT 1 FROM pago_cobertura_anual a WHERE a.pago_id=p_id AND (p_anio IS NULL OR a.anio=p_anio))
 ELSE EXISTS(SELECT 1 FROM pago_cobertura_anual a WHERE a.pago_id=p_id AND (p_anio IS NULL OR a.anio=p_anio)
   AND (p_tipo='ANUAL' OR a.estado_cobertura=CASE p_tipo WHEN 'ANUAL_LIQUIDADO' THEN 'LIQUIDADO' WHEN 'ANUAL_PENDIENTE' THEN 'PENDIENTE' WHEN 'ANUAL_NO_VIGENTE' THEN 'NO_VIGENTE' WHEN 'ANUAL_NO_LIQUIDADO' THEN 'NO_LIQUIDADO' ELSE '-' END)) END;
$consulta$;
