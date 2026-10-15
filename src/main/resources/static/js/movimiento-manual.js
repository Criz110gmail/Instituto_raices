document.addEventListener('DOMContentLoaded',()=>{
    const id=document.getElementById('motivoFinancieroId');
    const texto=document.getElementById('motivoTexto');
    if(!id||!texto)return;
    const validar=()=>texto.setCustomValidity(id.value?'':'Selecciona un motivo de la lista de resultados.');
    id.addEventListener('change',validar);validar();
});
