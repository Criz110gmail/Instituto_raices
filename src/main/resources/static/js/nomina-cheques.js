/* Ayudas de emisión. Sólo el servidor valida/consume el folio al confirmar el pago. */
(() => {
 document.querySelectorAll('.nomina-payment-source').forEach(source=>{
  const mode=source.querySelector('.nomina-source-instrument'),block=source.querySelector('[data-source-cheque]');if(!mode||!block)return;
  const account=source.querySelector('.nomina-source-grid .autocomplete-value'),medium=source.querySelector('.nomina-source-medium');
  const lookup=block.querySelector('[data-autocomplete]'),id=lookup.querySelector('.autocomplete-value'),text=lookup.querySelector('.autocomplete-input');
  const folio=block.querySelector('[data-cheque-folio]'),date=block.querySelector('[data-cheque-fecha]'),beneficiary=block.querySelector('[data-cheque-beneficiario]'),status=block.querySelector('[data-cheque-status]');
  let request,revision=0;
  const clear=()=>{revision++;request?.abort();id.value='';text.value='';folio.value='';date.value='';beneficiary.value='';lookup.querySelector('.autocomplete-results').hidden=true;};
  const bank=()=>account.value&&(account.dataset.detalle?account.dataset.detalle==='BANCO':/transfer|cheque/i.test(medium.value));
  const render=()=>{const allowed=Boolean(bank());mode.querySelector('[value=CHEQUE]').disabled=!allowed;if(!allowed&&mode.value==='CHEQUE'){mode.value='AUTO';clear();}const active=mode.value==='CHEQUE';block.hidden=!active;block.disabled=!active;
   lookup.dataset.endpoint='/admin/preparacion-nomina/cheques/chequeras?cuentaId='+encodeURIComponent(account.value);
   if(active){medium.value='Cheque';if(!beneficiary.value)beneficiary.value=source.closest('form').dataset.chequeBeneficiario||'';}else if(allowed)medium.value='Transferencia';else if(account.value&&(account.dataset.detalle==='CAJA'||/efectivo/i.test(medium.value)))medium.value='Efectivo';else medium.value='';
  };
  async function context(){const current=++revision;request?.abort();if(mode.value!=='CHEQUE'||!id.value)return;request=new AbortController();status.textContent='Consultando folios…';
   try{const r=await fetch('/admin/preparacion-nomina/cheques/contexto?'+new URLSearchParams({cuentaId:account.value,chequeraId:id.value}),{cache:'no-store',headers:{Accept:'application/json'},signal:request.signal});if(!r.ok||r.redirected)throw Error('No se pudo consultar la chequera. Revisa permisos y vuelve a seleccionarla.');const c=await r.json();if(current!==revision)return;folio.min=c.inicio;folio.max=c.fin;date.max=c.hoy;if(!folio.value&&c.proximo!=null)folio.value=c.proximo;if(!date.value)date.value=c.hoy;
    status.textContent=c.proximo==null?'No quedan folios disponibles en esta chequera. Selecciona otra.':'Primer folio disponible sugerido: '+c.proximo+'. Consultarlo no lo reserva; puedes corregirlo para coincidir con el documento físico.';
   }catch(e){if(e.name!=='AbortError'&&current===revision)status.textContent=e.message;}
  }
  mode.addEventListener('change',()=>{if(mode.value!=='CHEQUE')clear();render();if(id.value)context();});
  account.addEventListener('change',()=>{clear();render();status.textContent='Selecciona una chequera de esta cuenta bancaria.';});
  id.addEventListener('change',()=>{folio.value='';context();});render();if(id.value)context();
 });
})();
