const assert=require('node:assert/strict'),fs=require('node:fs'),vm=require('node:vm');
const code=fs.readFileSync('src/main/resources/static/js/login-session.js','utf8');
const fresh={ok:true,redirected:false,json:async()=>({parametro:'_csrf',token:'actual'})};
const expired={ok:true,redirected:true,url:'http://localhost:18080/login?sesionExpirada'};
const checked={status:204,redirected:false},changed={status:409,redirected:false};
async function run(responses,expectedPosts,expectedRequests,message){
 let handler,posts=0,requests=0,prevented=0,token={value:'anterior'},notice;
 const button={disabled:true};
 const form={dataset:{csrfUrl:'/acceso/csrf',checkUrl:'/acceso/comprobar',loginRefresh:'familias'},addEventListener(k,f){handler=f;},
  querySelector(s){return s==='button[type=submit]'?button:s==='input[name="_csrf"]'?token:null;},
  prepend(n){notice=n;},reportValidity(){return true;}};
 const document={querySelector(){return form;},createElement(){return {dataset:{},setAttribute(){}};}};
 vm.runInNewContext(code,{document,URL,URLSearchParams,HTMLFormElement:{prototype:{submit(){posts++;}}},location:{origin:'http://localhost:18080'},fetch:async(u,o)=>{
  assert.equal(o.cache,'no-store');assert.equal(o.credentials,'same-origin');
  if(u==='/acceso/comprobar'){
   assert.equal(o.method,'POST');assert.equal(o.body.toString(),'_csrf=actual');
   assert.equal(o.body.has('username'),false);assert.equal(o.body.has('password'),false);
  }else{assert.equal(u,'/acceso/csrf');assert.equal(o.body,undefined);}
  const response=responses[requests++];if(response instanceof Error)throw response;return response;
 }});
 assert.equal(button.disabled,false);assert.equal(requests,0);assert.equal(posts,0);
 await handler({preventDefault(){prevented++;}});
 assert.equal(prevented,1);assert.equal(button.disabled,false);assert.equal(requests,expectedRequests);assert.equal(posts,expectedPosts);
 if(expectedPosts){assert.equal(token.value,'actual');assert.equal(notice,undefined);}
 else{assert.equal(token.value,'anterior');assert.match(notice.textContent,/No se enviaron tus credenciales/);assert.match(notice.textContent,message);}
}
(async()=>{
 await run([fresh,checked],1,2);
 await run([expired,fresh,checked],1,3);
 await run([fresh,changed,fresh,checked],1,4);
 await run([fresh,changed,fresh,changed,fresh,changed],0,6,/sesión volvió a cambiar/);
 await run([fresh,{...expired,url:'https://otro.example/login'}],0,2,/servicio de acceso/);
 await run([expired,expired],0,2,/sesión volvió a cambiar/);
 await run([new TypeError('red')],0,1,/conectar/);
 await run([{ok:false,redirected:false}],0,1,/servicio de acceso/);
 await run([{...expired,url:'http://otro.example/login?sesionExpirada'}],0,1,/servicio de acceso/);
 await run([{...expired,url:'http://localhost:18080/login?error'}],0,1,/servicio de acceso/);
 console.log('Login: cookie caducada reintenta sólo GET una vez; credenciales se envían una vez; fallos/redirects externos no envían ni repiten.');
})().catch(e=>{console.error(e);process.exitCode=1;});
