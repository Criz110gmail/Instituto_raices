package escuela.calendario.dto;

import escuela.calendario.entity.TipoFechaCalendario;
import java.time.LocalDate;

public record FiltroCalendarioEscolar(Long institucionId,Long cicloEscolarId,Long plantelId,Long nivelEducativoId,
    TipoFechaCalendario tipo,Boolean activo,LocalDate desde,LocalDate hasta,String texto,int pagina,int tamanio){
    public FiltroCalendarioEscolar normalizado(){return new FiltroCalendarioEscolar(institucionId,cicloEscolarId,plantelId,nivelEducativoId,tipo,activo,desde,hasta,texto==null?"":texto.trim(),Math.max(0,pagina),java.util.Set.of(10,25,50,100).contains(tamanio)?tamanio:25);}
    public FiltroCalendarioEscolar conPagina(int p,int t){return new FiltroCalendarioEscolar(institucionId,cicloEscolarId,plantelId,nivelEducativoId,tipo,activo,desde,hasta,texto,p,t);}
}
