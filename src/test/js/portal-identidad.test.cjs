const {test}=require('node:test'),assert=require('node:assert/strict');const C=require('../../main/resources/static/js/portal-identidad.js');
test('paleta libre conserva contraste mínimo y rechaza estilos arbitrarios',()=>{for(let r=0;r<256;r+=51)for(let g=0;g<256;g+=51)for(let b=0;b<256;b+=51){const color='#'+[r,g,b].map(v=>v.toString(16).padStart(2,'0')).join('');const a=C.lum(color),v=C.lum(C.ink(color));assert.ok((Math.max(a,v)+.05)/(Math.min(a,v)+.05)>=4.5);}for(const bad of ['red','#123','#ffffff;display:none','url(javascript:1)',null])assert.equal(C.variables(bad,'#ffffff','#123456'),null);});
test('colores y texto de vista previa coinciden con la política del servidor',()=>{assert.equal(C.variables('#ffffff','#eeeeee','#000000')['--pi-story-text'],'#000000');assert.equal(C.variables('#ffffff','#eeeeee','#000000')['--pi-on-accent'],'#ffffff');assert.equal(C.variables('#000000','#ffffff','#ffff00')['--pi-link'],'#173d32');});

test('paletas automáticas conservan contraste en claro y oscuro incluso en extremos',()=>{
 for(const principal of ['#ffffff','#000000','#194f3d','#0000ff'])for(const acento of ['#ffffff','#000000','#ffff00','#ff00ff','#00ffff','#187d43','#ff0000'])for(const fondo of ['#ffffff','#000000','#f4f4f4'])for(const mode of ['light','dark']){
  const data={principal,fondo,acento},before=JSON.stringify(data),p=C.palette(data,mode),get=k=>p['--pi-ui-'+k];
  for(const [text,bg]of [['text','surface'],['muted','surface'],['link','surface'],['on-primary','primary'],['on-accent','accent'],['on-action','action'],['on-action','hover'],['disabled-text','disabled']])assert.ok(C.contrast(get(text),get(bg))>=4.5,`${mode} ${acento} ${text}`);
  assert.ok(C.contrast(get('border'),get('surface'))>=3);assert.ok(C.contrast(get('focus'),get('surface'))>=3);
  assert.equal(JSON.stringify(data),before);assert.notEqual(get('action'),undefined);
 }
});
test('modo oscuro avanzado conserva el color elegido y adapta acciones y enlaces por separado',()=>{
 const data={principal:'#194f3d',fondo:'#ffffff',acento:'#ffff00',oscuroPrincipal:'#220033',oscuroFondo:'#090510',oscuroAcento:'#ffee00'};
 const dark=C.palette(data,'dark'),light=C.palette(data,'light');
 assert.equal(dark['--pi-ui-primary'],'#220033');assert.equal(dark['--pi-ui-bg'],'#090510');assert.equal(dark['--pi-ui-accent'],'#ffee00');
 assert.equal(light['--pi-ui-primary'],'#194f3d');assert.notEqual(dark['--pi-ui-action'],dark['--pi-ui-link']);
 assert.ok(C.contrast(dark['--pi-ui-action'],'#ffffff')>=4.5);
 assert.equal(C.palette({...data,principal:'url(x)'}),null);
});
