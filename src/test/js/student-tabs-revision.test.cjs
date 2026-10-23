const {test}=require('node:test'),assert=require('node:assert/strict'),fs=require('node:fs'),vm=require('node:vm');
function interfaz(){
 const make=id=>({dataset:{studentTab:id,studentPanel:id,tabHash:'#'+id},listeners:{},attrs:{},setAttribute(k,v){this.attrs[k]=v;},addEventListener(k,fn){this.listeners[k]=fn;},querySelectorAll(){return this.errors||[];},focus(){this.focused=true;}});
 const tabs=['resumen','empleados','historial','gestion'].map(make),panels=['resumen','empleados','historial','gestion'].map(make),navigation={dataset:{activeTab:'resumen'},querySelectorAll(){return tabs;}};
 const handlers={},window={location:{hash:'#historial'},addEventListener(n,f){handlers[n]=f;}};let ready;
 const document={addEventListener(n,f){ready=f;},querySelector(){return navigation;},querySelectorAll(s){return s==='[data-student-panel]'?panels:[];}};
 vm.runInNewContext(fs.readFileSync('src/main/resources/static/js/student-tabs.js','utf8'),{document,window,history:{replaceState(a,b,hash){window.location.hash=hash;}}});ready();return {tabs,panels,window,handlers};
}
test('Ver revisión actual: hashchange abre Empleados sin recargar ni cambiar importes',()=>{
 const {tabs,panels,window,handlers}=interfaz();assert.equal(panels[2].hidden,false);
 window.location.hash='#empleados';handlers.hashchange();assert.equal(panels[1].hidden,false);assert.equal(panels[2].hidden,true);assert.equal(tabs[1].attrs['aria-selected'],'true');assert.equal(tabs[1].focused,true);
 tabs[2].listeners.click();assert.equal(panels[2].hidden,false);window.location.hash='#empleados';handlers.hashchange();assert.equal(panels[1].hidden,false);
});
test('Atrás/adelante por fragmento cambia panel y un fragmento ajeno no oculta contenido',()=>{
 const {panels,window,handlers}=interfaz();window.location.hash='#empleados';handlers.hashchange();window.location.hash='#historial';handlers.hashchange();assert.equal(panels[2].hidden,false);
 window.location.hash='#desconocido';handlers.hashchange();assert.equal(panels[2].hidden,false);
});
