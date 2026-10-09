(() => {
 const form=document.querySelector('#credit-form'); if(!form)return;
 const input=document.querySelector('#credit-query'),results=document.querySelector('#credit-results'),status=document.querySelector('#credit-status'),rows=document.querySelector('#credit-rows');
 const available=Number(form.dataset.available),currency=form.dataset.currency||'MXN',money=v=>new Intl.NumberFormat('es-MX',{style:'currency',currency}).format(v),cents=v=>Math.round((Number(v)||0)*100);
 let timer,abort,version=0;
 const selected=()=>[...rows.querySelectorAll('.credit-id')].map(e=>e.value);
 function update(){let total=0;rows.querySelectorAll('.saldo-row').forEach((r,i)=>{r.querySelector('.credit-id').name=`cargos[${i}].cargoId`;r.querySelector('.credit-label').name=`cargos[${i}].cargoEtiqueta`;const a=r.querySelector('.credit-amount');a.name=`cargos[${i}].montoSolicitado`;total+=cents(a.value);});document.querySelector('#credit-total').textContent=money(total/100);document.querySelector('#credit-remaining').textContent=money((cents(available)-total)/100);form.dataset.confirmAmount=(total/100).toFixed(2);}
 function connect(row){row.querySelector('.credit-remove').addEventListener('click',()=>{row.remove();update();});row.querySelector('.credit-amount').addEventListener('input',update);}
 rows.querySelectorAll('.saldo-row').forEach(connect);update();
 async function search(q){abort?.abort();abort=new AbortController();const seq=++version;status.textContent='Buscando…';try{
   const r=await fetch(form.dataset.endpoint+'?'+new URLSearchParams({q}),{signal:abort.signal,headers:{Accept:'application/json'}});
   if(r.redirected){location.assign('/login?sesionExpirada');return;}if(!r.ok)throw new Error();const data=await r.json();if(seq!==version)return;results.replaceChildren();
   (data.resultados||[]).filter(o=>!selected().includes(String(o.id))).forEach(o=>{const b=document.createElement('button');b.type='button';b.className='autocomplete-option';const t=document.createElement('strong'),d=document.createElement('small');t.textContent=o.titulo;d.textContent=money(Number(o.monto))+' · '+o.detalle;b.append(t,d);b.addEventListener('click',()=>{
      if(seq!==version||selected().includes(String(o.id)))return;if(selected().length>=20){status.textContent='Máximo veinte cargos por operación.';return;}
      const used=[...rows.querySelectorAll('.credit-amount')].reduce((s,e)=>s+cents(e.value),0),max=Math.min(cents(o.monto),cents(available)-used)/100;if(max<=0){status.textContent='Ya distribuiste todo el saldo disponible.';return;}
      const row=document.createElement('article');row.className='saldo-row';const body=document.createElement('div'),name=document.createElement('strong');name.className='credit-title';name.textContent=o.titulo;
      const id=document.createElement('input');id.type='hidden';id.className='credit-id';id.value=String(o.id);const label=document.createElement('input');label.type='hidden';label.className='credit-label';label.value=o.titulo;
      const field=document.createElement('label'),title=document.createElement('span'),amount=document.createElement('input');title.textContent='Importe a aplicar *';amount.type='text';amount.className='credit-amount';amount.dataset.money='';amount.inputMode='decimal';amount.min='0.01';amount.max=String(o.monto);amount.step='0.01';amount.required=true;amount.value=max.toFixed(2);field.append(title,amount);
      const remove=document.createElement('button');remove.type='button';remove.className='credit-remove change-destination-button';remove.textContent='Quitar';body.append(name,id,label,field);row.append(body,remove);rows.append(row);connect(row);input.value='';results.hidden=true;input.setAttribute('aria-expanded','false');status.textContent='Cargo agregado. Puedes buscar otro hermano o concepto.';update();
   });results.append(b);});results.hidden=!results.children.length;input.setAttribute('aria-expanded',String(!results.hidden));status.textContent=results.children.length?'Selecciona un cargo; ya elegidos no se repiten.':'No hay otros cargos disponibles del mismo plantel y moneda.';
 }catch(e){if(e.name!=='AbortError'&&seq===version)status.textContent='No fue posible consultar los adeudos.';}}
 const open=()=>{const q=input.value.trim();search(q.length>=3?q:'');};input.addEventListener('focus',open);input.addEventListener('click',open);
 input.addEventListener('input',()=>{clearTimeout(timer);abort?.abort();version++;results.hidden=true;const q=input.value.trim();if(!q)search('');else if(q.length>=3)timer=setTimeout(()=>search(q),280);else status.textContent='Escribe al menos tres caracteres.';});
 document.querySelector('#credit-clear').addEventListener('click',()=>{input.value='';input.focus();open();});
 document.addEventListener('click',e=>{if(!document.querySelector('#credit-search').contains(e.target)){results.hidden=true;input.setAttribute('aria-expanded','false');}});
 form.addEventListener('submit',e=>{const sum=[...rows.querySelectorAll('.credit-amount')].reduce((s,a)=>s+cents(a.value),0);if(!selected().length||sum<=0||sum>cents(available)){e.preventDefault();status.textContent='Selecciona cargos e importes sin superar el saldo disponible.';input.focus();}});
})();
