# Puesto de referencia en Datos laborales — 24/10/2026

Prueba humana pendiente. No asigna sueldo, no calcula nómina ni mueve dinero.

## Caso nuevo en esta computadora

La base local es distinta de la computadora anterior. El propietario indica que no
tiene empleados y se le propuso EMP-NOM-TEST-01 · Carlos Prueba Nómina; aún no confirma
qué pasos guardó. Buscar antes de crear y continuar los registros existentes.
No suponer que Git transportó datos ni pedir repetir Bruce del otro equipo.

1. Con administración institucional autorizada, Personal y nómina → Puestos laborales
   → Nuevo puesto. Código AUX-ADM-TEST, nombre Auxiliar administrativo de prueba,
   descripción Puesto ficticio para capacitación, Activo. Guardar con confirmación.
   Si ya existe, reutilizarlo. Administrar catálogo necesita permiso salarial existente.
2. Empleados → Nuevo empleado, sólo si no existe: institución propia, código
   EMP-NOM-TEST-01, nombres Carlos, primer apellido Prueba, segundo Nómina; correo y
   teléfono opcionales vacíos. Registrar y confirmar. No necesita perfil docente/usuario.
3. Abrir su expediente → Datos laborales. Situación Actividad laboral confirmada;
   fecha ingreso01/10/2026; baja/motivo vacíos. En Puesto laboral enfocar campo vacío:
   hasta diez puestos activos institucionales; el nombre aparece arriba y código abajo.
   Escribir AUX o Auxiliar y seleccionar AUX-ADM-TEST desde las sugerencias.
4. Guardar información laboral → Cancelar en modal conserva captura y no guarda.
   Reabrir, comprobar puesto/fecha/situación y confirmar. Recargar: nombre del puesto
   y fecha guardados; histórico privado conserva cambio. No crea condición salarial.
5. Cambiar el texto por Otro puesto no seleccionado e intentar guardar: validación
   local, conserva captura y Datos laborales abierto. Seleccionar sugerencia válida
   antes de confirmar. No registrar texto libre nuevo ni dar de baja al empleado.
6. Si hay más de diez puestos, ayuda indica más resultados; buscar desde tres letras.
   Con uno/dos caracteres no inicia búsqueda nueva. Sin catálogos activos no hay
   coincidencias: registrar puesto con cuenta autorizada, no inventar texto libre.

## Compatibilidad y permisos

- Nombres anteriores pueden conservarse al editar ingreso/situación aunque su puesto
  no esté catalogado o se haya renombrado/desactivado. No vincular por coincidencia de
  nombre. Para cambiar el nombre elegir un puesto activo desde el catálogo.
- La selección valida ID/institución/activo/nombre en servidor; guarda el nombre como
  referencia histórica en el campo existente, no crea una nueva FK ni fuente salarial.
  Renombrar el catálogo no modifica automáticamente los datos laborales anteriores.
- Ayuda sólo requiere Empleados + administración laboral institucional, no acceso a
  salarios; devuelve ID/nombre/código, nunca importes. Crear/editar Puestos conserva sus
  permisos salariales actuales. Lectura laboral sin edición no puede usar esta ayuda.
- Revisar formulario/ayuda/modal en móvil y temas claro/oscuro. No cambiar estilos
  globales, clases, usuarios, planteles ni nóminas para verificar este ajuste.

## Continuar el ejemplo de nómina

Después de guardar laboral: asignar plantel propio desde01/10/2026 como Principal;
crear jornada JOR-NOM-TEST30horas; configuración ESQ-NOM-TEST mensual12000;
asignarla a Carlos desde01/01/2027 sin fin; concepto BON-NOM-TEST Percepción/Habitual
de500 desde01/01/2027; calendario NOM-MENSUAL-TEST Mensual; periodo ENE-2027-TEST
01–31/01/2027/pago31/01, incidencias opcionales vacías. No repetir registros guardados.
Consultar Configuración de nómina al15/01/2027, asignar calendario con motivo e historial.
Preparación de nómina → enero → revisar Carlos12000+500=12500 → guardar borrador.
NO aplicar egreso todavía. IMSS/Infonavit/ISR/préstamos no se incluyen en este caso.
Registrar aceptación sólo cuando el propietario confirme resultados.
