const assert=require('node:assert/strict'),fs=require('node:fs'),vm=require('node:vm');
const source=fs.readFileSync('src/main/resources/static/js/anticipo-form.js','utf8');
function run(previewPresent,reduced,ready){
 const calls=[],events={},node={value:'PORCENTAJE',addEventListener(){},querySelectorAll(){return [];},querySelector(){return node;}};
 const preview={focus(o){calls.push(['focus',o.preventScroll]);},scrollIntoView(o){calls.push(['scroll',o.behavior,o.block]);}};
 const document={readyState:ready,addEventListener(){},querySelector(s){return s==='#anticipation-preview-section'?(previewPresent?preview:null):node;}};
 vm.runInNewContext(source,{document,window:{matchMedia(){return {matches:reduced};},addEventListener(k,f){events[k]=f;}},requestAnimationFrame(f){f();}});
 if(events.load)events.load();return calls;
}
assert.deepEqual(run(true,false,'loading'),[['focus',true],['scroll','smooth','start']]);
assert.deepEqual(run(true,true,'complete'),[['focus',true],['scroll','auto','start']]);
assert.deepEqual(run(false,false,'complete'),[]);
const css=fs.readFileSync('src/main/resources/static/css/forms.css','utf8');
const card=css.match(/#anticipation-selected \.anticipation-row \{([^}]+)\}/)[1];
assert.match(card,/border:2px solid/);assert.doesNotMatch(card,/border-top:/);
assert.match(card,/background:var\(--white,#fff\)/);
assert.match(css,/\[data-theme=dark\] #anticipation-selected \.anticipation-row \{[^}]*background:#0e1d31/);
console.log('Acuerdo: vista previa recibe foco y desplazamiento; respeta movimiento reducido y no desplaza formulario inicial.');
