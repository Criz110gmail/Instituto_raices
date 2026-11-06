-- Acceso explícito, sin habilitar personas ni conceder roles automáticamente.
ALTER TABLE usuario DROP CONSTRAINT ck_usuario_tipo_cuenta;
ALTER TABLE usuario ADD CONSTRAINT ck_usuario_tipo_cuenta CHECK(tipo_cuenta IN ('ADMINISTRATIVO','PORTAL_TUTOR','PORTAL_MAESTRO','PORTAL_EMPLEADO'));
ALTER TABLE empleado ADD COLUMN usuario_portal_id BIGINT UNIQUE REFERENCES usuario(id),
 ADD COLUMN portal_recibos_habilitado BOOLEAN NOT NULL DEFAULT false,
 ADD CONSTRAINT ck_empleado_portal_configurado CHECK(NOT portal_recibos_habilitado OR usuario_portal_id IS NOT NULL);

CREATE FUNCTION validar_usuario_portal_empleado() RETURNS trigger LANGUAGE plpgsql AS $$
BEGIN
 IF NEW.usuario_portal_id IS NOT NULL AND NOT EXISTS(SELECT 1 FROM usuario u WHERE u.id=NEW.usuario_portal_id AND u.institucion_id=NEW.institucion_id AND u.tipo_cuenta IN ('PORTAL_EMPLEADO','PORTAL_MAESTRO')) THEN
  RAISE EXCEPTION 'Cuenta del empleado incompatible con institución o tipo';
 END IF;
 IF TG_OP='UPDATE' AND OLD.usuario_portal_id IS NOT NULL AND NEW.usuario_portal_id IS DISTINCT FROM OLD.usuario_portal_id THEN
  RAISE EXCEPTION 'El vínculo de acceso del empleado no puede reasignarse';
 END IF;
 IF EXISTS(SELECT 1 FROM maestro m WHERE m.empleado_id=NEW.id AND m.usuario_id IS NOT NULL AND NEW.usuario_portal_id IS NOT NULL AND m.usuario_id<>NEW.usuario_portal_id) THEN
  RAISE EXCEPTION 'El empleado ya tiene otra cuenta docente';
 END IF;
 IF NEW.usuario_portal_id IS NOT NULL AND EXISTS(SELECT 1 FROM maestro m WHERE m.usuario_id=NEW.usuario_portal_id AND m.empleado_id<>NEW.id) THEN
  RAISE EXCEPTION 'La cuenta docente pertenece a otro empleado';
 END IF;
 RETURN NEW;
END $$;
CREATE TRIGGER trg_empleado_usuario_portal BEFORE INSERT OR UPDATE ON empleado FOR EACH ROW EXECUTE FUNCTION validar_usuario_portal_empleado();

CREATE FUNCTION validar_cuenta_docente_empleado() RETURNS trigger LANGUAGE plpgsql AS $$
BEGIN
 IF NEW.usuario_id IS NOT NULL AND EXISTS(SELECT 1 FROM empleado e WHERE e.id=NEW.empleado_id AND e.usuario_portal_id IS NOT NULL AND e.usuario_portal_id<>NEW.usuario_id) THEN
  RAISE EXCEPTION 'Reutiliza la cuenta del empleado en su perfil docente';
 END IF;
 RETURN NEW;
END $$;
CREATE TRIGGER trg_maestro_cuenta_empleado BEFORE INSERT OR UPDATE ON maestro FOR EACH ROW EXECUTE FUNCTION validar_cuenta_docente_empleado();

CREATE FUNCTION conservar_cuenta_portal_empleado() RETURNS trigger LANGUAGE plpgsql AS $$
BEGIN
 IF EXISTS(SELECT 1 FROM empleado e WHERE e.usuario_portal_id=NEW.id AND (e.institucion_id<>NEW.institucion_id OR NEW.tipo_cuenta NOT IN ('PORTAL_EMPLEADO','PORTAL_MAESTRO'))) THEN
  RAISE EXCEPTION 'La cuenta vinculada al empleado conserva su institución y tipo de portal';
 END IF;
 RETURN NEW;
END $$;
CREATE TRIGGER trg_usuario_cuenta_empleado BEFORE UPDATE ON usuario FOR EACH ROW EXECUTE FUNCTION conservar_cuenta_portal_empleado();
