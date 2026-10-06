const fs=require('node:fs'),vm=require('node:vm'),assert=require('node:assert/strict');
const source=fs.readFileSync('src/main/resources/static/js/generation-selection.js','utf8');
class E {
 constructor(){this.dataset={};this.listeners={};this.children=[];this.checked=true;this.classList={toggle(){}};}
 addEventListener(n,f){this.listeners[n]=f;}closest(){return new E();}replaceChildren(){this.children=[];}appendChild(e){this.children.push(e);}
}
const saved=new Map();
function run(ids=['RECARGO:19','RECARGO:20'],storage={getItem:k=>saved.get(k)||null,setItem:(k,v)=>saved.set(k,v)}){
 const panel=new E(),form=new E(),button=new E(),hidden=new E(),page=new E(),all=new E(),none=new E(),summary=new E();
 panel.dataset={selectionId:'test-token',selectionTotal:'3',selectionAmount:'180.00'};
 const rows=ids.map(id=>{const e=new E();e.value=id;e.dataset.selectionAmount=id==='RECARGO:20'?'80':'50';return e;});
 panel.querySelector=s=>({'[data-selection-confirm]':form,'[data-select-page]':page,'[data-selection-all]':all,'[data-selection-none]':none}[s]);
 panel.querySelectorAll=s=>s==='[data-selection-row]'?rows:[summary];
 form.querySelector=s=>s==='button[type="submit"]'?button:hidden;
 const document={querySelector:()=>panel,createElement:()=>new E()};
 vm.runInNewContext(source,{document,sessionStorage:storage,Intl,Number,Map,JSON});
 return{rows,button,page,all,none,summary,form,hidden};
}
let ui=run();assert.equal(ui.button.disabled,false);assert.match(ui.summary.textContent,/3 de 3.*180\.00/);
ui.rows[1].checked=false;ui.rows[1].listeners.change();assert.match(ui.summary.textContent,/2 de 3.*100\.00/);
ui=run(['RECARGO:21']);assert.match(ui.summary.textContent,/2 de 3.*100\.00/);assert.equal(ui.rows[0].checked,true);
ui=run();assert.equal(ui.rows[1].checked,false);assert.equal(ui.page.indeterminate,true);
ui.none.listeners.click();assert.equal(ui.button.disabled,true);
let stopped=false;ui.form.listeners.submit({preventDefault(){stopped=true;}});assert.equal(stopped,true);
ui.rows[0].checked=true;ui.rows[0].listeners.change();assert.match(ui.summary.textContent,/1 de 3.*50\.00/);
ui.form.listeners.submit({preventDefault(){throw new Error('Selección válida');}});
assert.deepEqual(ui.hidden.children.map(e=>[e.name,e.value]),[['seleccionIndividual','true'],['incluidos','RECARGO:19']]);
ui=run();assert.equal(ui.rows[0].checked,true);assert.equal(ui.rows[1].checked,false);
ui.all.listeners.click();assert.match(ui.summary.textContent,/3 de 3.*180\.00/);
ui=run(undefined,{getItem:()=>'{malformed',setItem(){}});assert.equal(ui.button.disabled,true);
ui=run(undefined,{getItem:()=>null,setItem(){throw new Error('No disponible');}});assert.equal(ui.button.disabled,true);
console.log('Selección: todos, exclusiones entre páginas, sólo un registro, importe, cero selección y almacenamiento fallido correctos.');
