(() => {
 const form=document.querySelector('[data-login-refresh]');if(!form)return;
 const button=form.querySelector('button[type=submit]');
 let busy=false;
 async function tokenActual(){
  for(let intento=0;intento<2;intento++){
   const response=await fetch(form.dataset.csrfUrl,{credentials:'same-origin',cache:'no-store',headers:{Accept:'application/json'}});
   if(response.redirected){
    let caducada=false;
    try{const url=new URL(response.url);caducada=url.origin===location.origin&&url.searchParams.has('sesionExpirada');}catch(error){}
    // Compatibilidad con accesos abiertos antes de actualizar la app: repetir sólo GET.
    if(caducada&&intento===0)continue;
    throw new Error(caducada?'sesion':'servicio');
   }
   if(!response.ok)throw new Error('servicio');
   let data;try{data=await response.json();}catch(error){throw new Error('servicio');}
   if(data.parametro!=='_csrf'||typeof data.token!=='string'||!data.token)throw new Error('servicio');
   return data;
  }
 }
 async function prepararAcceso(){
  for(let intento=0;intento<3;intento++){
   const data=await tokenActual();
   // Comprobar que el servidor recibe la cookie ligada al token. Sin usuario/contraseña.
   const response=await fetch(form.dataset.checkUrl||form.dataset.csrfUrl.replace(/\/csrf$/,'/comprobar'),{method:'POST',credentials:'same-origin',cache:'no-store',
    headers:{Accept:'application/json','Content-Type':'application/x-www-form-urlencoded'},
    body:new URLSearchParams({[data.parametro]:data.token})});
   if(!response.redirected&&response.status===204)return data;
   if(response.status===409)continue;
   if(response.redirected){
    let local=false;try{const url=new URL(response.url);local=url.origin===location.origin&&url.searchParams.has('sesionExpirada');}catch(error){}
    if(local)continue;
   }
   throw new Error('servicio');
  }
  throw new Error('sesion');
 }
 form.addEventListener('submit',async event=>{
  event.preventDefault();if(busy)return;busy=true;
  if(button)button.disabled=true;
  const old=form.querySelector('[data-login-refresh-error]');if(old)old.remove();
  try {
   const data=await prepararAcceso();
   let input=form.querySelector('input[name="_csrf"]');
   if(!input){input=document.createElement('input');input.type='hidden';input.name=data.parametro;form.append(input);}
   input.value=data.token;
   if(!form.reportValidity()){busy=false;return;}
   // Un único envío nativo; no disparar un segundo evento submit ni reusar token de otro controlador.
   HTMLFormElement.prototype.submit.call(form);
  }catch(error){
   const notice=document.createElement('div');notice.className=form.dataset.loginRefresh==='familias'?'family-alert error':'alert warning';
   notice.dataset.loginRefreshError='';notice.setAttribute('role','alert');
   notice.textContent=error.message==='sesion'?'La sesión volvió a cambiar durante el acceso. Vuelve a pulsar Entrar. No se enviaron tus credenciales.':error.message==='servicio'?'El servicio de acceso no está disponible en este momento. Vuelve a intentarlo. No se enviaron tus credenciales.':'No fue posible conectar con el sistema. Revisa tu conexión y vuelve a pulsar Entrar. No se enviaron tus credenciales.';
   form.prepend(notice);busy=false;
  }finally{if(button)button.disabled=false;}
 });
 // Sin el controlador listo no enviar un formulario con token vacío o antiguo.
 if(button)button.disabled=false;
})();
