(() => {
 async function iniciar(){
  const enlace=document.querySelector('[data-personal-agenda]');if(!enlace)return;
  const base=enlace.getAttribute('href');
  try{
   const r=await fetch(base+'/avisos',{headers:{Accept:'application/json'},credentials:'same-origin',cache:'no-store'});
   if(!r.ok||r.redirected){if(r.status===403)enlace.hidden=true;return;}
   const a=await r.json(),contador=enlace.querySelector('[data-agenda-count]');contador.textContent=String(a.total);contador.hidden=a.total===0;
   enlace.setAttribute('aria-label','Mis pendientes: '+a.total+' vencidos o próximos a cumplir');
  }catch{/* Una falla de red no impide abrir la agenda desde su enlace. */}
 }
 if(document.readyState==='loading')document.addEventListener('DOMContentLoaded',iniciar);else iniciar();
})();
