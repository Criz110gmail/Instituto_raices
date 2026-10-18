package escuela.pendiente.dto;

import escuela.pendiente.entity.*;
import java.time.*;

public record PendienteVista(Long id,String titulo,String descripcion,LocalDate fechaCompromiso,
 EstadoPendiente estado,Long version,Instant realizadoEn,boolean vencido) {
 public static PendienteVista de(PendientePersonal p,LocalDate hoy){return new PendienteVista(p.getId(),p.getTitulo(),p.getDescripcion(),p.getFechaCompromiso(),p.getEstado(),p.getVersion(),p.getRealizadoEn(),p.getEstado().abierto()&&p.getFechaCompromiso().isBefore(hoy));}
 public PendienteForm formulario(){var f=new PendienteForm();f.setTitulo(titulo);f.setDescripcion(descripcion);f.setFechaCompromiso(fechaCompromiso);f.setEstado(estado);f.setVersion(version);return f;}
}
