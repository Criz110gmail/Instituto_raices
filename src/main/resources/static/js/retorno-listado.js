// El servidor valida el destino; el campo sólo conserva contexto al enviar formularios.
document.addEventListener('DOMContentLoaded',()=>{
    const retorno=document.body.dataset.retornoListado;
    if(!retorno)return;
    document.querySelectorAll('form[method="post"]').forEach(form=>{
        if(new URL(form.action,location.href).origin!==location.origin)return;
        if(form.querySelector('input[name="retorno"]'))return;
        const input=document.createElement('input');input.type='hidden';input.name='retorno';
        input.value=retorno;form.append(input);
    });
});
