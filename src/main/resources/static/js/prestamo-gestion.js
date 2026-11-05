/* Submenú exclusivo de Préstamos: independiente del menú principal de detalle. */
document.addEventListener('DOMContentLoaded',()=>{
 const root=document.querySelector('[data-prestamo-gestion]');if(!root)return;
 const tabs=[...root.querySelectorAll('[data-prestamo-gestion-tab]')];
 const panels=[...root.querySelectorAll('[data-prestamo-gestion-panel]')];
 if(!tabs.length)return;
 const activate=name=>{
  const selected=tabs.some(t=>t.dataset.prestamoGestionTab===name)?name:tabs[0].dataset.prestamoGestionTab;
  for(const tab of tabs){const active=tab.dataset.prestamoGestionTab===selected;tab.setAttribute('aria-selected',String(active));tab.tabIndex=active?0:-1;}
  for(const panel of panels)panel.hidden=panel.dataset.prestamoGestionPanel!==selected;
 };
 for(const [index,tab] of tabs.entries()){
  tab.addEventListener('click',()=>activate(tab.dataset.prestamoGestionTab));
  tab.addEventListener('keydown',event=>{
   if(!['ArrowLeft','ArrowRight','Home','End'].includes(event.key))return;
   event.preventDefault();const i=event.key==='Home'?0:event.key==='End'?tabs.length-1:(index+(event.key==='ArrowRight'?1:-1)+tabs.length)%tabs.length;
   activate(tabs[i].dataset.prestamoGestionTab);tabs[i].focus();
  });
 }
 activate(root.dataset.activeSection);
});
