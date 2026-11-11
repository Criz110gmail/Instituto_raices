"""Genera el mismo contenido escolar para SSR y lienzo desde inicio.html.
Ejecutar al cambiar la composición original. Nunca incorpora contenido del usuario.
"""
from html.parser import HTMLParser
from html import escape
from pathlib import Path
import json,re
ROOT=Path(__file__).resolve().parents[1]
class N:
 def __init__(self,tag,attrs=()):self.tag=tag;self.attrs=dict(attrs);self.children=[]
class P(HTMLParser):
 def __init__(self):super().__init__(convert_charrefs=True);self.root=N('root');self.stack=[self.root]
 def handle_starttag(self,t,a):
  n=N(t,a);self.stack[-1].children.append(n)
  if t not in {'img','br','meta','link','input','hr'}:self.stack.append(n)
 def handle_endtag(self,t):
  for i in range(len(self.stack)-1,0,-1):
   if self.stack[i].tag==t:self.stack=self.stack[:i];break
 def handle_data(self,d):self.stack[-1].children.append(d)
p=P();p.feed((ROOT/'src/main/resources/templates/inicio.html').read_text())
def walk(n):
 yield n
 for c in n.children:
  if isinstance(c,N):yield from walk(c)
nodes=list(walk(p.root));main=next(n for n in nodes if n.tag=='main')
parts=[('encabezado',next(n for n in nodes if n.tag=='header'))]+[(k,n) for k,n in zip(['portada','banda','nosotros','oferta','galeria','ubicaciones','accesos'],[c for c in main.children if isinstance(c,N)])]+[('pie',next(n for n in nodes if n.tag=='footer'))]
allmeta={};fragments=[]
def serialize(n,meta,server=False):
 if isinstance(n,str):return escape(n)
 attrs={k:v for k,v in n.attrs.items() if not k.startswith('th:') and k not in {'data-essential-image','tabindex'}}
 if 'th:src' in n.attrs:attrs['src']=re.sub(r'^@\{|\}$','',n.attrs['th:src'])
 if 'th:href' in n.attrs:attrs['href']=re.sub(r'^@\{|\}$','',n.attrs['th:href'])
 if n.tag=='img':
  key='imagen'+str(len(meta['imagenes']));meta['imagenes'][key]={'src':attrs['src'],'alt':attrs.get('alt','')};attrs['data-school-image']=key
  if server:attrs['th:classappend']="${b.imagenes.get('"+key+"')!=null ? 'school-upload' : ''}"
  if server:attrs['th:src']="${b.imagenes.get('"+key+"')!=null ? imagenBase+b.imagenes.get('"+key+"') : b.seccion=='encabezado' and sitio.logoId!=null ? imagenBase+sitio.logoId : '"+attrs['src']+"'}";attrs['th:alt']="${b.campos.get('alt-"+key+"')!=null ? b.campos.get('alt-"+key+"') : '"+attrs.get('alt','').replace("'","''")+"'}"
 if n.tag=='a':
  key='enlace'+str(len(meta['enlaces']));meta['enlaces'][key]=attrs.get('href','#inicio');attrs['data-school-link']=key
  if server:attrs['th:href']="${b.enlaces.get('"+key+"')!=null ? b.enlaces.get('"+key+"') : '"+attrs['href']+"'}"
 if server:
  key=attrs.get('data-school-image') or attrs.get('data-school-link')
  if key:attrs['th:if']="${b.objetos.get('"+key+"')==null or b.objetos.get('"+key+"').visible}"
  if key:attrs['th:style']="${b.objetos.get('"+key+"')!=null ? b.objetos.get('"+key+"').estilo : null}"
 content=''
 for c in n.children:
  if isinstance(c,str) and c.strip():
   key='texto'+str(len(meta['textos']));meta['textos'][key]=c;val=''
   if server:val=' th:style="'+escape("${b.objetos.get('"+key+"')!=null ? b.objetos.get('"+key+"').estilo : null}",quote=True)+'" th:text="'+escape("${b.campos.get('"+key+"')!=null ? b.campos.get('"+key+"') : '"+c.replace("'","''")+"'}",quote=True)+'"'
   content+='<school-text data-school-text="'+key+'"'+val+'>'+escape(c)+'</school-text>'
  else:content+=serialize(c,meta,server) if isinstance(c,N) else c
 a=''.join(' '+k+('="'+escape(v or '',quote=True)+'"') for k,v in attrs.items())
 return '<'+n.tag+a+'>'+content+('' if n.tag in {'img','br','input','hr'} else '</'+n.tag+'>')
for key,n in parts:
 m={'textos':{},'imagenes':{},'enlaces':{}};html=serialize(n,m);allmeta[key]={'html':html,**m}
 m2={'textos':{},'imagenes':{},'enlaces':{}};ssr=serialize(n,m2,True)
 extra='''<div class="school-free-objects"><school-text class="school-free-text" th:each="entry:${b.campos}" th:if="${#strings.startsWith(entry.key,'libre')}" th:text="${entry.value}" th:style="${b.objetos.get(entry.key)!=null ? b.objetos.get(entry.key).estilo : null}"></school-text><img class="school-free-image" th:each="entry:${b.imagenes}" th:if="${entry.value!=null and (#strings.startsWith(entry.key,'imagen8') or #strings.startsWith(entry.key,'imagen9')) and (b.objetos.get(entry.key)==null or b.objetos.get(entry.key).visible)}" th:src="${imagenBase+entry.value}" th:alt="${b.campos.get('alt-'+entry.key)}" th:style="${b.objetos.get(entry.key)!=null ? b.objetos.get(entry.key).estilo : null}"></div>'''
 if key=='galeria':
  ssr=ssr.replace('class="gallery-empty"','class="gallery-empty" th:if="${albumesPublicos.empty}"')
  extra+='''<div class="section-shell school-albums" th:if="${!albumesPublicos.empty}"><article th:each="album:${albumesPublicos}"><h3 th:text="${album.titulo}"></h3><p th:text="${album.descripcion}"></p><div class="school-photo-grid"><figure th:each="foto:${album.fotosPublicas}"><a th:href="${imagenBase+foto.imagenId}" target="_blank" rel="noopener"><img th:src="${imagenBase+foto.imagenId}" th:alt="${foto.descripcion}" loading="lazy"></a><figcaption th:text="${foto.descripcion}"></figcaption></figure></div></article></div>'''
 if key=='ubicaciones':
  extra+='''<div class="section-shell school-locations" th:if="${!ubicacionesPublicas.empty}"><article th:each="u:${ubicacionesPublicas}"><h3 th:text="${u.nombre}"></h3><p th:text="${u.direccion}"></p><p th:text="${u.horario}"></p><p th:text="${u.telefono}"></p><a th:href="${u.mapaUrl}" target="_blank" rel="noopener">Ver mapa ↗</a></article></div>'''
 ssr+=extra
 fragments.append('<th:block th:fragment="'+key+'">'+ssr+'</th:block>')
(ROOT/'src/main/resources/templates/fragments/sitio-escolar-editable.html').write_text('<!DOCTYPE html><html xmlns:th="http://www.thymeleaf.org"><body>'+''.join(fragments)+'</body></html>')
(ROOT/'src/main/resources/static/js/sitio-visual-escolar.js').write_text("(function(root){'use strict';const secciones="+json.dumps(allmeta,ensure_ascii=False)+";const api={secciones};if(typeof module!=='undefined'&&module.exports)module.exports=api;root.NexoEscolar=api;})(typeof window!=='undefined'?window:globalThis);\n")
