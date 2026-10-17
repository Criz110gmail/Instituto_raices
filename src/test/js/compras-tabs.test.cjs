const {test}=require('node:test'),assert=require('node:assert/strict'),fs=require('node:fs'),vm=require('node:vm');
for(const capture of [false,true])test('compras: pestañas '+(capture?'de captura':'de detalle')+', hash, errores y gestión independiente',()=>{
 const ids=capture?['datos','partidas']:['resumen','productos','gestion'];
 const e=id=>({dataset:{studentTab:id,studentPanel:id,tabHash:'#'+(capture?'captura':'compra')+'-'+id},listeners:{},attributes:{},addEventListener(k,f){this.listeners[k]=f;},setAttribute(k,v){this.attributes[k]=v;},focus(){this.focused=true;},querySelectorAll(){return this.errors||[];}});
 const tabs=ids.map(e),panels=ids.map(e),nav={dataset:{activeTab:ids[0]},querySelectorAll:()=>tabs};let ready,hash;
 const link={dataset:{openStudentTab:ids.at(-1),focusStudentTab:'true'},listeners:{},addEventListener(k,f){this.listeners[k]=f;}};
 const document={addEventListener(k,f){ready=f;},querySelector:()=>nav,querySelectorAll:s=>s==='[data-student-panel]'?panels:[link]};
 vm.runInNewContext(fs.readFileSync('src/main/resources/static/js/student-tabs.js','utf8'),{document,window:{location:{hash:''}},history:{replaceState(a,b,h){hash=h;}}});
 ready();assert.equal(panels[0].hidden,false);assert.equal(panels.at(-1).hidden,true);
 tabs.at(-1).listeners.click();assert.equal(panels.at(-1).hidden,false);assert.equal(panels[0].hidden,true);assert.equal(hash,tabs.at(-1).dataset.tabHash);
 tabs.at(-1).listeners.keydown({key:'ArrowRight',preventDefault(){}});assert.equal(panels[0].hidden,false);
 link.listeners.click();assert.equal(panels.at(-1).hidden,false);assert.equal(tabs.at(-1).focused,true);
 panels.at(-1).errors=[{textContent:'Revisa este campo'}];ready();assert.equal(panels.at(-1).hidden,false);
});
