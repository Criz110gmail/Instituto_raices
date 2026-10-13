const assert=require('node:assert/strict'),fs=require('node:fs'),vm=require('node:vm');
const create=id=>({dataset:{studentTab:id,studentPanel:id,tabHash:'#'+(id==='anual'?'cobertura-anual':id)+'-pago'},listeners:{},attrs:{},
 addEventListener(k,f){this.listeners[k]=f;},setAttribute(k,v){this.attrs[k]=v;},focus(){this.focused=true;},querySelectorAll(){return [];}});
const tabs=['resumen','distribucion','anual'].map(create),panels=['resumen','distribucion','anual'].map(create);
const link=create('abrir');link.dataset.openStudentTab='anual';link.dataset.focusStudentTab='true';
const nav={dataset:{activeTab:'resumen'},querySelectorAll(){return tabs;}};let ready,hash;
const doc={addEventListener(k,f){ready=f;},querySelector(){return nav;},querySelectorAll(s){return s==='[data-student-panel]'?panels:[link];}};
vm.runInNewContext(fs.readFileSync('src/main/resources/static/js/student-tabs.js','utf8'),{document:doc,window:{location:{hash:''}},history:{replaceState(a,b,c){hash=c;}}});
ready();assert.equal(panels[0].hidden,false);assert.equal(panels[2].hidden,true);
link.listeners.click();assert.equal(panels[2].hidden,false);assert.equal(panels[0].hidden,true);assert.equal(tabs[2].focused,true);
tabs[2].listeners.click();assert.equal(hash,'#cobertura-anual-pago');
tabs[2].listeners.keydown({key:'ArrowRight',preventDefault(){}});assert.equal(panels[0].hidden,false);assert.equal(tabs[0].focused,true);
console.log('Cobertura anual: resumen abre pestaña y transfiere foco; hash, flechas y vuelta a Resumen correctos.');
