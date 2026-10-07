const assert=require('node:assert/strict'),fs=require('node:fs'),vm=require('node:vm');
const ids=['resumen','transferencias','ajustes','historial','gestion'];
const create=id=>({dataset:{studentTab:id,studentPanel:id,tabHash:'#'+id+'-cargo'},listeners:{},attrs:{},
    addEventListener(k,fn){this.listeners[k]=fn;},setAttribute(k,v){this.attrs[k]=v;},focus(){this.focused=true;},querySelectorAll(){return this.errors||[];}});
const tabs=ids.map(create),panels=ids.map(create),nav={dataset:{activeTab:'resumen',forceActiveTab:'ajustes'},querySelectorAll(){return tabs;}};
let ready,hash;
const doc={addEventListener(k,fn){ready=fn;},querySelector(){return nav;},querySelectorAll(s){return s==='[data-student-panel]'?panels:[];}};
vm.runInNewContext(fs.readFileSync('src/main/resources/static/js/student-tabs.js','utf8'),{document:doc,window:{location:{hash:'#resumen-cargo'}},history:{replaceState(a,b,c){hash=c;}}});
ready();assert.equal(panels[2].hidden,false);assert.equal(panels[0].hidden,true);
tabs[4].listeners.click();assert.equal(panels[4].hidden,false);assert.equal(hash,'#gestion-cargo');
tabs[4].listeners.keydown({key:'ArrowRight',preventDefault(){}});assert.equal(panels[0].hidden,false);assert.equal(tabs[0].focused,true);
nav.dataset.forceActiveTab='gestion';panels[2].errors=[{textContent:'Indica el monto'}];ready();assert.equal(panels[2].hidden,false);
console.log('Pestañas: error abre Ajustar saldo aunque haya hash, clic Gestión y teclado conservan navegación.');
