(function(root){'use strict';
 const address=value=>String(value||'').trim().slice(0,500);
 const zoom=value=>Math.max(10,Math.min(19,Math.round(Number(value)||16)));
 const embed=(value,level=16)=>'https://maps.google.com/maps?'+new URLSearchParams({q:address(value),z:String(zoom(level)),output:'embed',hl:'es'});
 const directions=value=>'https://www.google.com/maps/dir/?'+new URLSearchParams({api:'1',destination:address(value)});
 function create({title='Ven a conocernos',direction='',level=16,layout='tarjeta',schedule='',phone=''}={}){
  const card=document.createElement('article');card.className='nd-map-card';card.dataset.mapLayout=layout==='solo'?'solo':'tarjeta';card.dataset.ndMap='true';
  const text=(tag,cls,value)=>{const n=document.createElement(tag);n.className=cls;n.textContent=value;return n;},info=text('div','nd-map-info',''),place=address(direction);
  info.append(text('span','nd-map-eyebrow','CERCA DE TI'),text('h3','nd-map-title',title||'Ven a conocernos'),text('p','nd-map-address',place||'Agrega la dirección de tu plantel en Propiedades.'));
  if(schedule)info.append(text('p','nd-map-detail',schedule));if(phone)info.append(text('p','nd-map-detail',phone));
  const surface=text('div','nd-map-surface','');if(place){const link=text('a','nd-map-directions','Cómo llegar ↗');link.href=directions(place);link.target='_blank';link.rel='noopener noreferrer';info.append(link);const iframe=document.createElement('iframe');iframe.className='nd-map-frame';iframe.src=embed(place,level);iframe.title='Mapa de '+place;iframe.loading='lazy';iframe.referrerPolicy='strict-origin-when-cross-origin';iframe.allowFullscreen=true;surface.append(iframe);}else surface.append(text('div','nd-map-empty','⌖'),text('p','nd-map-placeholder','Tu ubicación aparecerá aquí'));
  card.append(info,surface);return card;
 }
 const api={address,zoom,embed,directions,create};if(typeof module!=='undefined'&&module.exports)module.exports=api;root.NexoMapa=api;
})(typeof window!=='undefined'?window:globalThis);
