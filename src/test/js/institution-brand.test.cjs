const fs = require('node:fs'), vm = require('node:vm'), assert = require('node:assert/strict');
const source = fs.readFileSync('src/main/resources/static/js/institution-brand.js','utf8');
const surface = (color, image='none', parentElement=null) => ({parentElement, style:{backgroundColor:color,backgroundImage:image}});
const images = [];
function logo(parentElement, src='/images/logo1.png') {
 const attrs={src};const image={parentElement,getAttribute:n=>attrs[n],setAttribute:(n,v)=>attrs[n]=v,removeAttribute:n=>delete attrs[n],attrs};images.push(image);return image;
}
const dark=logo(surface('rgb(15, 40, 30)'));
const light=logo(surface('rgb(255, 255, 255)'));
const gradient=logo(surface('rgba(0, 0, 0, 0)','linear-gradient(rgb(11, 25, 48), rgb(18, 57, 87))'));
const custom=logo(surface('rgb(10, 20, 30)'),'/accesos/ESCUELA/admin/logo.png');
const transparent=logo(surface('transparent','none',surface('rgb(20, 30, 40)')));
let onThemeChange;
vm.runInNewContext(source,{document:{readyState:'complete',documentElement:{},body:{},querySelectorAll:()=>images,addEventListener(){}},window:{addEventListener(){}},getComputedStyle:e=>e.style,requestAnimationFrame:fn=>fn(),MutationObserver:class{constructor(fn){onThemeChange=fn;}observe(){}}});
for(const image of [dark,gradient,transparent]){assert.equal(image.attrs['data-logo-tone'],'white');assert.match(image.attrs.src,/logo_blanco\.svg/);}
assert.equal(light.attrs['data-logo-tone'],'original');assert.match(light.attrs.src,/logo_general\.png/);
assert.equal(custom.attrs.src,'/accesos/ESCUELA/admin/logo.png');assert.equal(custom.attrs['data-logo-tone'],undefined);
console.log('Marca: blanco sobre oscuro/gradiente/transparente, original sobre claro y logos personalizados intactos.');
const original = fs.readFileSync('src/main/resources/static/images/logo_general.png');
assert.ok(original.equals(fs.readFileSync('src/main/resources/static/images/logo1.png')));
const svg = fs.readFileSync('src/main/resources/static/images/logo_blanco.svg','utf8');
const embedded = svg.match(/href="data:image\/png;base64,([^"]+)"/);
assert.ok(embedded);assert.ok(Buffer.from(embedded[1],'base64').equals(original));
assert.match(svg,/0 0 0 0 1 0 0 0 0 1 0 0 0 0 1 0 0 0 1 0/);
console.log('Archivos: original exacto conservado; SVG blanco usa los mismos píxeles y conserva el alfa.');
