package escuela.compras.dto;

public record FiltroProveedor(Long institucionId,Boolean activo,String texto,int pagina,int tamanio){
    public FiltroProveedor normalizado(){return new FiltroProveedor(institucionId,activo,texto==null?"":texto.trim(),Math.max(0,pagina),java.util.Set.of(10,25,50,100).contains(tamanio)?tamanio:25);}
    public FiltroProveedor conPagina(int p,int t){return new FiltroProveedor(institucionId,activo,texto,p,t);}
}
