(() => {
 document.querySelectorAll('[data-date-range]').forEach(section => {
  const desde=section.querySelector('[name="desde"]'),hasta=section.querySelector('[name="hasta"]'),preset=section.querySelector('[data-range-preset]');
  const [year,month,day]=section.dataset.today.split('-').map(Number);
  const iso=d=>`${d.getFullYear()}-${String(d.getMonth()+1).padStart(2,'0')}-${String(d.getDate()).padStart(2,'0')}`;
  function validar(){hasta.setCustomValidity(desde.value&&hasta.value&&desde.value>hasta.value?'La fecha Desde no puede ser posterior a Hasta.':'');}
  preset.addEventListener('change',()=>{
   let inicio,fin;
   if(preset.value==='month'){inicio=new Date(year,month-1,1);fin=new Date(year,month,0);}
   if(preset.value==='previous'){inicio=new Date(year,month-2,1);fin=new Date(year,month-1,0);}
   if(preset.value==='year'){inicio=new Date(year,0,1);fin=new Date(year,11,31);}
   if(inicio){desde.value=iso(inicio);hasta.value=iso(fin);} validar();
  });
  [desde,hasta].forEach(input=>input.addEventListener('input',()=>{preset.value='custom';validar();}));
  validar();
 });
})();
