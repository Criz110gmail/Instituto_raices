const {test}=require('node:test');
const assert=require('node:assert/strict');
const fs=require('node:fs');

test('Periodos: Limpiar usa la acción secundaria compartida y conserva su ruta',()=>{
 const html=fs.readFileSync('src/main/resources/templates/admin/programacion-nomina-listado.html','utf8');
 assert.ok(html.includes("<a class=\"module-action\" th:href=\"@{'/admin/'+${tipo}}\">Limpiar</a>"));
});
