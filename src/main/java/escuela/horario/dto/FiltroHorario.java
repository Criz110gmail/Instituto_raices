package escuela.horario.dto;

import java.time.LocalDate;

public record FiltroHorario(Long maestroId,Long grupoId,Integer diaSemana,LocalDate fecha,
                            Boolean activo,int pagina,int tamanio){
    public FiltroHorario normalizado(){return new FiltroHorario(maestroId,grupoId,diaSemana,fecha,activo,
            Math.max(0,pagina),java.util.Set.of(10,25,50,100).contains(tamanio)?tamanio:25);}
    public FiltroHorario conPagina(int p,int t){return new FiltroHorario(maestroId,grupoId,diaSemana,fecha,activo,p,t);}
}
