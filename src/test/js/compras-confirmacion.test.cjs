const {test}=require('node:test'),assert=require('node:assert/strict'),fs=require('node:fs'),vm=require('node:vm');
for(const kind of ['proveedor','compra-borrador'])test(kind+': resumen, cancelar conserva y confirmar envía sólo una vez',()=>{
 class E{constructor(tag){this.tag=tag;this.children=[];this.listeners={};this.dataset={};}append(...v){this.children.push(...v);}replaceChildren(){this.children=[];}setAttribute(){}focus(){}close(){this.open=false;}showModal(){this.open=true;}addEventListener(n,f){(this.listeners[n]??=[]).push(f);}fire(n,e={}){for(const f of this.listeners[n]||[])f(e);}}
 const body=new E('body');body.classList={add(){},remove(){}};const document=new E('document');document.body=body;document.readyState='complete';document.querySelectorAll=()=>[];document.createElement=t=>new E(t);
 const fields={};for(const name of ['razonSocial','nombreComercial','rfc','contactoNombre','telefono','correo','direccion','notas','proveedorTexto','cuentaTexto','motivoTexto','fechaOperacion','documentoReferencia','observaciones'])fields[name]={value:''};
 Object.assign(fields,{institucionId:{selectedOptions:[{textContent:'Escuela de prueba'}]},plantelId:{selectedOptions:[{textContent:'Plantel de prueba'}]},activo:{checked:true}});
 fields.razonSocial.value='Papelería <prueba>';fields.proveedorTexto.value='Papelería <prueba>';fields.fechaOperacion.value='2026-10-16T12:00';fields.cuentaTexto.value='Cuenta de prueba';fields.motivoTexto.value='Papelería';
 const rowFields={'.line-description input':{value:'Papel de prueba'},'[data-quantity]':{value:'2'},'[data-price]':{value:'100'},'[data-tax]':{value:'16'},'[data-line-total]':{textContent:'$232.00'}};
 const row={querySelector:s=>rowFields[s]};const form=new E('form');form.dataset.moneyConfirm=kind;form.reportValidity=()=>true;
 form.querySelectorAll=s=>s==='[data-lines] .line-item'?[row]:[];
 form.querySelector=s=>s.startsWith('[data-')?{textContent:s==='[data-subtotal]'?'$200.00':s==='[data-taxes]'?'$32.00':'$232.00'}:fields[s.match(/name="([^"]+)"/)[1]];
 let enviados=0;function emitir(){const e={target:form,submitter:new E('button'),defaultPrevented:false,preventDefault(){this.defaultPrevented=true;}};document.fire('submit',e);if(!e.defaultPrevented)enviados++;}form.requestSubmit=emitir;
 vm.runInNewContext(fs.readFileSync('src/main/resources/static/js/money-input.js','utf8'),{document,MutationObserver:class{observe(){}},Intl,WeakMap,WeakSet,setTimeout});
 emitir();assert.equal(enviados,0);const d=body.children.find(x=>x.tag==='dialog'),details=d.children[2],actions=d.children[4];
 assert.equal(d.open,true);assert.equal(actions.children[0].textContent,'Cancelar');assert.ok(details.children.some(x=>x.textContent==='Papelería <prueba>'));
 if(kind==='compra-borrador'){assert.ok(details.children.some(x=>x.textContent.includes('Papel de prueba')));assert.ok(details.children.some(x=>x.textContent==='16/10/2026 · 12:00'));assert.ok(details.children.some(x=>x.textContent==='$232.00'));}
 actions.children[0].fire('click');assert.equal(enviados,0);assert.equal(d.open,false);
 fields.razonSocial.value='Proveedor corregido';fields.proveedorTexto.value='Proveedor corregido';emitir();assert.ok(details.children.some(x=>x.textContent==='Proveedor corregido'));
 actions.children[1].fire('click');actions.children[1].fire('click');assert.equal(enviados,1);
});
