const assert=require('node:assert/strict'),fs=require('node:fs'),vm=require('node:vm');
const code=fs.readFileSync('src/main/resources/static/js/login-session.js','utf8');
async function run(ok){
 let handler,posts=0,prevented=0,token={value:'anterior'},notice,options;
 const button={disabled:false};
 const form={dataset:{csrfUrl:'/acceso/csrf',loginRefresh:'familias'},addEventListener(k,f){handler=f;},
  querySelector(s){return s==='button[type=submit]'?button:s==='input[name="_csrf"]'?token:null;},
  prepend(n){notice=n;},requestSubmit(){const e={preventDefault(){prevented++;}};handler(e);posts++;}};
 const document={querySelector(){return form;},createElement(){return {dataset:{},setAttribute(){}};}};
 vm.runInNewContext(code,{document,fetch:async(u,o)=>{options=o;return {ok,redirected:false,json:async()=>({parametro:'_csrf',token:'actual'})};}});
 await handler({preventDefault(){prevented++;}});
 assert.equal(prevented,1);assert.equal(button.disabled,false);assert.equal(options.cache,'no-store');assert.equal(options.credentials,'same-origin');
 if(ok){assert.equal(posts,1);assert.equal(token.value,'actual');assert.equal(notice,undefined);}
 else{assert.equal(posts,0);assert.match(notice.textContent,/No se enviaron tus credenciales/);assert.equal(token.value,'anterior');}
}
(async()=>{await run(true);await run(false);console.log('Login: actualiza token antes de un único envío; fallo de consulta no envía credenciales.');})().catch(e=>{console.error(e);process.exitCode=1;});
