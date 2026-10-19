const {test}=require('node:test'),assert=require('node:assert/strict'),fs=require('node:fs'),vm=require('node:vm');
test('planeación: versiones accesibles por pestaña/hash, enfoque al inicio y publicación abre PDF',()=>{
 const ids=['documento','revision','versiones','historial'];
 const make=id=>({dataset:{studentTab:id,studentPanel:id,tabHash:'#'+id},attrs:{},events:{},addEventListener(k,f){this.events[k]=f;},setAttribute(k,v){this.attrs[k]=v;},focus(){this.focused=true;},querySelectorAll(){return [];}});
 const tabs=ids.map(make),panels=ids.map(make),scrolls=[];
 const nav={dataset:{activeTab:'revision',scrollToTabs:'true'},querySelectorAll(){return tabs;},scrollIntoView(options){scrolls.push(options);}};
 let ready,hash='';const win={location:{hash:''},matchMedia(){return {matches:false};}};
 const document={addEventListener(event,callback){ready=callback;},querySelector(){return nav;},querySelectorAll(selector){return selector==='[data-student-panel]'?panels:[];}};
 vm.runInNewContext(fs.readFileSync('src/main/resources/static/js/student-tabs.js','utf8'),{document,window:win,history:{replaceState(a,b,value){hash=value;}}});
 ready();assert.equal(panels[1].hidden,false);assert.equal(panels[2].hidden,true);
 tabs[2].events.click();assert.equal(panels[2].hidden,false);assert.equal(panels[1].hidden,true);assert.equal(hash,'#versiones');assert.equal(scrolls[0].block,'start');assert.equal(scrolls[0].behavior,'smooth');
 win.matchMedia=()=>({matches:true});tabs[2].events.keydown({key:'ArrowRight',preventDefault(){}});assert.equal(panels[3].hidden,false);assert.equal(tabs[3].focused,true);assert.equal(scrolls[1].behavior,'auto');
 win.location.hash='#historial';nav.dataset.forceActiveTab='versiones';ready();assert.equal(panels[2].hidden,false);
 nav.dataset.forceActiveTab='revision';ready();assert.equal(panels[1].hidden,false);
 delete nav.dataset.forceActiveTab;win.location.hash='#versiones';ready();assert.equal(panels[2].hidden,false);
});
