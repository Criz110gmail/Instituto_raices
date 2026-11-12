(function(root){
 'use strict';
 const originalSource=typeof module!=='undefined'&&module.exports?require('./sitio-disenador-original.js'):root.NexoPaginaOriginal;
 const clone=o=>JSON.parse(JSON.stringify(o));
 const uid=()=>root.crypto?.randomUUID?.()||'c-'+Date.now().toString(36)+'-'+Math.random().toString(36).slice(2,10);
 const TIPOS={texto:['Texto','T'],titulo:['Título','H'],imagen:['Imagen','▧'],logo:['Logotipo','◈'],menu:['Menú','☰'],boton:['Botón','↗'],enlace:['Enlace','↗'],contenedor:['Contenedor','▥'],tarjeta:['Tarjeta','▣'],galeria:['Galería','▦'],ubicaciones:['Ubicaciones','⌖'],mapa:['Mapa','⌖'],whatsapp:['WhatsApp','◉'],icono:['Objeto escolar','✎'],forma:['Forma','◼'],linea:['Línea','─']};
 const contenedor=n=>n&&['contenedor','menu','tarjeta'].includes(n.tipo)&&['div','section','header','footer','nav','article','figure','li'].includes(n.tag);
 const contar=n=>1+(n.hijos||n.componentes||[]).reduce((sum,c)=>sum+contar(c),0);
 const profundidad=n=>Math.max(0,...(n.hijos||n.componentes||[]).map(c=>1+profundidad(c)));
 const textoDe=n=>n.texto||(n.hijos||[]).map(textoDe).join('');const nombre=n=>n.titulo||(n.tipo==='titulo'&&/^Título/.test(n.nombre||'')?'Título · '+textoDe(n).trim().slice(0,50):n.nombre)||n.texto?.trim().slice(0,50)||TIPOS[n.tipo]?.[0]||'Componente';
 function nodo(tipo,values={}){return Object.assign({id:uid(),tipo,tag:'div',nombre:TIPOS[tipo]?.[0]||'Componente',texto:'',clases:'',ancla:'',enlace:'',recurso:'ninguno',imagenId:null,descripcion:'',icono:'cuaderno',decorativo:true,whatsappNumero:'',whatsappMensaje:'',whatsappDiseno:'etiqueta',direccionMapa:'',zoomMapa:16,disenoMapa:'tarjeta',albumId:null,presentacion:'cuadricula',efectoFoto:'ninguno',transicion:'deslizar',columnas:3,mostrarDescripciones:true,automatico:false,intervalo:5,posicion:'flujo',visible:true,estilos:{},hijos:[]},values);}
 function nuevo(tipo,{libre=true,x=32,y=32}={}){
  const n=nodo(tipo),s={};if(libre){n.posicion='libre';s.x=x;s.y=y;}n.estilos.escritorio=s;
  if(tipo==='texto'||tipo==='titulo'){n.tag=tipo==='titulo'?'h2':'p';n.texto=tipo==='titulo'?'Nuevo título':'Escribe aquí tu texto';s.ancho=tipo==='titulo'?360:280;s.fuente=tipo==='titulo'?36:18;s.color='#304731';}
  if(tipo==='imagen'||tipo==='logo'){n.tag='img';n.recurso=tipo==='logo'?'logo':'ninguno';n.descripcion=tipo==='logo'?'Logotipo de la escuela':'Imagen de la escuela';s.ancho=tipo==='logo'?96:280;s.alto=tipo==='logo'?96:180;s.encuadre='contain';}
  if(tipo==='boton'||tipo==='enlace'){n.tag='a';n.texto=tipo==='boton'?'Conoce más':'Nuevo enlace';n.enlace='#inicio';s.fuente=16;if(tipo==='boton'){s.fondo='#2b5031';s.color='#ffffff';s.radio=14;s.relleno=14;}}
  if(tipo==='whatsapp'){n.tag='a';n.texto='Escríbenos por WhatsApp';s.ancho=260;s.alto=0;s.fondo='#187d43';s.color='#ffffff';s.radio=18;s.fuente=15;}
  if(tipo==='mapa'){n.texto='Ven a conocernos';s.ancho=960;s.alto=0;}
  if(tipo==='icono'){n.nombre='Cuaderno';s.ancho=112;s.alto=112;s.color='#25aaa1';s.fondo='transparent';}
  if(tipo==='forma'){n.clases='nd-shape';s.ancho=140;s.alto=100;s.fondo='#4ed6bf';s.radio=16;}
  if(tipo==='linea'){n.tag='hr';s.ancho=240;s.borde=1;s.colorBorde='#4c7065';}
  if(tipo==='contenedor'||tipo==='tarjeta'){n.tag=tipo==='tarjeta'?'article':'div';n.clases=tipo==='tarjeta'?'nd-card':'';s.ancho=340;s.alto=210;s.distribucion='vertical';s.relleno=24;s.espacio=12;s.fondo=tipo==='tarjeta'?'#f0f8e5':'transparent';s.radio=20;n.hijos=[nuevo('titulo',{libre:false}),nuevo('texto',{libre:false})];n.hijos[0].estilos.escritorio={fuente:24};n.hijos[1].estilos.escritorio={fuente:16};}
  if(tipo==='menu'){n.tag='nav';n.clases='nd-menu';s.distribucion='horizontal';s.espacio=20;s.relleno=12;n.hijos=[['Inicio','#inicio'],['Nuestra escuela','#nosotros'],['Contacto','#contacto']].map(([text,url])=>nodo('enlace',{tag:'a',nombre:text,texto:text,enlace:url,estilos:{escritorio:{fuente:16}}}));}
  if(tipo==='galeria'||tipo==='ubicaciones'){s.ancho=720;if(tipo==='ubicaciones')s.alto=300;}
  return n;
 }
 function seccion(nombre='Nueva sección',papel='contenido'){return {id:uid(),tipo:'seccion',titulo:nombre,papel,distribucion:'libre',altura:360,componentes:[],fondo:'#fffef9',color:'#304731',espacio:0,visible:true,enMenu:false,objetos:{},campos:{},imagenes:{},enlaces:{}};}
 function limpiar(source,old=null,sitio={}){
  const walk=raw=>{const n=nodo(raw.tipo,clone(raw)),slot=n.slot;n.id=uid();n.posicion=raw.posicion||'original';delete n.slot;
   n.hijos=(raw.hijos||[]).map(walk);if(slot&&old){if(slot.startsWith('texto')&&Object.hasOwn(old.campos||{},slot))n.texto=old.campos[slot];if(slot.startsWith('enlace')&&Object.hasOwn(old.enlaces||{},slot))n.enlace=old.enlaces[slot];if(slot.startsWith('imagen')){n.imagenId=old.imagenes?.[slot]||null;if(old.campos?.['alt-'+slot])n.descripcion=old.campos['alt-'+slot];}const o=old.objetos?.[slot];if(o){n.posicion='flujo';n.visible=o.visible!==false;n.estilos.escritorio={x:o.x||0,y:o.y||0,giro:o.giro||0,...(o.ancho?{ancho:o.ancho}:{}),...(o.fuente?{fuente:o.fuente}:{}),...(o.color?{color:o.color}:{})};}}
   if(n.tipo==='logo'&&!n.imagenId&&sitio.logoId)n.imagenId=sitio.logoId;
   return n;
  };return walk(source);
 }
 function original(sitio={}){const d={formato:1,tema:'escolar',fondo:'#fffef9',tinta:'#304731',fuente:'amigable',radio:20,menuVisible:true,bloques:[]};for(const raw of originalSource){const b=seccion(raw.titulo,raw.papel);Object.assign(b,clone(raw),{componentes:raw.componentes.map(n=>limpiar(n,null,sitio))});d.bloques.push(b);}return recursos(d,sitio);}
 function recursos(d,sitio){const extras=sitio.extras||{};for(const b of d.bloques){const visit=list=>{for(let i=0;i<list.length;i++){const n=list[i];if((n.clases||'').split(' ').includes('gallery-empty')&&(extras.albumes||[]).some(a=>a.activo&&(a.fotos||[]).some(f=>f.activo))){const gallery=nodo('galeria',{nombre:'Galería publicada',estilos:{}});list[i]=gallery;}else visit(n.hijos||[]);}};visit(b.componentes);}return d;}
 function convertir(sitio){
  if(!sitio.diseno)return original(sitio);if(sitio.diseno.bloques?.length&&sitio.diseno.bloques.every(b=>b.tipo==='seccion'))return clone(sitio.diseno);
  const source=sitio.diseno,d={...clone(source),bloques:[]};
  for(const old of source.bloques||[]){if(old.tipo==='seccion'){d.bloques.push(clone(old));continue;}
   const b=seccion(old.titulo||old.seccion||'Sección');b.id=old.id;b.visible=old.visible!==false;b.fondo=old.fondo||'#fffef9';b.color=old.color||'#304731';b.distribucion='vertical';b.altura=0;
   if(old.tipo==='escolar'){const raw=originalSource.find(x=>x.id==='seccion-'+old.seccion);if(raw){Object.assign(b,{titulo:old.titulo||raw.titulo,papel:raw.papel,distribucion:'original',componentes:raw.componentes.map(n=>limpiar(n,old,sitio))});for(const [key,text]of Object.entries(old.campos||{})){if(!/^libre/.test(key))continue;const n=nuevo('texto');n.texto=text;n.estilos.escritorio={...n.estilos.escritorio,...Object.fromEntries(Object.entries(old.objetos?.[key]||{}).filter(([k])=>['x','y','ancho','fuente','giro','color'].includes(k)))};b.componentes.push(n);}for(const [key,image]of Object.entries(old.imagenes||{})){if(!/^imagen[89]/.test(key)||!image)continue;const n=nuevo('imagen');n.imagenId=image;n.estilos.escritorio={...n.estilos.escritorio,...Object.fromEntries(Object.entries(old.objetos?.[key]||{}).filter(([k])=>['x','y','ancho','fuente','giro','color'].includes(k)))};b.componentes.push(n);}}}
   else{b.espacio=old.espacio??32;if(old.titulo){const n=nuevo(old.tipo==='portada'?'titulo':'titulo',{libre:false});n.texto=old.titulo;b.componentes.push(n);}if(old.texto){const n=nuevo('texto',{libre:false});n.texto=old.texto;b.componentes.push(n);}if(old.imagenId){const n=nuevo('imagen',{libre:false});n.imagenId=old.imagenId;n.descripcion=old.texto||'Imagen de la escuela';b.componentes.push(n);}if(old.boton){const n=nuevo('boton',{libre:false});n.texto=old.boton;n.enlace=old.enlace||'#inicio';b.componentes.push(n);}if(['galeria','ubicaciones'].includes(old.tipo)){const n=nuevo(old.tipo,{libre:false});n.albumId=old.albumId||null;b.componentes.push(n);}if(old.tipo==='separador')b.componentes.push(nuevo('linea',{libre:false}));if(old.tipo==='espacio')b.altura=old.espacio||40;}
   d.bloques.push(b);
  }return recursos(d,sitio);
 }
 function indice(d){const found=new Map();for(const b of d.bloques){found.set(b.id,{node:b,section:b,parent:null,list:d.bloques,ancestros:[]});const visit=(list,parent,ancestros)=>{for(const n of list||[]){found.set(n.id,{node:n,section:b,parent,list,ancestros});visit(n.hijos,n,[...ancestros,n.id]);}};visit(b.componentes,b,[b.id]);}return found;}
 function estilo(n,device='escritorio'){return {...n.estilos?.escritorio,...(device==='escritorio'?{}:n.estilos?.[device])};}
 function mover(d,id,parentId,index=null){const map=indice(d),entry=map.get(id),dest=map.get(parentId);if(!entry||!dest||entry.node.tipo==='seccion'||id===parentId||dest.ancestros.includes(id))return false;if(dest.node.tipo!=='seccion'&&!contenedor(dest.node))return false;if(profundidad(entry.node)+dest.ancestros.length>12)return false;entry.list.splice(entry.list.indexOf(entry.node),1);const list=dest.node.tipo==='seccion'?dest.node.componentes:dest.node.hijos;list.splice(index===null?list.length:Math.max(0,Math.min(list.length,index)),0,entry.node);return true;}
 function duplicar(d,id){const item=indice(d).get(id);if(!item)return null;const copy=clone(item.node);const fresh=n=>{n.id=uid();if(n.ancla)n.ancla='';for(const child of n.hijos||n.componentes||[])fresh(child);};fresh(copy);copy.nombre=copy.nombre?copy.nombre+' · copia':copy.nombre;if(copy.tipo==='seccion')copy.titulo+=' · copia';else{copy.estilos??={};copy.estilos.escritorio={...copy.estilos.escritorio,x:(copy.estilos.escritorio?.x||0)+16,y:(copy.estilos.escritorio?.y||0)+16};}item.list.splice(item.list.indexOf(item.node)+1,0,copy);return copy;}
 function quitar(d,ids){const set=new Set(ids);d.bloques=d.bloques.filter(b=>!set.has(b.id));const visit=list=>{for(const n of list){n.hijos=(n.hijos||[]).filter(c=>!set.has(c.id));visit(n.hijos);}};for(const b of d.bloques){b.componentes=b.componentes.filter(c=>!set.has(c.id));visit(b.componentes);}}
 function copiar(d,ids){const map=indice(d),set=new Set(ids);return ids.map(id=>map.get(id)).filter(e=>e&&!e.ancestros.some(id=>set.has(id))).map(e=>({node:clone(e.node),parentId:e.parent?.id||null}));}
 function pegar(d,items,{parentId=null,offset=16}={}){
  if(!items?.length)return {ids:[],error:'Primero copia un objeto del diseñador.'};
  const map=indice(d),planned=[],newSections=items.filter(i=>i.node.tipo==='seccion').length;
  if(d.bloques.length+newSections>60||map.size-d.bloques.length+items.reduce((sum,i)=>sum+contar(i.node)-(i.node.tipo==='seccion'?1:0),0)>800)return {ids:[],error:'La copia supera el límite de secciones o componentes de la página.'};
  for(const item of items){const section=item.node.tipo==='seccion';const dest=section?null:map.get(parentId||item.parentId)||map.get(d.bloques.find(b=>b.papel==='contenido')?.id||d.bloques[0]?.id);
   if(!section&&(!dest||(dest.node.tipo!=='seccion'&&!contenedor(dest.node)&&dest.node.id!==item.parentId)))return {ids:[],error:'Selecciona una sección o un contenedor donde pegar el objeto.'};
   const list=section?d.bloques:dest.node.tipo==='seccion'?dest.node.componentes:dest.node.hijos;
   if(!section&&(profundidad(item.node)+dest.ancestros.length+1>12||list.length+planned.filter(p=>p.list===list).length>=100))return {ids:[],error:'La copia supera el límite de niveles o de objetos de este contenedor.'};
   const copy=clone(item.node);const fresh=n=>{n.id=uid();if(n.ancla)n.ancla='';for(const child of n.hijos||n.componentes||[])fresh(child);};fresh(copy);
   if(section)copy.titulo=(copy.titulo||'Sección').slice(0,190)+' · copia';else{copy.nombre=(copy.nombre||TIPOS[copy.tipo]?.[0]||'Objeto').slice(0,150)+' · copia';copy.estilos??={};copy.estilos.escritorio={...copy.estilos.escritorio,x:Math.min(3000,(copy.estilos.escritorio?.x||0)+offset),y:Math.min(6000,(copy.estilos.escritorio?.y||0)+offset)};}
   planned.push({node:copy,list});
  }
  for(const p of planned)p.list.push(p.node);return {ids:planned.map(p=>p.node.id),error:null};
 }
 function giroPorArrastre(base,start,current,snap=false){const delta=Math.atan2(Math.sin(current-start),Math.cos(current-start))*180/Math.PI;let angle=base+delta;if(snap)angle=Math.round(angle/15)*15;return Math.round(((angle+180)%360+360)%360-180);}
 // Geometría en píxeles de página, independiente del zoom del editor.
 function ajustarMovimiento({box,dx=0,dy=0,origin={x:0,y:0},targets=[],grid=16,enabled=true,free=false,axis=null,tolerance=6}={}){
  const raw={x:axis==='y'?0:dx,y:axis==='x'?0:dy},result={dx:raw.x,dy:raw.y,guides:[]};if(free)return result;
  const step=[4,8,16,24,32].includes(Number(grid))?Number(grid):16;
  for(const direction of ['x','y']){
   if(axis&&(axis==='x'?'y':'x')===direction)continue;
   const dimension=direction==='x'?'width':'height',start=box[direction]+raw[direction],size=box[dimension],offset=origin[direction]||0;let nearest=null;
   for(const target of targets)for(const anchor of [0,.5,1])for(const edge of [0,.5,1]){const at=target[direction]+target[dimension]*edge,delta=at-(start+size*anchor);if(Math.abs(delta)<=tolerance&&(!nearest||Math.abs(delta)<Math.abs(nearest.delta))){nearest={axis:direction,at,delta,anchor,target:target.label||'Objeto'};}}
   let delta=raw[direction];if(nearest){if(enabled)delta+=nearest.delta;result.guides.push(nearest);}else if(enabled)delta=Math.round((start-offset)/step)*step+offset-box[direction];
   result[direction==='x'?'dx':'dy']=delta;
  }return result;
 }
 function prepararMapaUbicacion(d,places=[]){
  const index=indice(d),existing=d.bloques.find(b=>b.id==='seccion-ubicaciones')||d.bloques.find(b=>(b.titulo||'').toLocaleLowerCase()==='ubicaciones');
  if(existing?.visible===false)return {error:'La sección Ubicaciones está oculta. Actívala antes de colocar el mapa.'};
  const section=existing||seccion('Ubicaciones'),existingMap=existing?[...index.values()].find(e=>e.section.id===existing.id&&e.node.tipo==='mapa'):null;
  const options=places.filter(p=>p.direccion?.trim());if(!existingMap&&options.length!==1)return {error:'Selecciona Mapa en Componentes y elige la dirección del plantel en Propiedades.'};
  const parent=existing?[...index.values()].find(e=>e.section.id===section.id&&contenedor(e.node)&&(e.node.clases||'').split(' ').includes('visit-inner'))?.node:null,list=parent?parent.hijos:section.componentes;
  if(!existingMap&&(index.size-d.bloques.length>=800||(!existing&&d.bloques.length>=60)||list.length>=100||(parent&&index.get(parent.id).ancestros.length>=12)))return {error:'Se alcanzó el límite de secciones, componentes o niveles del diseño.'};
  const n=existingMap?.node||nuevo('mapa',{libre:false});if(!existingMap){n.nombre='Mapa de ubicación';n.direccionMapa=options[0].direccion;n.estilos.escritorio.ancho=0;n.clases='nd-visit-map';}if(parent)parent.clases=[...new Set([...(parent.clases||'').split(' ').filter(Boolean),'nd-visit-with-map'])].join(' ');
  if(!existing){section.altura=0;section.distribucion='vertical';section.espacio=32;d.bloques.push(section);}
  const hasAnchor=[...index.values()].some(e=>e.node.ancla==='ubicaciones'),shell=existing?[...index.values()].find(e=>e.section.id===section.id&&(e.node.clases||'').split(' ').includes('visit-section'))?.node:null;
  if(!hasAnchor){if(shell&&!shell.ancla)shell.ancla='ubicaciones';else n.ancla='ubicaciones';}
  for(const entry of index.values()){const node=entry.node;if(entry.section.id===section.id&&(node.clases||'').split(' ').includes('visit-decoration'))node.visible=false;if(entry.section.id===section.id&&['Próximamente encontrarás las direcciones de nuestros planteles,','sus horarios y el contacto por WhatsApp.'].includes(node.texto))node.visible=false;if(['enlace','boton'].includes(node.tipo)&&/ubicaciones/i.test(textoDe(node)||node.nombre||''))node.enlace='#ubicaciones';}
  if(!existingMap)list.push(n);return {id:n.id,sectionId:section.id,parentId:existingMap?.parent?.id||parent?.id||section.id,created:!existingMap};
 }
 function avisoPublicacion(borrador,publicada,cambios=false){const draft=Math.max(0,Number(borrador)||0),live=Math.max(0,Number(publicada)||0);if(cambios)return 'Hay cambios sin guardar. Guarda y después publica para mostrarlos en tu página.';if(!live)return 'Página aún sin publicar. Pulsa Publicar cuando termines de revisar.';if(draft>live)return 'Cambios guardados sin publicar · Revisión '+draft+'. La página muestra la '+live+'. Pulsa Publicar para actualizarla.';return '';}
 const ESCALAS_ZOOM=[.25,.5,.75,1,1.25,1.5,1.75,2,2.5,3];
 function pasoZoom(actual,direccion){const current=Number(actual)||1;return direccion>0?(ESCALAS_ZOOM.find(n=>n>current+.00001)??Math.max(current,3)):([...ESCALAS_ZOOM].reverse().find(n=>n<current-.00001)??Math.min(current,.25));}
 function vista(mode,windowWidth,available,zoom='auto'){const fallback=Number(windowWidth)>0?Number(windowWidth):1440;const width=Math.max(1,Math.round(mode==='ventana'?fallback:Number(mode)||fallback));const dispositivo=width<=700?'movil':width<=1000?'tableta':'escritorio';const scale=zoom==='auto'?Math.min(1,Math.max(1,Number(available)||1)/width):Number(zoom)||1;return {width,dispositivo,scale};}
 const api={prepararMapaUbicacion,avisoPublicacion,ESCALAS_ZOOM,pasoZoom,ajustarMovimiento,copiar,pegar,giroPorArrastre,vista,contar,profundidad,TIPOS,clone,uid,nodo,nuevo,seccion,original,convertir,indice,estilo,mover,duplicar,quitar,contenedor,nombre};if(typeof module!=='undefined'&&module.exports)module.exports=api;root.NexoDisenador=api;
})(typeof window!=='undefined'?window:globalThis);
