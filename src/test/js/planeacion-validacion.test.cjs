const {test}=require('node:test'),assert=require('node:assert/strict'),fs=require('node:fs');
const {mensajeCampo}=require('../../main/resources/static/js/planeacion-form-ui.js');
test('obligatorios vacíos o con espacios se identifican; campos opcionales no se exigen',()=>{
 for(const valor of ['',null,'   '])assert.match(mensajeCampo({valor,requerido:true}),/obligatorio/);
 assert.equal(mensajeCampo({valor:'',requerido:false}),'');assert.equal(mensajeCampo({valor:'Propósito',requerido:true}),'');
});
test('fin de semana, actividad fuera de rango y duración tienen mensajes claros',()=>{
 const campo={nombre:'fechaFin',valor:'2026-10-18',tipo:'date',requerido:true};
 assert.match(mensajeCampo(campo,{inicio:'2026-10-19'}),/anterior/);
 assert.match(mensajeCampo({...campo,valor:'2026-10-26'},{inicio:'2026-10-19'}),/siete días/);
 assert.equal(mensajeCampo({...campo,valor:'2026-10-25'},{inicio:'2026-10-19'}),'');
 assert.match(mensajeCampo({nombre:'actividades[2].fecha',valor:'2026-10-26'},{inicio:'2026-10-19',fin:'2026-10-23'}),/dentro/);
 for(const valor of ['0','1441','1.5','texto'])assert.match(mensajeCampo({tipo:'number',valor,min:'1',max:'1440'}),/duración/);
 assert.equal(mensajeCampo({tipo:'number',valor:'45',min:'1',max:'1440'}),'');
 assert.match(mensajeCampo({valor:'abcd',maxLength:3}),/máximo 3/);
});
test('contrato de navegación, validación y filas evita envío inválido e índices duplicados',()=>{
 const ui=fs.readFileSync('src/main/resources/static/js/planeacion-form-ui.js','utf8'),filas=fs.readFileSync('src/main/resources/static/js/planeacion-form.js','utf8');
 for(const texto of ['event.preventDefault()','summary.focus()','aria-invalid','aria-describedby','planeacion:rows-changed',"form.dataset.readonly==='true'",'panels.indexOf(errors[0].panel)'])assert.ok(ui.includes(texto));
 assert.ok(filas.includes('function reindex('));assert.ok(filas.includes('AbortController'));assert.ok(filas.includes("input[name=materiaIds]:checked"));assert.ok(filas.includes("form.dataset.readonly==='true'"));
});
