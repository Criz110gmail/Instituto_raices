const {test}=require('node:test'),assert=require('node:assert/strict'),fs=require('node:fs'),vm=require('node:vm');
test('corte: resumen inicial, navegación/hash/teclado y errores abren conteo y cierre',()=>{
 const ids=['resumen','apertura','cierre'];
 const create=id=>({dataset:{studentTab:id,studentPanel:id,tabHash:'#'+id+'-corte'},listeners:{},attrs:{},addEventListener(k,fn){this.listeners[k]=fn;},setAttribute(k,v){this.attrs[k]=v;},focus(){this.focused=true;},querySelectorAll(){return this.errors||[];}});
 const tabs=ids.map(create),panels=ids.map(create),nav={dataset:{activeTab:'resumen'},querySelectorAll(){return tabs;}};
 const open=create('shortcut');open.dataset.openStudentTab='cierre';open.dataset.focusStudentTab='true';
 let ready,hash;
 const doc={addEventListener(k,fn){ready=fn;},querySelector(){return nav;},querySelectorAll(s){return s==='[data-student-panel]'?panels:[open];}};
 const win={location:{hash:''}};
 vm.runInNewContext(fs.readFileSync('src/main/resources/static/js/student-tabs.js','utf8'),{document:doc,window:win,history:{replaceState(a,b,c){hash=c;}}});
 ready();assert.equal(panels[0].hidden,false);assert.equal(panels[1].hidden,true);assert.equal(panels[2].hidden,true);
 tabs[1].listeners.click();assert.equal(panels[1].hidden,false);assert.equal(hash,'#apertura-corte');assert.equal(tabs[1].attrs['aria-selected'],'true');
 tabs[1].listeners.keydown({key:'ArrowRight',preventDefault(){}});assert.equal(panels[2].hidden,false);assert.equal(tabs[2].focused,true);
 tabs[2].listeners.keydown({key:'ArrowRight',preventDefault(){}});assert.equal(panels[0].hidden,false);
 open.listeners.click();assert.equal(panels[2].hidden,false);assert.equal(tabs[2].focused,true);
 win.location.hash='#apertura-corte';ready();assert.equal(panels[1].hidden,false);
 nav.dataset.forceActiveTab='cierre';ready();assert.equal(panels[2].hidden,false);
 delete nav.dataset.forceActiveTab;panels[2].errors=[{textContent:'Captura el efectivo declarado'}];ready();assert.equal(panels[2].hidden,false);
});
