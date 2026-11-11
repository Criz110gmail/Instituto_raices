const {test}=require('node:test');
const assert=require('node:assert/strict');
const fs=require('node:fs');
const vm=require('node:vm');
const source=fs.readFileSync('src/main/resources/static/js/sitio-imagen-carga.js','utf8');
function setup(){
 const nodes=new Map(),revoked=[];
 function node(){return {hidden:true,textContent:'',value:'',files:[],naturalWidth:800,naturalHeight:600,handlers:{},attrs:{},classList:{values:new Set(),add(x){this.values.add(x)},remove(x){this.values.delete(x)}},addEventListener(n,f){this.handlers[n]=f},setAttribute(k,v){this.attrs[k]=v},removeAttribute(k){delete this.attrs[k]},focus(){this.focused=true},click(){this.clicked=true},contains(){return false}};}
 for(const key of ['input[type="file"]','[data-drop-pick]','[data-drop-title]','[data-drop-selected]','[data-drop-preview]','[data-drop-name]','[data-drop-details]','[data-drop-dimensions]','[role="status"]','[role="alert"]','[data-drop-remove]','[data-drop-or]'])nodes.set(key,node());
 const zone=node();zone.querySelector=s=>nodes.get(s);
 const document={querySelector:()=>zone,addEventListener(){}};
 class DataTransfer{constructor(){this.files=[];this.items={add:f=>this.files.push(f)}}}
 let id=0;vm.runInNewContext(source,{document,DataTransfer,URL:{createObjectURL:()=>`blob:qa-${++id}`,revokeObjectURL:u=>revoked.push(u)},Intl,addEventListener(){}});
 const drop=files=>{let prevented=false;zone.handlers.drop({preventDefault(){prevented=true},dataTransfer:{files}});assert.equal(prevented,true)};
 return {zone,nodes,revoked,drop};
}
test('arrastrar un PNG lo incluye en el archivo enviado y muestra su vista previa',()=>{const {drop,nodes}=setup();const file={name:'imagen.png',type:'image/png',size:1024};drop([file]);assert.equal(nodes.get('input[type="file"]').files[0],file);assert.equal(nodes.get('[data-drop-name]').textContent,'imagen.png');assert.equal(nodes.get('[data-drop-selected]').hidden,false);nodes.get('[data-drop-preview]').onload();assert.match(nodes.get('[data-drop-dimensions]').textContent,/800 × 600/);});
test('rechaza múltiples archivos y tamaño excesivo conservando la selección válida',()=>{const {drop,nodes}=setup();const file={name:'primera.jpg',type:'image/jpeg',size:2000};drop([file]);drop([file,file]);assert.equal(nodes.get('input[type="file"]').files[0],file);assert.match(nodes.get('[role="alert"]').textContent,/una sola imagen/);drop([{...file,size:6*1024*1024}]);assert.match(nodes.get('[role="alert"]').textContent,/5 MB/);assert.equal(nodes.get('[data-drop-name]').textContent,'primera.jpg');});
test('selección nativa inválida restaura la anterior y quitar libera la vista previa',()=>{const {drop,nodes,revoked}=setup();const file={name:'foto.png',type:'image/png',size:1000};drop([file]);const input=nodes.get('input[type="file"]');input.files=[{name:'archivo.pdf',type:'application/pdf',size:1000}];input.handlers.change();assert.equal(input.files[0],file);assert.match(nodes.get('[role="alert"]').textContent,/PNG o JPEG/);nodes.get('[data-drop-remove]').handlers.click();assert.equal(input.value,'');assert.equal(nodes.get('[data-drop-selected]').hidden,true);assert.equal(revoked.length,1);});
test('detecta una imagen que no se puede abrir y dimensiones excesivas',()=>{const {drop,nodes}=setup();drop([{name:'foto.png',type:'image/png',size:1000}]);nodes.get('[data-drop-preview]').onerror();assert.equal(nodes.get('[data-drop-selected]').hidden,true);assert.match(nodes.get('[role="alert"]').textContent,/No fue posible abrir/);drop([{name:'grande.png',type:'image/png',size:1000}]);const img=nodes.get('[data-drop-preview]');img.naturalWidth=6000;img.naturalHeight=6000;img.onload();assert.match(nodes.get('[role="alert"]').textContent,/25 megapíxeles/);});
