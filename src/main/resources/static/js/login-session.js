(() => {
 const form=document.querySelector('[data-login-refresh]');if(!form)return;
 let ready=false,busy=false;
 async function tokenActual(){
  for(let intento=0;intento<2;intento++){
   const response=await fetch(form.dataset.csrfUrl,{credentials:'same-origin',cache:'no-store',headers:{Accept:'application/json'}});
   if(response.redirected){
    let caducada=false;
    try{const url=new URL(response.url);caducada=url.origin===location.origin&&url.searchParams.has('sesionExpirada');}catch(error){}
    // Repetir sólo GET: la redirección eliminó la cookie caducada.
    if(caducada&&intento===0)continue;
    throw new Error(caducada?'sesion':'servicio');
   }
   if(!response.ok)throw new Error('servicio');
   let data;try{data=await response.json();}catch(error){throw new Error('servicio');}
   if(data.parametro!=='_csrf'||typeof data.token!=='string'||!data.token)throw new Error('servicio');
   return data;
  }
 }
 form.addEventListener('submit',async event=>{
  if(ready){ready=false;return;}
  event.preventDefault();if(busy)return;busy=true;
  const button=form.querySelector('button[type=submit]');if(button)button.disabled=true;
  const old=form.querySelector('[data-login-refresh-error]');if(old)old.remove();
  try {
   const data=await tokenActual();
   let input=form.querySelector('input[name="_csrf"]');
   if(!input){input=document.createElement('input');input.type='hidden';input.name=data.parametro;form.append(input);}
   input.value=data.token;ready=true;form.requestSubmit();
  }catch(error){
   const notice=document.createElement('div');notice.className=form.dataset.loginRefresh==='familias'?'family-alert error':'alert warning';
   notice.dataset.loginRefreshError='';notice.setAttribute('role','alert');
   notice.textContent=error.message==='sesion'?'La sesión volvió a cambiar durante el acceso. Vuelve a pulsar Entrar. No se enviaron tus credenciales.':error.message==='servicio'?'El servicio de acceso no está disponible en este momento. Vuelve a intentarlo. No se enviaron tus credenciales.':'No fue posible conectar con el sistema. Revisa tu conexión y vuelve a pulsar Entrar. No se enviaron tus credenciales.';
   form.prepend(notice);busy=false;
  }finally{if(button)button.disabled=false;}
 });
})();
