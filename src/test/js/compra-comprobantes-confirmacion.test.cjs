const {test}=require('node:test'),assert=require('node:assert/strict'),fs=require('node:fs'),vm=require('node:vm');
for(const kind of ['compra-documento','compra-respaldo','compra-documento-retirar','compra-cfdi-importar'])test(kind+': cancelar y Escape conservan captura; confirmar envía una vez',()=>{
 class E{constructor(tag){this.tag=tag;this.children=[];this.listeners={};this.dataset={};}append(...v){this.children.push(...v);}replaceChildren(){this.children=[];}setAttribute(){}focus(){}close(){this.open=false;}showModal(){this.open=true;}addEventListener(n,f){(this.listeners[n]??=[]).push(f);}fire(n,e={}){for(const f of this.listeners[n]||[])f(e);}}
 const document=new E('document');document.body=new E('body');document.readyState='complete';document.body.classList={add(){},remove(){}};document.querySelectorAll=()=>[];document.createElement=t=>new E(t);
 const form=new E('form');form.dataset={moneyConfirm:kind,confirmAmount:'232.00',confirmUuid:'AAAAAAAA-AAAA-4AAA-8AAA-AAAAAAAAAAAA'};form.querySelectorAll=()=>[];
 form.querySelector=s=>s.includes('archivo')?{files:[{name:'comprobante-ficticio.xml'}]}:{value:'Justificación de prueba',selectedOptions:[{textContent:'Factura CFDI'}]};form.reportValidity=()=>true;
 let enviados=0;function enviar(){const e={target:form,submitter:new E('button'),defaultPrevented:false,preventDefault(){this.defaultPrevented=true;}};document.fire('submit',e);if(!e.defaultPrevented)enviados++;}form.requestSubmit=enviar;
 vm.runInNewContext(fs.readFileSync('src/main/resources/static/js/money-input.js','utf8'),{document,MutationObserver:class{observe(){}},Intl,WeakMap,WeakSet,setTimeout});
 enviar();const d=document.body.children.find(e=>e.tag==='dialog');assert.equal(d.open,true);assert.equal(enviados,0);const actions=d.children[4];actions.children[0].fire('click');assert.equal(enviados,0);
 enviar();d.fire('cancel',{preventDefault(){}});assert.equal(enviados,0);enviar();actions.children[1].fire('click');actions.children[1].fire('click');assert.equal(enviados,1);
 assert.ok(d.children[3].textContent.includes('no')||d.children[3].textContent.includes('No'));
});
