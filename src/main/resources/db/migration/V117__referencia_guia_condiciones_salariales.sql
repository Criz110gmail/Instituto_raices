-- Actualiza sólo la referencia de continuidad de una guía confirmada previamente.
UPDATE guia_proceso_paso SET precaucion=
$texto$No se declara auditoría exhaustiva de seguridad ni lectura laboral seleccionable en Roles. Puestos y condiciones salariales ya están disponibles desde Empleados → Ver expediente → Condiciones salariales → Abrir condiciones salariales, con permiso salarial propio además de Empleados y Datos laborales. Su prueba humana es independiente y sigue pendiente; no genera nómina ni mueve dinero. No repetir permisos ya confirmados para acceder a esta referencia.$texto$
WHERE guia_id=(SELECT id FROM guia_proceso WHERE slug='empleado-permisos-laborales') AND numero=8;
UPDATE guia_proceso SET version_contenido=version_contenido+1,revisada_el=DATE '2026-10-20'
WHERE slug='empleado-permisos-laborales';
