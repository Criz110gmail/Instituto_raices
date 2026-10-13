(() => {
 const form=document.querySelector('[data-login-refresh]');if(!form)return;
 let ready=false,busy=false;
 form.addEventListener('submit',async event=>{
  if(ready){ready=false;return;}
  event.preventDefault();if(busy)return;busy=true;
  const button=form.querySelector('button[type=submit]');if(button)button.disabled=true;
  const old=form.querySelector('[data-login-refresh-error]');if(old)old.remove();
  try {
   const response=await fetch(form.dataset.csrfUrl,{credentials:'same-origin',cache:'no-store',headers:{Accept:'application/json'}});
   if(!response.ok||response.redirected)throw new Error('csrf');
   const data=await response.json();if(data.parametro!=='_csrf'||typeof data.token!=='string'||!data.token)throw new Error('csrf');
   let input=form.querySelector('input[name="_csrf"]');
   if(!input){input=document.createElement('input');input.type='hidden';input.name=data.parametro;form.append(input);}
   input.value=data.token;ready=true;form.requestSubmit();
  }catch(error){
   const notice=document.createElement('div');notice.className=form.dataset.loginRefresh==='familias'?'family-alert error':'alert warning';
   notice.dataset.loginRefreshError='';notice.setAttribute('role','alert');
   notice.textContent='No fue posible actualizar el acceso. Revisa tu conexión y vuelve a pulsar Entrar. No se enviaron tus credenciales.';
   form.prepend(notice);busy=false;
  }finally{if(button)button.disabled=false;}
 });
})();
