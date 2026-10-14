const {test}=require('node:test');
const assert=require('node:assert/strict');
const fs=require('node:fs');
const vm=require('node:vm');
test('conserva retorno en formularios locales sin duplicarlo ni enviarlo a otros sitios',()=>{
 const retorno='/admin/catalogos/pagos?q=Alumno&conceptoCobro=TEST&pagina=2';
 const forms=[
  {action:'http://localhost:18080/admin/pagos/1/validar',inputs:[]},
  {action:'http://localhost:18080/admin/pagos/1/devolver',inputs:[{name:'retorno'}]},
  {action:'https://externo.test/guardar',inputs:[]}
 ];
 forms.forEach(f=>{f.querySelector=()=>f.inputs.find(i=>i.name==='retorno');f.append=i=>f.inputs.push(i);});
 let ready;
 const document={body:{dataset:{retornoListado:retorno}},addEventListener:(_,fn)=>{ready=fn;},querySelectorAll:()=>forms,createElement:()=>({})};
 vm.runInNewContext(fs.readFileSync('src/main/resources/static/js/retorno-listado.js','utf8'),{document,URL,location:{href:'http://localhost:18080/admin/pagos/1/editar',origin:'http://localhost:18080'}});
 ready();ready();
 assert.equal(forms[0].inputs.length,1);assert.equal(forms[0].inputs[0].value,retorno);assert.equal(forms[0].inputs[0].type,'hidden');
 assert.equal(forms[1].inputs.length,1);assert.equal(forms[2].inputs.length,0);
});
