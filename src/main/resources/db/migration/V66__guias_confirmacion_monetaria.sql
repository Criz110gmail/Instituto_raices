-- Actualización editorial: los nuevos modales no ejecutan operaciones al consultar guías.
WITH pasos_actualizados AS (
    UPDATE guia_proceso_paso
    SET instrucciones = regexp_replace(instrucciones,
        '(pulsa|presiona|haz clic en) (Validar y publicar|Ejecutar devolución y publicar egreso|Aplicar ajuste al saldo)',
        '\1 \2; revisa el resumen del modal y pulsa Confirmar operación si coincide', 'gi')
    WHERE guia_id IN (SELECT id FROM guia_proceso WHERE estado = 'CONFIRMADA')
      AND instrucciones ~* '(pulsa|presiona|haz clic en) (Validar y publicar|Ejecutar devolución y publicar egreso|Aplicar ajuste al saldo)'
    RETURNING guia_id
)
UPDATE guia_proceso
SET version_contenido = version_contenido + 1, revisada_el = DATE '2026-10-07'
WHERE id IN (SELECT DISTINCT guia_id FROM pasos_actualizados);
