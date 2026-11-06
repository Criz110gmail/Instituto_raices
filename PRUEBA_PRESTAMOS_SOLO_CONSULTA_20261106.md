# Préstamos: usuario de sólo consulta

Estado: **CONFIRMADA por el usuario el 06/11/2026**.
Confirmación: todas las pruebas pasaron y las rutas protegidas muestran
«No tienes acceso a esta información».

Guía integrada: **Préstamos: entrega, liquidación y neto cero → paso 14**,
versión de contenido 6, incorporada por la migración editorial V160.

El usuario configuró y probó la cuenta. El agente no creó usuarios, concedió
derechos ni ejecutó operaciones sobre préstamos o cheques.

## Configuración propuesta y ensayada

| Campo | Valor del ejemplo |
| --- | --- |
| Rol propuesto | PRUEBA_PRESTAMOS_LECTURA · Préstamos — Sólo consulta |
| Usuario propuesto | prueba.prestamos.lectura |
| Institución | Instituto Raices |
| Alcance | Institución |
| Único permiso del módulo | Préstamos - Consultar |

Sin otros roles que eleven permisos. Sin Préstamos - Administrar ni permisos
salariales o de operación financiera. No se quitaron derechos al administrador.
La cuenta se probó en una sesión separada; su contraseña la establece el usuario,
no se documenta ni se comparte con el agente.

## Lectura y Excel aceptados

1. Iniciar sesión con la cuenta limitada, usando institución\\usuario si corresponde.
2. Entrar a Préstamos y consultar los registros existentes de Tomás y Sofía.
3. Abrir Resumen, Cuotas y abonos e Historial, sin modificar registros.
4. Filtrar Sofía y LIQUIDADO; exportar Excel con PRE-000002, principal $100.00 y
   pendientes $0.00, sin incluir a Tomás.
5. No mostrar Nuevo préstamo ni acciones de modificación al lector.

## Rutas administrativas denegadas

Con la cuenta limitada, abrir las tres rutas del ejemplo:

| Ruta | Resultado confirmado |
| --- | --- |
| /admin/prestamos/nuevo | No tienes acceso a esta información |
| /admin/prestamos/2/editar | No tienes acceso a esta información |
| /admin/prestamos/2/reprogramar | No tienes acceso a esta información |

Las rutas deben denegar antes de abrir un formulario de captura. No guardar,
entregar, cancelar ni reprogramar para ensayar permisos. El ID2 corresponde a
Sofía en la base del ejemplo; no asumir los mismos IDs en otra instalación.

## Límites y continuación

Esta aceptación acredita lectura, Excel y denegación de las rutas GET indicadas.
No se afirma ensayo humano de POST forzado, cuentas de otras instituciones,
concurrencia o paginación masiva, ni un rol lector de cheques por esta prueba.
La seguridad de servidor permanece obligatoria, no sólo ocultar botones.

Con esta prueba y la consulta de cheques aprobada se cierran los dos bloques
manuales inmediatos. No equivale liberar todo el sistema: recibos/consultas de
nómina, escenarios familiares, sesión y requisitos de producción conservan sus
pendientes independientes. No repetir pagos de Tomás, Sofía ni Clara.
