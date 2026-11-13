"""Importa la composición aprobada a componentes tipados del diseñador.
La entrada es código del repositorio; nunca HTML aportado por usuarios.
"""
from pathlib import Path
import runpy,json,re
ROOT=Path(__file__).resolve().parents[1]
source=runpy.run_path(str(ROOT/'scripts/generar_sitio_escolar.py'))
N=source['N']; parts=source['parts']; sections=[]
labels={'encabezado':'Encabezado','portada':'Portada','banda':'Franja del eslogan','nosotros':'Quiénes somos','oferta':'Un mundo por explorar','galeria':'Galería','ubicaciones':'Ubicaciones','accesos':'Accesos a portales','pie':'Pie de página'}
human={'hero':'Portada','hero-copy':'Textos de portada','hero-art':'Composición fotográfica','hero-actions':'Botones de portada','hero-signature':'Firma de portada','hero-sun':'Sol','hero-spark':'Destello','photo-blob':'Marco de fotografía','photo-sticker':'Pegatina','art-caption':'Pie de fotografía','crayon':'Crayola','about':'Quiénes somos','about-copy':'Presentación','about-doodle':'Decoración alfabética','eyebrow':'Antetítulo','hero-label':'Etiqueta de portada','hero-intro':'Introducción','motto-band':'Franja del eslogan','section-shell':'Área de contenido','offer-section':'Oferta','section-heading':'Encabezado de sección','feature-grid':'Tarjetas de oferta','feature-card':'Tarjeta de oferta','card-tab':'Etiqueta de tarjeta','feature-icon':'Ícono de tarjeta','upcoming':'Estado de tarjeta','gallery-section':'Galería','gallery-empty':'Aviso de galería','camera-doodle':'Cámara','visit-section':'Ubicaciones','visit-inner':'Contenido de visita','visit-decoration':'Saludo','portal-section':'Accesos a portales','portal-grid':'Accesos de usuarios','site-header':'Encabezado','site-footer':'Pie de página','footer-inner':'Contenido del pie','footer-name':'Nombre de escuela','footer-bottom':'Información inferior','wordmark':'Identidad de escuela','header-logo':'Logotipo','principles':'Texto destacado verde','culture':'Texto destacado café','editorial-note':'Nota de presentación'}
for key,root in parts:
 counter=[0]; slots={'texto':0,'imagen':0,'enlace':0}
 def node(n):
  counter[0]+=1; identifier=key+'-c'+str(counter[0]);clases='' if isinstance(n,str) else n.attrs.get('class','')
  if isinstance(n,str):
   slot='texto'+str(slots['texto']);slots['texto']+=1
   return dict(id=identifier,tipo='texto',tag='school-text',nombre=n.strip()[:120] or 'Texto',texto=n,slot=slot,hijos=[])
  attrs=n.attrs;tag=n.tag;tipo='contenedor'
  if tag in ['h1','h2','h3']:tipo='titulo'
  elif tag=='img':tipo='logo' if 'header-logo' in clases else 'imagen'
  elif tag=='nav':tipo='menu'
  elif tag=='a':tipo='boton' if 'button' in clases or 'portal-grid' in clases else 'enlace'
  elif tag=='article':tipo='tarjeta'
  elif tag in ['br','hr']:tipo='salto' if tag=='br' else 'linea'
  elif tag=='span' and not n.children:tipo='forma'
  d=dict(id=identifier,tipo=tipo,tag=tag,nombre={'h1':'Título principal','h2':'Título de sección','nav':'Menú','header':'Encabezado','footer':'Pie de página','img':'Logotipo' if tipo=='logo' else 'Imagen','a':'Enlace','article':'Tarjeta'}.get(tag,clases.split(' ')[0] or tag),clases=clases,ancla=attrs.get('id',''),hijos=[])
  if tag=='img':
   slot='imagen'+str(slots['imagen']);slots['imagen']+=1;d['slot']=slot
   src=attrs.get('th:src',attrs.get('src',''));d['recurso']='logo' if any(nombre in src for nombre in ('logo.png','logo1.png')) else 'foto';d['descripcion']=attrs.get('alt','') or ('Logotipo de la escuela' if tipo=='logo' else 'Comunidad escolar')
  if tag=='a':
   slot='enlace'+str(slots['enlace']);slots['enlace']+=1;d['slot']=slot
   d['enlace']=re.sub(r'^@\{|\}$','',attrs.get('th:href',attrs.get('href','#inicio')))
  d['hijos']=[node(c) for c in n.children if not isinstance(c,str) or c.strip()]
  words=clases.split();nice=next((human[c] for c in words if c in human),None)
  if nice:d['nombre']=nice
  elif tag in ['div','section','span','p','strong','em','button','br','hr']:d['nombre']={'div':'Grupo','section':'Sección','span':'Texto agrupado','p':'Párrafo','strong':'Texto destacado','em':'Texto con acento','button':'Botón de menú','br':'Salto de línea','hr':'Separador'}[tag]
  if 'crayon-green' in words:d['nombre']='Crayola verde'
  if 'crayon-yellow' in words:d['nombre']='Crayola amarilla'
  if tag=='article':
   title=next((x for x in n.children if isinstance(x,N) and x.tag=='h3'),None)
   if title:d['nombre']='Tarjeta · '+''.join(x for x in title.children if isinstance(x,str)).strip()
  return d
 sections.append(dict(id='seccion-'+key,tipo='seccion',titulo=labels[key],papel='encabezado' if key=='encabezado' else 'pie' if key=='pie' else 'contenido',distribucion='original',altura=0,espacio=0,componentes=[node(root)]))
(ROOT/'src/main/resources/static/js/sitio-disenador-original.js').write_text('(function(root){const pagina='+json.dumps(sections,ensure_ascii=False)+";if(typeof module!=='undefined'&&module.exports)module.exports=pagina;root.NexoPaginaOriginal=pagina;})(typeof window!=='undefined'?window:globalThis);\n")
def cantidad(n):return 1+sum(cantidad(c) for c in n.get('hijos',[]))
print('Secciones:',len(sections),'componentes:',sum(cantidad(n) for s in sections for n in s['componentes']))
