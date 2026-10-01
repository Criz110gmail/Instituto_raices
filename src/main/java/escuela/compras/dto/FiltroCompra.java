package escuela.compras.dto;

import escuela.compras.entity.EstadoCompra;
import java.time.LocalDate;

public record FiltroCompra(Long institucionId,Long plantelId,Long proveedorId,Long cuentaId,EstadoCompra estado,
                           LocalDate desde,LocalDate hasta,String texto,int pagina,int tamanio){
    public FiltroCompra normalizado(){return new FiltroCompra(institucionId,plantelId,proveedorId,cuentaId,estado,desde,hasta,texto==null?"":texto.trim(),Math.max(0,pagina),java.util.Set.of(10,25,50,100).contains(tamanio)?tamanio:25);}
    public FiltroCompra conPagina(int p,int t){return new FiltroCompra(institucionId,plantelId,proveedorId,cuentaId,estado,desde,hasta,texto,p,t);}
}
