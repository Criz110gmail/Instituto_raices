-- Sólo índices para consultas históricas; no crea pagos, cuentas, usuarios ni permisos.
CREATE INDEX ix_recibo_nomina_pago_fecha ON nomina_pago_intento(institucion_id,ocurrido_en DESC,id DESC) WHERE estado IN ('PAGADO','REVERTIDO');
CREATE INDEX ix_recibo_nomina_cero_fecha ON nomina_cierre_cero(institucion_id,ocurrido_en DESC,fila_id DESC);
CREATE INDEX ix_recibo_nomina_empleado ON nomina_borrador_empleado(institucion_id,empleado_id,fecha_inicio,fecha_fin);
