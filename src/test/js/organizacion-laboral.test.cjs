const {test}=require('node:test'),assert=require('node:assert/strict'),fs=require('node:fs'),vm=require('node:vm');
test('asignación consulta datos, bloquea envío incompleto y descarta respuesta anterior',async()=>{
 const fields={},nodes={};for(const n of ['empleadoId','esquemaId','versionEsquema','versionJornada','versionPuesto','versionLaboral','revision','inicio'])fields[n]={value:'',listeners:{},addEventListener(e,f){this.listeners[e]=f;}};
 for(const n of ['puesto','jornada','sueldo','frecuencia'])nodes[n]={textContent:''};
 const boton={disabled:false},status={textContent:''},listeners={};
 const form={dataset:{},querySelector(s){if(s==='[data-labor-submit]')return boton;if(s==='[data-labor-status]')return status;const name=s.match(/name="([^"]+)"/);if(name)return fields[name[1]];return nodes[s.match(/data-labor-value="([^"]+)"/)[1]];},querySelectorAll(){return Object.values(nodes);},addEventListener(e,f){listeners[e]=f;}};
 const pending=[];vm.runInNewContext(fs.readFileSync('src/main/resources/static/js/organizacion-laboral.js','utf8'),{document:{querySelectorAll:()=>[form]},fetch:()=>new Promise(resolve=>pending.push(resolve)),URLSearchParams,Intl});
 assert.equal(boton.disabled,true);let blocked=false;listeners.submit({preventDefault(){blocked=true;},stopImmediatePropagation(){}});assert.equal(blocked,true);
 fields.empleadoId.value='1';fields.esquemaId.value='2';const uno=fields.esquemaId.listeners.change();fields.esquemaId.value='3';const dos=fields.esquemaId.listeners.change();
 const response=nombre=>({ok:true,headers:{get:()=> 'application/json'},json:async()=>({versionEsquema:1,versionJornada:2,versionPuesto:3,versionLaboral:4,revision:5,hoy:'2026-10-21',empleado:'EMP · Ana',esquema:{codigo:nombre,nombre,puestoTexto:'Docente',jornadaTexto:'Matutina',horas:30,importe:12000,moneda:'MXN',base:'MENSUAL',frecuencia:'QUINCENAL'}})});
 pending[1](response('NUEVO'));await dos;pending[0](response('VIEJO'));await uno;
 assert.equal(boton.disabled,false);assert.equal(fields.revision.value,5);assert.match(form.dataset.confirmScheme,/NUEVO/);assert.match(nodes.frecuencia.textContent,/Mensual \/ Quincenal/);assert.match(nodes.jornada.textContent,/30 horas/);
 fields.esquemaId.value='';await fields.esquemaId.listeners.change();assert.equal(boton.disabled,true);assert.equal(fields.revision.value,'');assert.equal(nodes.sueldo.textContent,'—');
});
