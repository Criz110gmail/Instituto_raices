(function(root){'use strict';
 const valid=c=>/^#[0-9a-f]{6}$/i.test(c||'');
 const lum=c=>[.2126,.7152,.0722].reduce((sum,w,i)=>{const v=parseInt(c.slice(1+i*2,3+i*2),16)/255;return sum+w*(v<=.04045?v/12.92:Math.pow((v+.055)/1.055,2.4));},0);
 const ink=c=>lum(c)>.179?'#000000':'#ffffff';
 const contrast=(a,b)=>(Math.max(lum(a),lum(b))+.05)/(Math.min(lum(a),lum(b))+.05);
 const mix=(a,b,t)=>'#'+[0,1,2].map(i=>Math.round(parseInt(a.slice(1+i*2,3+i*2),16)*(1-t)+parseInt(b.slice(1+i*2,3+i*2),16)*t).toString(16).padStart(2,'0')).join('');
 function readable(color,against,min=4.5){if(contrast(color,against)>=min)return color;const target=ink(against);for(let n=1;n<=100;n++){const candidate=mix(color,target,n/100);if(contrast(candidate,against)>=min)return candidate;}return target;}
 function palette(data,theme='light'){
  if(![data.principal,data.fondo,data.acento].every(valid))return null;
  const dark=theme==='dark',custom=dark&&[data.oscuroPrincipal,data.oscuroFondo,data.oscuroAcento].every(valid);
  const primary=custom?data.oscuroPrincipal:dark?mix(data.principal,'#07111f',.28):data.principal;
  const bg=custom?data.oscuroFondo:dark?mix(data.principal,'#090d14',.92):data.fondo;
  const accent=custom?data.oscuroAcento:data.acento;
  const surface=dark?mix(bg,ink(bg),.06):mix(bg,'#ffffff',.94),text=ink(surface),soft=mix(surface,text,.05);
  // Los alias antiguos de botones todavía usan texto blanco: nunca reciben el color de enlaces.
  const action=readable(accent,'#ffffff');
  const values={primary,'on-primary':ink(primary),accent,'on-accent':ink(accent),bg,surface,text,'page-text':ink(bg),soft,
   muted:readable(mix(text,surface,.36),surface),'soft-text':readable(text,soft),border:readable(mix(surface,text,.22),surface,3),
   link:readable(accent,surface),action,'on-action':'#ffffff','action-border':readable(accent,surface,3),
   hover:readable(mix(action,'#000000',.12),'#ffffff'),focus:readable(accent,surface,3),
   disabled:mix(surface,text,.10),'disabled-text':readable(mix(text,surface,.38),mix(surface,text,.10))};
  return Object.fromEntries(Object.entries(values).map(([k,v])=>['--pi-ui-'+k,v]));
 }
 function variables(p,f,a){if(![p,f,a].every(valid))return null;return {'--pi-principal':p,'--pi-fondo':f,'--pi-acento':a,'--pi-story-text':ink(p),'--pi-on-accent':ink(a),'--pi-link':lum(a)>.179?'#173d32':a,'--pi-link-dark':lum(a)>.179?a:'#ffffff'};}
 function apply(target,data,theme){const vars=palette(data,theme);if(!vars)return false;for(const[k,v]of Object.entries(vars))target.style.setProperty(k,v);return true;}
 const adminDefaults={principal:'#214936',fondo:'#f5f7f2',acento:'#72a629',oscuroPrincipal:null,oscuroFondo:null,oscuroAcento:null};
 const api={valid,lum,ink,contrast,mix,readable,palette,variables,apply,adminDefaults};if(typeof module!=='undefined'&&module.exports)module.exports=api;root.NexoPortalColores=api;
 if(typeof document==='undefined')return;
 const start=()=>{
  const body=document.body;if(!body||body.dataset.piPersonalizada===undefined)return;
  const css=getComputedStyle(body),read=name=>css.getPropertyValue(name).trim();
  let data={principal:read('--pi-principal'),fondo:read('--pi-fondo'),acento:read('--pi-acento'),oscuroPrincipal:read('--pi-dark-principal'),oscuroFondo:read('--pi-dark-fondo'),oscuroAcento:read('--pi-dark-acento')};
  const refresh=()=>{const effective=data.original===true&&body.dataset.piPortal==='admin'?adminDefaults:data;apply(body,effective,document.documentElement.dataset.theme);if(document.documentElement.dataset.piDemo==='true'){document.documentElement.dataset.piInterior=String(body.dataset.piPersonalizada==='true');apply(document.documentElement,effective,document.documentElement.dataset.theme);}};
  refresh();new MutationObserver(refresh).observe(document.documentElement,{attributes:true,attributeFilter:['data-theme']});
  if(body.dataset.piPreview!=='true')return;
  document.querySelectorAll('form').forEach(f=>{f.inert=true;f.setAttribute('autocomplete','off');f.addEventListener('submit',e=>e.preventDefault());});
  document.querySelectorAll('[autofocus]').forEach(n=>n.removeAttribute('autofocus'));
  addEventListener('message',e=>{if(e.origin!==location.origin||e.source!==parent||e.data?.canal!=='nexo-portales')return;const colors=variables(e.data.principal,e.data.fondo,e.data.acento);if(!colors)return;data=e.data;body.dataset.piPersonalizada=String(e.data.original!==true||body.dataset.piPortal==='admin');for(const[k,v]of Object.entries(colors))body.style.setProperty(k,v);if(['light','dark'].includes(e.data.tema))document.documentElement.dataset.theme=e.data.tema;refresh();});
 };
 if(document.readyState==='loading')document.addEventListener('DOMContentLoaded',start,{once:true});else start();
})(typeof window!=='undefined'?window:globalThis);
