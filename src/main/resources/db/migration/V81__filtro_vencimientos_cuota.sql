-- Consulta de configuraciones; no genera cargos ni modifica el calendario.
-- Mismo vencimiento que CargoServiceImpl: día limitado al mes y a vigencia de cuota.
CREATE FUNCTION cuota_vencimiento_en_rango(inicio DATE, fin DATE, dia INTEGER, desde DATE, hasta DATE)
RETURNS BOOLEAN LANGUAGE plpgsql IMMUTABLE AS $$
DECLARE
 inferior DATE := greatest(inicio, coalesce(desde, inicio));
 superior DATE := least(fin, coalesce(hasta, fin));
 mes DATE;
 fin_mes DATE;
 vence DATE;
BEGIN
 IF inicio IS NULL OR fin IS NULL OR dia IS NULL OR dia NOT BETWEEN 1 AND 31 OR inferior>superior THEN RETURN false; END IF;
 mes := date_trunc('month', inferior)::date;
 fin_mes := (mes + interval '1 month' - interval '1 day')::date;
 vence := greatest(inicio, least(fin, mes + least(dia, extract(day FROM fin_mes)::integer)-1));
 IF vence < inferior THEN
   mes := (mes + interval '1 month')::date;
   IF mes>fin THEN RETURN false; END IF;
   fin_mes := (mes + interval '1 month' - interval '1 day')::date;
   vence := greatest(inicio, least(fin, mes + least(dia, extract(day FROM fin_mes)::integer)-1));
 END IF;
 RETURN vence BETWEEN inferior AND superior;
END;
$$;
