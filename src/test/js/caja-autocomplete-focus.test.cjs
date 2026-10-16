const {test}=require('node:test');
const assert=require('node:assert/strict');
const fs=require('node:fs'),vm=require('node:vm');

test('cajas: foco y clic cargan iniciales y reabren opciones sin repetir petición',async()=>{
    class E {
        constructor(){this.value='';this.hidden=true;this.dataset={};this.children=[];this.listeners={};}
        addEventListener(n,f){(this.listeners[n]??=[]).push(f);}
        dispatchEvent(e){for(const f of this.listeners[e.type]||[])f(e);}
        setAttribute(){} removeAttribute(){} replaceChildren(){this.children=[];}
        append(...v){this.children.push(...v);} appendChild(v){this.append(v);} focus(){this.dispatchEvent({type:'focus'});}
        contains(){return false;}
    }
    const entrada=new E(),id=new E(),lista=new E(),estado=new E(),limpiar=new E(),institucion=new E(),contenedor=new E(),doc=new E();
    entrada.id='cuentaTexto';institucion.value='1';
    contenedor.dataset={institutionInput:'institucionId',refreshOnFocus:'true',endpoint:'/admin/autocompletado/cuentas-caja'};
    contenedor.hasAttribute=()=>false;contenedor.querySelectorAll=()=>[];
    contenedor.querySelector=s=>({'.autocomplete-input':entrada,'.autocomplete-value':id,'.autocomplete-results':lista,'.autocomplete-status':estado,'.autocomplete-clear':limpiar}[s]);
    doc.querySelectorAll=()=>[contenedor];doc.getElementById=()=>institucion;doc.createElement=()=>new E();
    let timer;const requests=[];
    vm.runInNewContext(fs.readFileSync('src/main/resources/static/js/autocomplete.js','utf8'),{
        document:doc,URLSearchParams,URL,AbortController,Event:class{constructor(type){this.type=type;}},
        setTimeout(fn){timer=fn;},clearTimeout(){timer=null;},
        fetch(url,opts){return new Promise(resolve=>requests.push({url,opts,resolve}));}
    });
    const flush=async()=>{for(let i=0;i<8;i++)await Promise.resolve();};
    const respuesta=n=>requests[n].resolve({ok:true,json:async()=>({resultados:[{id:3,titulo:'CAJA-SOBRANTE-TEST',detalle:'CAJA'}]})});
    entrada.focus();entrada.dispatchEvent({type:'click'});assert.equal(requests.length,1);
    assert.equal(new URL(requests[0].url,'http://test').searchParams.get('q'),'');
    assert.equal(new URL(requests[0].url,'http://test').searchParams.get('institucionId'),'1');
    respuesta(0);await flush();assert.equal(lista.hidden,false);
    doc.dispatchEvent({type:'click',target:new E()});assert.equal(lista.hidden,true);
    entrada.focus();assert.equal(requests.length,2);respuesta(1);await flush();assert.equal(lista.hidden,false);
    entrada.value='ab';entrada.dispatchEvent({type:'input'});assert.equal(timer,null);assert.equal(requests.length,2);
    entrada.value='SOB';entrada.dispatchEvent({type:'input'});timer();assert.equal(new URL(requests[2].url,'http://test').searchParams.get('q'),'SOB');
    respuesta(2);await flush();lista.children[0].dispatchEvent({type:'click'});assert.equal(id.value,3);
    entrada.focus();assert.equal(requests.length,3);
    institucion.value='2';institucion.dispatchEvent({type:'change'});assert.equal(id.value,'');
    entrada.dispatchEvent({type:'click'});assert.equal(new URL(requests[3].url,'http://test').searchParams.get('institucionId'),'2');
    for(const path of ['corte-caja-apertura-form.html','cortes-caja.html']) {
        const html=fs.readFileSync('src/main/resources/templates/admin/'+path,'utf8');
        assert.match(html,/data-endpoint="\/admin\/autocompletado\/cuentas-caja"[^>]*data-refresh-on-focus="true"/);
    }
});
