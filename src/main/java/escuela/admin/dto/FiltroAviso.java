package escuela.admin.dto;

import java.util.Set;

public record FiltroAviso(Long institucionId, Long plantelId, String texto, String estado, int pagina, int tamanio) {
    public FiltroAviso normalizado() { String q=texto==null?"":texto.trim().replaceAll("\\s+"," ");
        String e=estado==null?"TODOS":estado.toUpperCase(); if(!Set.of("TODOS","BORRADOR","PUBLICADO","RETIRADO").contains(e)) e="TODOS";
        int t=Set.of(10,25,50,100).contains(tamanio)?tamanio:25; return new FiltroAviso(institucionId,plantelId,q,e,Math.max(0,pagina),t); }
    public FiltroAviso conPagina(int p,int t){ return new FiltroAviso(institucionId,plantelId,texto,estado,p,t); }
}
