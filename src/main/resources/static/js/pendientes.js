(() => {
 async function iniciar(){
  const enlace=document.querySelector('[data-personal-agenda]');if(!enlace)return;
  const base=enlace.getAttribute('href');
  try{
   const r=await fetch(base+'/avisos',{headers:{Accept:'application/json'},credentials:'same-origin',cache:'no-store'});
   if(!r.ok||r.redirected){if(r.status===403)enlace.hidden=true;return;}
   const a=await r.json(),contador=enlace.querySelector('[data-agenda-count]');contador.textContent=String(a.total);contador.hidden=a.total===0;
   enlace.setAttribute('aria-label','Mis pendientes: '+a.total+' vencidos o próximos a cumplir');
   const cabecera=document.querySelector('.workspace>.topbar, .teacher-shell>.teacher-hero');if(!cabecera||!a.total)return;
   const tarjeta=document.createElement('section');tarjeta.className='agenda-entry-notice';tarjeta.setAttribute('aria-label','Avisos de mis pendientes');
   const header=document.createElement('header'),titulo=document.createElement('h2'),ver=document.createElement('a');titulo.textContent='Tu agenda personal';ver.textContent='Ver mis pendientes';ver.href=base;header.append(titulo,ver);
   const resumen=document.createElement('p');resumen.textContent=a.vencidos+' vencidos · '+a.hoy+' para hoy · '+a.proximos+' para los próximos 7 días. Sólo tú puedes verlos.';
   const lista=document.createElement('ul');for(const p of a.primeros){const li=document.createElement('li'),link=document.createElement('a'),texto=document.createElement('span'),fecha=document.createElement('time');link.href=base+'/'+p.id;texto.textContent=p.titulo;fecha.dateTime=p.fechaCompromiso;fecha.textContent=p.fechaCompromiso.split('-').reverse().join('/')+(p.vencido?' · Vencido':'');link.append(texto,fecha);li.append(link);lista.append(li);}
   tarjeta.append(header,resumen,lista);cabecera.after(tarjeta);
  }catch{/* Una falla de red no impide abrir la agenda desde su enlace. */}
 }
 if(document.readyState==='loading')document.addEventListener('DOMContentLoaded',iniciar);else iniciar();
})();
