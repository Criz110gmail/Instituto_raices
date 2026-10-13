DO $prueba$
BEGIN
 IF NOT pago_coincide_cobertura(1,2027,'ANUAL_PENDIENTE') THEN RAISE EXCEPTION 'Propuesta anual pendiente';END IF;
 IF NOT pago_coincide_cobertura(2,2027,'ANUAL_LIQUIDADO') THEN RAISE EXCEPTION 'Anual liquidado con diciembre bonificado';END IF;
 IF pago_coincide_cobertura(2,2026,'ANUAL_LIQUIDADO') THEN RAISE EXCEPTION 'Año cubierto no es fecha del pago';END IF;
 IF NOT EXISTS(SELECT 1 FROM pago_cobertura_anual WHERE pago_id=2 AND meses_pagados=11 AND meses_bonificados=1 AND beneficio=1000 AND aplicado=11000) THEN RAISE EXCEPTION '11 pagados más 1 bonificado';END IF;
 IF pago_coincide_cobertura(3,2027,'ANUAL_LIQUIDADO') OR NOT pago_coincide_cobertura(3,2027,'ANUAL_NO_LIQUIDADO') THEN RAISE EXCEPTION 'Devolución parcial no debe parecer anual liquidado';END IF;
 IF NOT pago_coincide_cobertura(4,2027,'ANUAL_NO_VIGENTE') THEN RAISE EXCEPTION 'Pago cancelado';END IF;
 IF NOT pago_coincide_cobertura(10,2027,'ANUAL_NO_VIGENTE') THEN RAISE EXCEPTION 'Acuerdo revertido por devolución total';END IF;
 IF pago_coincide_cobertura(5,2027,'ANUAL') THEN RAISE EXCEPTION 'No mezclar meses de hermanos';END IF;
 IF NOT pago_coincide_cobertura(5,2027,'SIN_ANUAL') THEN RAISE EXCEPTION 'Selección parcial de hermanos';END IF;
 IF pago_coincide_cobertura(6,2027,'ANUAL') THEN RAISE EXCEPTION 'No mezclar conceptos';END IF;
 IF pago_coincide_cobertura(7,2027,'ANUAL') THEN RAISE EXCEPTION 'No aceptar enero duplicado';END IF;
 IF pago_coincide_cobertura(9,NULL,'ANUAL') THEN RAISE EXCEPTION 'No mezclar años';END IF;
 IF NOT pago_coincide_cobertura(8,2027,'ANUAL_LIQUIDADO') THEN RAISE EXCEPTION 'Pago ordinario que cubre12 meses';END IF;
END;
$prueba$;
SELECT 'Casos SQL de cobertura anual correctos' AS resultado;
