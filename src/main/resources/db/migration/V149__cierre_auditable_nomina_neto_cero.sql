-- Cierre sin tesorería: no insertar pagos ni movimientos financieros ficticios.
CREATE TABLE nomina_cierre_cero (
 fila_id bigint PRIMARY KEY REFERENCES nomina_borrador_empleado(id),
 borrador_id bigint NOT NULL REFERENCES nomina_autorizacion(borrador_id),
 institucion_id bigint NOT NULL REFERENCES institucion(id),
 revision integer NOT NULL,
 usuario_id bigint NOT NULL REFERENCES usuario(id),
 ocurrido_en timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP,
 motivo varchar(2000) NOT NULL CHECK(btrim(motivo)<>''),
 detalle jsonb NOT NULL CHECK(jsonb_typeof(detalle)='object')
);
CREATE INDEX ix_nomina_cierre_cero_borrador ON nomina_cierre_cero(borrador_id);
CREATE TRIGGER tr_nomina_cierre_cero_inmutable BEFORE UPDATE OR DELETE ON nomina_cierre_cero
 FOR EACH ROW EXECUTE FUNCTION proteger_personal_historial();

CREATE OR REPLACE FUNCTION validar_autorizacion_nomina() RETURNS trigger LANGUAGE plpgsql AS $$ BEGIN
 PERFORM 1 FROM nomina_borrador WHERE id=NEW.borrador_id FOR UPDATE;
 IF NOT EXISTS(SELECT 1 FROM nomina_borrador b JOIN usuario u ON u.id=NEW.usuario_id AND u.institucion_id=b.institucion_id
  WHERE b.id=NEW.borrador_id AND b.institucion_id=NEW.institucion_id AND b.periodo_id=NEW.periodo_id
   AND b.estado='BORRADOR' AND b.revision_actual=NEW.revision)
 OR NOT EXISTS(SELECT 1 FROM nomina_fila_efectiva ne WHERE ne.borrador_id=NEW.borrador_id AND ne.revision=NEW.revision)
 OR EXISTS(SELECT 1 FROM nomina_fila_efectiva ne WHERE ne.borrador_id=NEW.borrador_id AND ne.revision=NEW.revision
  AND ne.tratamiento<>'NO_APLICAR' AND (ne.propuesto<0 OR (ne.propuesto=0 AND
   (ne.tratamiento<>'APLICAR' OR jsonb_array_length(coalesce(ne.fuentes->'prestamos','[]'::jsonb))>0))))
 THEN RAISE EXCEPTION 'Revisa la revisión, los netos negativos y los netos cero con préstamos o posposición antes de confirmar'; END IF;
 RETURN NEW;
END $$;

CREATE FUNCTION validar_cierre_cero_nomina() RETURNS trigger LANGUAGE plpgsql AS $$ BEGIN
 IF NOT EXISTS(SELECT 1 FROM nomina_fila_efectiva ne JOIN nomina_autorizacion a
  ON a.borrador_id=ne.borrador_id AND a.revision=ne.revision
  WHERE ne.id=NEW.fila_id AND ne.borrador_id=NEW.borrador_id AND ne.institucion_id=NEW.institucion_id
   AND ne.revision=NEW.revision AND ne.activo AND ne.tratamiento='APLICAR' AND ne.propuesto=0
   AND jsonb_array_length(coalesce(ne.fuentes->'prestamos','[]'::jsonb))=0
   AND a.usuario_id=NEW.usuario_id AND a.motivo=NEW.motivo AND a.ocurrido_en=NEW.ocurrido_en
   AND NEW.detalle=to_jsonb(ne))
 THEN RAISE EXCEPTION 'El cierre cero debe coincidir con la revisión autorizada y su auditoría'; END IF;
 RETURN NEW;
END $$;
CREATE TRIGGER tr_nomina_cierre_cero_validar BEFORE INSERT ON nomina_cierre_cero
 FOR EACH ROW EXECUTE FUNCTION validar_cierre_cero_nomina();

CREATE FUNCTION registrar_cierres_cero_nomina() RETURNS trigger LANGUAGE plpgsql AS $$ BEGIN
 INSERT INTO nomina_cierre_cero(fila_id,borrador_id,institucion_id,revision,usuario_id,ocurrido_en,motivo,detalle)
 SELECT ne.id,ne.borrador_id,ne.institucion_id,ne.revision,NEW.usuario_id,NEW.ocurrido_en,NEW.motivo,to_jsonb(ne)
 FROM nomina_fila_efectiva ne WHERE ne.borrador_id=NEW.borrador_id AND ne.revision=NEW.revision
  AND ne.tratamiento='APLICAR' AND ne.propuesto=0;
 RETURN NEW;
END $$;
CREATE TRIGGER tr_nomina_cierres_cero_autorizar AFTER INSERT ON nomina_autorizacion
 FOR EACH ROW EXECUTE FUNCTION registrar_cierres_cero_nomina();

-- No se reescribe ningún estado, fotografía ni autorización histórica.
DO $$ DECLARE d text; anterior text; BEGIN
 d:=pg_get_functiondef('nomina_estado_vigente(bigint)'::regprocedure);
 anterior:='AND ne.tratamiento<>''NO_APLICAR'' AND NOT EXISTS';
 IF strpos(d,anterior)=0 THEN RAISE EXCEPTION 'Revisar función de estado antes de integrar cierres cero'; END IF;
 d:=replace(d,anterior,'AND ne.tratamiento<>''NO_APLICAR'' AND NOT EXISTS(SELECT 1 FROM nomina_cierre_cero nc WHERE nc.fila_id=ne.id) AND NOT EXISTS');
 EXECUTE d;
END $$;
