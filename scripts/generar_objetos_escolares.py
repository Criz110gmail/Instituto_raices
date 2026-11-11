"""Ilustraciones SVG originales, locales y cerradas: no acepta SVG del usuario."""
from pathlib import Path
import json
items=[]
def add(key,name,category,svg): items.append(dict(id=key,nombre=name,categoria=category,svg=svg))
add('cuaderno','Cuaderno','Útiles','<rect x="18" y="9" width="36" height="47" rx="5" fill="currentColor" fill-opacity=".12"/><path d="M24 9v47M32 22h14M32 30h10M14 18h8M14 28h8M14 38h8M14 48h8"/>')
add('libro','Libro abierto','Útiles','<path d="M32 17c-9-6-18-7-25-4v36c8-3 16-1 25 5 9-6 17-8 25-5V13c-7-3-16-2-25 4Z" fill="currentColor" fill-opacity=".12"/><path d="M32 17v37M13 22c5-1 9 0 13 2M13 30c5-1 9 0 13 2M38 24c4-2 8-3 13-2M38 32c4-2 8-3 13-2"/>')
add('libros','Pila de libros','Útiles','<path d="M13 12h37v12H13a6 6 0 0 1 0-12ZM51 27H16v12h35a6 6 0 0 0 0-12ZM12 42h39v12H12a6 6 0 0 1 0-12Z" fill="currentColor" fill-opacity=".12"/><path d="M13 18h30M22 33h28M12 48h32"/>')
add('lapiz','Lápiz','Útiles','<path d="m12 44 30-30 10 10-30 30-14 4Z" fill="currentColor" fill-opacity=".12"/><path d="m38 18 10 10M12 44l10 10M17 49l26-26M8 58l5-5M42 14l4-4a4 4 0 0 1 6 0l4 4a4 4 0 0 1 0 6l-4 4"/>')
add('crayola','Crayola','Útiles','<path d="M23 19 32 5l9 14v35a4 4 0 0 1-4 4H27a4 4 0 0 1-4-4Z" fill="currentColor" fill-opacity=".18"/><path d="M23 19h18M23 27h18M23 47h18M28 33h8v8h-8Z"/>')
add('regla','Regla','Útiles','<rect x="7" y="22" width="50" height="20" rx="3" fill="currentColor" fill-opacity=".12"/><path d="M15 23v9M23 23v6M31 23v9M39 23v6M47 23v9"/>')
add('mochila','Mochila','Útiles','<rect x="15" y="16" width="34" height="41" rx="10" fill="currentColor" fill-opacity=".12"/><path d="M25 16v-4a7 7 0 0 1 14 0v4M15 32h34M23 38h18v13H23ZM9 29v18M55 29v18M29 24h6"/>')
add('tijeras','Tijeras','Útiles','<circle cx="16" cy="45" r="8"/><circle cx="42" cy="47" r="8"/><path d="m21 39 27-30M36 41 17 9M29 30l7 12"/><circle cx="29" cy="30" r="2" fill="currentColor"/>')
add('escuela','Escuela','Aprendizaje','<path d="m7 28 25-19 25 19M12 26v30h40V26M25 56V39h14v17" fill="currentColor" fill-opacity=".12"/><path d="M32 9V4h13v8H32M18 30h5M41 30h5"/><circle cx="32" cy="27" r="4"/>')
add('birrete','Birrete','Aprendizaje','<path d="m5 23 27-13 27 13-27 13ZM16 30v14c10 7 22 7 32 0V30" fill="currentColor" fill-opacity=".12"/><path d="M58 24v22M55 47h6"/>')
add('pizarron','Pizarrón','Aprendizaje','<rect x="7" y="10" width="50" height="34" rx="3" fill="currentColor" fill-opacity=".12"/><path d="M4 46h56M22 46l-5 13M42 46l5 13M16 32l7-15 7 15M19 26h8M38 20h10M43 15v10M38 32h10"/>')
add('globo','Globo terráqueo','Aprendizaje','<circle cx="30" cy="27" r="18" fill="currentColor" fill-opacity=".12"/><ellipse cx="30" cy="27" rx="8" ry="18"/><path d="M12 27h36M15 18h30M15 36h30M45 7a26 26 0 0 1-30 42M30 51v7M20 59h20"/>')
add('microscopio','Microscopio','Aprendizaje','<path d="m23 9 9-5 14 23-9 5ZM18 37h24M25 32l-5 5M40 31a13 13 0 0 1 0 25H15M22 57v-9M12 59h42" fill="currentColor" fill-opacity=".12"/><circle cx="43" cy="39" r="4"/>')
add('matraz','Matraz','Aprendizaje','<path d="M25 7h14M27 7v18L13 49a6 6 0 0 0 5 9h28a6 6 0 0 0 5-9L37 25V7M20 37h24" fill="currentColor" fill-opacity=".12"/><circle cx="27" cy="46" r="2"/><circle cx="38" cy="51" r="2"/>')
add('atomo','Átomo','Aprendizaje','<ellipse cx="32" cy="32" rx="27" ry="10"/><ellipse cx="32" cy="32" rx="27" ry="10" transform="rotate(60 32 32)"/><ellipse cx="32" cy="32" rx="27" ry="10" transform="rotate(120 32 32)"/><circle cx="32" cy="32" r="4" fill="currentColor"/>')
add('calculadora','Calculadora','Aprendizaje','<rect x="16" y="6" width="32" height="52" rx="5" fill="currentColor" fill-opacity=".12"/><path d="M22 14h20v10H22ZM23 33h2M33 33h2M41 33h1M23 42h2M33 42h2M41 42h1M23 51h2M33 51h9"/>')
add('paleta','Paleta de pintura','Creatividad','<path d="M33 6C15 6 6 18 6 34c0 14 11 24 23 24 8 0 11-6 6-10-4-4-1-9 5-9h6c16 0 16-33-13-33Z" fill="currentColor" fill-opacity=".12"/><circle cx="20" cy="20" r="3"/><circle cx="35" cy="16" r="3"/><circle cx="47" cy="24" r="3"/><circle cx="16" cy="35" r="3"/>')
add('pincel','Pincel','Creatividad','<path d="m25 35 23-27a5 5 0 0 1 8 6L33 42ZM25 35c-11-1-9 15-19 17 8 8 27 6 27-10" fill="currentColor" fill-opacity=".12"/>')
add('musica','Música','Creatividad','<path d="M23 44V16l29-7v30M23 24l29-7"/><ellipse cx="15" cy="47" rx="8" ry="6" fill="currentColor" fill-opacity=".18"/><ellipse cx="44" cy="42" rx="8" ry="6" fill="currentColor" fill-opacity=".18"/>')
add('teatro','Teatro','Creatividad','<path d="M8 9c12 5 20 5 30 0v17c0 13-8 19-15 23C12 43 8 35 8 26ZM38 22l18 3-3 19c-2 10-8 14-17 15l-8-8" fill="currentColor" fill-opacity=".12"/><path d="M15 23h4M27 23h4M16 33q7 8 14 0M42 35h3M48 39h2M37 48q5-5 10 1"/>')
add('cohete','Cohete','Creatividad','<path d="M24 40C17 22 34 7 53 6c1 18-8 35-25 38ZM21 22l-10 3-4 16 15-5M37 41l-1 15-16 3 3-15M17 47 7 57" fill="currentColor" fill-opacity=".12"/><circle cx="39" cy="21" r="6"/>')
add('avion','Avión de papel','Creatividad','<path d="m5 25 54-18-17 51-13-19ZM29 39 59 7M29 39l-2 14 9-7" fill="currentColor" fill-opacity=".12"/>')
add('balon','Balón','Vida escolar','<circle cx="32" cy="32" r="25" fill="currentColor" fill-opacity=".12"/><path d="m32 21 11 8-4 13H25l-4-13ZM32 21V7M43 29l13-5M39 42l8 10M25 42 17 52M21 29 8 24"/>')
add('trofeo','Trofeo','Vida escolar','<path d="M20 8h24v18c0 17-24 17-24 0ZM20 14H8v8c0 9 8 13 14 13M44 14h12v8c0 9-8 13-14 13M32 39v13M23 52h18v6H23Z" fill="currentColor" fill-opacity=".12"/>')
add('medalla','Medalla','Vida escolar','<path d="m18 6 14 24L46 6M10 6h14l8 15 8-15h14L39 33H25Z"/><circle cx="32" cy="43" r="15" fill="currentColor" fill-opacity=".12"/><path d="m32 34 3 6 7 1-5 5 1 7-6-3-6 3 1-7-5-5 7-1Z"/>')
add('campana','Campana','Vida escolar','<path d="M15 43c5-5 4-12 4-19a13 13 0 0 1 26 0c0 7-1 14 4 19l5 6H10ZM26 54a6 6 0 0 0 12 0M30 9V5h4v4" fill="currentColor" fill-opacity=".12"/>')
add('reloj','Reloj','Vida escolar','<circle cx="32" cy="32" r="25" fill="currentColor" fill-opacity=".12"/><path d="M32 16v17l11 7M32 10v2M54 32h-2M32 54v-2M10 32h2"/>')
add('computadora','Computadora','Vida escolar','<rect x="9" y="9" width="46" height="33" rx="4" fill="currentColor" fill-opacity=".12"/><path d="M9 35h46M25 43v11M39 43v11M19 55h26M24 19l-5 5 5 5M40 19l5 5-5 5"/>')
add('estrella','Estrella','Decoraciones','<path d="m32 5 8 17 19 3-14 13 4 20-17-10-17 10 4-20L5 25l19-3Z" fill="currentColor" fill-opacity=".18"/>')
add('sol','Sol','Decoraciones','<circle cx="32" cy="32" r="15" fill="currentColor" fill-opacity=".18"/><path d="M32 3v8M32 53v8M3 32h8M53 32h8M11 11l6 6M47 47l6 6M11 53l6-6M47 17l6-6"/>')
add('arcoiris','Arcoíris','Decoraciones','<path d="M5 51V35a27 27 0 0 1 54 0v16M13 51V35a19 19 0 0 1 38 0v16M21 51V35a11 11 0 0 1 22 0v16M29 51V35a3 3 0 0 1 6 0v16"/>')
add('nube','Nube','Decoraciones','<path d="M17 49a13 13 0 0 1-1-26 17 17 0 0 1 33-3 15 15 0 0 1-2 29Z" fill="currentColor" fill-opacity=".12"/>')
add('manzana','Manzana','Decoraciones','<path d="M32 20C14 6 4 28 15 47c9 17 14 7 17 7s8 10 17-7C60 28 50 6 32 20Z" fill="currentColor" fill-opacity=".12"/><path d="M32 20V9M32 12C34 4 43 3 48 5c-1 9-9 11-16 7M18 28q-5 7 0 14"/>')
add('corazon','Corazón','Decoraciones','<path d="M32 55 10 33C-5 16 18 0 32 18 46 0 69 16 54 33Z" fill="currentColor" fill-opacity=".18"/>')
add('sonrisa','Carita feliz','Decoraciones','<circle cx="32" cy="32" r="25" fill="currentColor" fill-opacity=".12"/><path d="M20 39q12 15 24 0M22 24v4M42 24v4"/>')
add('hoja','Hoja y crecimiento','Decoraciones','<path d="M12 49C-2 22 30 9 57 7c-2 27-13 53-40 44M8 58 44 22M23 43V30M23 43h15" fill="currentColor" fill-opacity=".12"/>')
root=Path(__file__).resolve().parents[1]
symbols=''.join('<symbol id="'+i['id']+'" viewBox="0 0 64 64"><g fill="none" stroke="currentColor" stroke-width="2.4" stroke-linecap="round" stroke-linejoin="round">'+i['svg']+'</g></symbol>' for i in items)
(root/'src/main/resources/static/images/sitio-objetos-escolares.svg').write_text('<svg xmlns="http://www.w3.org/2000/svg">'+symbols+'</svg>\n')
entries=[{k:v for k,v in i.items() if k!='svg'} for i in items]
js="""(function(root){'use strict';
 const items=CATALOGO;
 const encontrar=id=>items.find(item=>item.id===id)||items[0];
 const url=id=>'/images/sitio-objetos-escolares.svg?v=20261111-objetos#'+encontrar(id).id;
 function crear(id){const svg=document.createElementNS('http://www.w3.org/2000/svg','svg'),use=document.createElementNS(svg.namespaceURI,'use');svg.setAttribute('viewBox','0 0 64 64');svg.setAttribute('aria-hidden','true');svg.setAttribute('focusable','false');use.setAttribute('href',url(id));svg.append(use);return svg;}
 const api={items,encontrar,url,crear};if(typeof module!=='undefined'&&module.exports)module.exports=api;root.NexoObjetos=api;
})(typeof window!=='undefined'?window:globalThis);
""".replace('CATALOGO',json.dumps(entries,ensure_ascii=False))
(root/'src/main/resources/static/js/sitio-objetos-escolares.js').write_text(js)
print(len(items),'objetos generados')
