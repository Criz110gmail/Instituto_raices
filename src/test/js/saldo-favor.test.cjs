const assert=require('node:assert/strict');
const fs=require('node:fs');
const vm=require('node:vm');
class Element {
 constructor(tag='div'){this.tag=tag;this.children=[];this.listeners={};this.dataset={};this.value='';this.textContent='';}
 addEventListener(name,fn){(this.listeners[name] ||= []).push(fn);}
 fire(name,event={}){for(const fn of this.listeners[name]||[])fn(event);}
 append(...children){for(const c of children){c.parent=this;this.children.push(c);}}
 replaceChildren(){this.children=[];}
 setAttribute(){}
 contains(e){return e===this||this.children.some(c=>c.contains(e));}
 focus(){}
 remove(){this.parent.children=this.parent.children.filter(c=>c!==this);}
 querySelectorAll(selector){return this.children.flatMap(c=>[(c.className||'').split(' ').includes(selector.slice(1))?c:null,...c.querySelectorAll(selector)]).filter(Boolean);}
 querySelector(selector){return this.querySelectorAll(selector)[0];}
}
const fields=Object.fromEntries(['credit-form','credit-query','credit-results','credit-status','credit-rows','credit-total','credit-remaining','credit-clear','credit-search'].map(k=>[k,new Element()]));
fields['credit-form'].dataset={available:'0.30',currency:'MXN',endpoint:'/admin/pagos/50/saldo-favor/cargos'};
function row(id,amount){const r=new Element();r.className='saldo-row';for(const [cls,value] of [['credit-id',id],['credit-label','Hijo '+id],['credit-amount',amount],['credit-remove','']]){const e=new Element();e.className=cls;e.value=value;r.append(e);}fields['credit-rows'].append(r);return r;}
const a=row('7','0.10'),b=row('8','0.20'),calls=[];
const document={querySelector:s=>fields[s.slice(1)],createElement:t=>new Element(t),addEventListener(){}};
vm.runInNewContext(fs.readFileSync('src/main/resources/static/js/saldo-favor.js','utf8'),{document,Intl,AbortController,URLSearchParams,clearTimeout,setTimeout:fn=>{fn();return 1;},fetch:async url=>{calls.push(url);return {ok:true,redirected:false,json:async()=>({resultados:[{id:8,titulo:'Ya elegido',monto:'0.20',detalle:'MXN'},{id:9,titulo:'Hermano · Cuota',monto:'0.10',detalle:'MXN'}]})};}});
const flush=()=>new Promise(setImmediate);
(async()=>{
 assert.equal(fields['credit-total'].textContent,'$0.30');assert.equal(fields['credit-remaining'].textContent,'$0.00');
 let prevented=false;fields['credit-form'].fire('submit',{preventDefault(){prevented=true;}});assert.equal(prevented,false,'0.10 + 0.20 no debe exceder 0.30');
 fields['credit-query'].fire('focus');await flush();assert.match(calls.at(-1),/q=$/);
 assert.equal(fields['credit-results'].children.length,1,'El cargo ya seleccionado debe desaparecer de la ayuda');
 a.querySelector('.credit-remove').fire('click');assert.equal(fields['credit-remaining'].textContent,'$0.10');
 const option=fields['credit-results'].children[0];option.fire('click');
 assert.equal(fields['credit-rows'].querySelectorAll('.credit-id').length,2);assert.equal(fields['credit-total'].textContent,'$0.30');
 option.fire('click');assert.equal(fields['credit-rows'].querySelectorAll('.credit-id').length,2,'Doble clic no duplica cargo');
 assert.equal(b.querySelector('.credit-id').name,'cargos[0].cargoId');
 const before=calls.length;fields['credit-query'].value='ab';fields['credit-query'].fire('input');await flush();assert.equal(calls.length,before);
 fields['credit-query'].value='ana';fields['credit-query'].fire('input');await flush();assert.match(calls.at(-1),/q=ana$/);
 b.querySelector('.credit-amount').value='0.50';b.querySelector('.credit-amount').fire('input');prevented=false;fields['credit-form'].fire('submit',{preventDefault(){prevented=true;}});assert.equal(prevented,true);
 console.log('Saldo a favor: foco primeras opciones, tres caracteres, duplicados, centavos y exceso verificados.');
})().catch(error=>{console.error(error);process.exitCode=1;});
