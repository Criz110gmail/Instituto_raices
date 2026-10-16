package escuela.admin.dto;

import java.time.LocalDate;

public record FiltroCatalogo(String q, String estado, LocalDate fecha, int pagina, int tamanio,
                             LocalDate desde, LocalDate hasta, String tipoFecha,String cobertura,Integer anioCobertura,String conceptoCobro,Long alumnoId,String alumnoTexto) {
    public FiltroCatalogo(String q,String estado,LocalDate fecha,int pagina,int tamanio,LocalDate desde,LocalDate hasta,String tipoFecha,String cobertura,Integer anioCobertura,String conceptoCobro){this(q,estado,fecha,pagina,tamanio,desde,hasta,tipoFecha,cobertura,anioCobertura,conceptoCobro,null,"");}
    public FiltroCatalogo(String q,String estado,LocalDate fecha,int pagina,int tamanio,LocalDate desde,LocalDate hasta,String tipoFecha,String cobertura,Integer anioCobertura){this(q,estado,fecha,pagina,tamanio,desde,hasta,tipoFecha,cobertura,anioCobertura,"");}
    public FiltroCatalogo(String q,String estado,LocalDate fecha,int pagina,int tamanio,LocalDate desde,LocalDate hasta,String tipoFecha){this(q,estado,fecha,pagina,tamanio,desde,hasta,tipoFecha,"TODOS",null);}

    public FiltroCatalogo(String q, String estado, LocalDate fecha, int pagina, int tamanio) {
        this(q, estado, fecha, pagina, tamanio, null, null, "VENCIMIENTO");
    }

    public FiltroCatalogo(String q, String estado, int pagina, int tamanio) {
        this(q, estado, null, pagina, tamanio);
    }

    public FiltroCatalogo normalizado() {
        return new FiltroCatalogo(q == null ? "" : q.trim(),
                estado == null ? "TODOS" : estado.trim().toUpperCase(), fecha,
                Math.max(0, pagina), Math.max(10, Math.min(100, tamanio)), desde, hasta,
                "REGISTRO".equals(tipoFecha) ? "REGISTRO" : "VENCIMIENTO",cobertura==null?"TODOS":cobertura.trim().toUpperCase(java.util.Locale.ROOT),anioCobertura,conceptoCobro==null?"":conceptoCobro.trim(),alumnoId,alumnoTexto==null?"":alumnoTexto.trim());
    }

    public FiltroCatalogo conPagina(int nuevaPagina) {
        return new FiltroCatalogo(q, estado, fecha, nuevaPagina, tamanio, desde, hasta, tipoFecha,cobertura,anioCobertura,conceptoCobro,alumnoId,alumnoTexto);
    }

    public boolean tieneRango() { return desde != null || hasta != null; }
    public void validarCobertura(){
        if ((alumnoId != null && alumnoId <= 0) || (alumnoId == null && alumnoTexto != null && !alumnoTexto.isBlank()))
            throw new escuela.common.exception.ReglaNegocioException("Selecciona un alumno de la lista de sugerencias o limpia el filtro para consultar todos los pagos.");
        if(!java.util.Set.of("TODOS","SIN_ANUAL","ANUAL","ANUAL_LIQUIDADO","ANUAL_PENDIENTE","ANUAL_NO_VIGENTE","ANUAL_NO_LIQUIDADO").contains(cobertura))
            throw new escuela.common.exception.ReglaNegocioException("Selecciona una cobertura de pago válida.");
        if(anioCobertura!=null&&(anioCobertura<1900||anioCobertura>9999))
            throw new escuela.common.exception.ReglaNegocioException("El año cubierto debe estar entre1900 y9999.");
    }
    public void validarRango() {
        if (desde != null && hasta != null && desde.isAfter(hasta))
            throw new escuela.common.exception.ReglaNegocioException("La fecha Desde no puede ser posterior a Hasta. Corrige el rango para buscar o exportar.");
    }
}
