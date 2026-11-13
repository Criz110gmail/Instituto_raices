/* La institución procede de la sesión; nunca de localStorage o de una URL compartida. */
(() => {
 'use strict';
 const start=async()=>{
  if(document.body?.dataset.piPreview==='true'||document.body?.dataset.piPersonalizada!==undefined)return;
  const route=location.pathname;
  const portal=/^\/admin(?:\/|$)/.test(route)?'admin':/^\/portal(?:\/|$)/.test(route)?'familias':/^\/maestros(?:\/|$)/.test(route)?'maestros':/^\/personal(?:\/|$)/.test(route)?'personal':null;
  if(!portal||['/maestros/acceso','/personal/acceso'].includes(route))return;
  try{
   const response=await fetch('/sesion/identidad?portal='+portal,{credentials:'same-origin',cache:'no-store',headers:{Accept:'application/json'}});
   if(!response.ok)return;
   let data=await response.json();
   if(data.original===true&&portal==='admin'&&window.NexoPortalColores)data={...data,...window.NexoPortalColores.adminDefaults,original:false};
   if(data.portal!==portal||data.original!==false||![data.principal,data.fondo,data.acento].every(c=>/^#[0-9a-f]{6}$/i.test(c)))return;
   const root=document.documentElement;
   const refresh=()=>window.NexoPortalColores.apply(root,data,root.dataset.theme);
   if(!window.NexoPortalColores||!refresh())return;
   new MutationObserver(refresh).observe(root,{attributes:true,attributeFilter:['data-theme']});
   root.dataset.piInterior='true';root.dataset.piPortal=portal;
   const css=document.createElement('link');css.rel='stylesheet';css.href='/css/portal-interior.css?v=20261112-adaptativo';document.head.append(css);
  }catch(_){/* Si no está disponible, conservar el diseño habitual. */}
 };
 if(document.readyState==='loading')document.addEventListener('DOMContentLoaded',start,{once:true});else start();
})();
