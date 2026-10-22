const {test}=require('node:test'),assert=require('node:assert/strict'),fs=require('node:fs'),vm=require('node:vm');
const source=fs.readFileSync('src/main/resources/static/js/theme.js','utf8');
function entorno({saved=null,dark=false,blocked=false,ready='loading'}={}){
 const listeners={},button={dataset:{},attrs:{},listeners:{},setAttribute(k,v){this.attrs[k]=v;},addEventListener(k,v){this.listeners[k]=v;}},root={dataset:{},style:{}},loaded=[];
 const document={documentElement:root,readyState:ready,head:{appendChild(e){loaded.push({tag:e.tag,theme:root.dataset.theme,scheme:root.style.colorScheme});}},querySelector(){return null;},querySelectorAll(){return [button];},createElement(tag){return {tag,dataset:{}};},addEventListener(k,v){listeners[k]=v;}};
 const storage={getItem(){if(blocked)throw Error('Storage denied');return saved;},setItem(k,v){if(blocked)throw Error('Storage denied');saved=v;}};
 vm.runInNewContext(source,{document,location:{pathname:'/admin/empleados'},localStorage:storage,matchMedia:()=>({matches:dark})});
 if(ready==='loading')listeners.DOMContentLoaded();return {root,button,loaded,saved:()=>saved};
}
test('tema guardado se declara antes de cargar cualquier componente y prevalece sobre el sistema',()=>{
 for(const theme of ['dark','light']){const e=entorno({saved:theme,dark:theme==='light'});assert.equal(e.root.dataset.theme,theme);assert.equal(e.root.style.colorScheme,'only '+theme);assert.ok(e.loaded.length>0);assert.ok(e.loaded.every(x=>x.theme===theme&&x.scheme==='only '+theme));assert.equal(e.button.dataset.activeTheme,theme);}
});
test('primera visita y recarga tienen los mismos colores declarados; alternar persiste el esquema',()=>{
 const first=entorno({dark:true});assert.equal(first.root.style.colorScheme,'only dark');first.button.listeners.click();assert.equal(first.root.dataset.theme,'light');assert.equal(first.root.style.colorScheme,'only light');assert.equal(first.saved(),'light');assert.equal(first.button.attrs['aria-label'],'Cambiar a tema oscuro');const reload=entorno({saved:first.saved(),dark:true});assert.deepEqual(reload.root,first.root);
});
test('almacenamiento bloqueado o valor inválido no interrumpe tema, controles ni recursos',()=>{
 for(const opts of [{saved:'invalido',dark:true},{blocked:true,dark:true}]){const e=entorno(opts);assert.equal(e.root.dataset.theme,'dark');e.button.listeners.click();assert.equal(e.root.dataset.theme,'light');assert.equal(e.root.style.colorScheme,'only light');assert.ok(e.loaded.length>0);}
});
test('si el documento ya cargó también se enlaza el cambio de tema',()=>{const e=entorno({saved:'dark',ready:'complete'});e.button.listeners.click();assert.equal(e.root.style.colorScheme,'only light');});
