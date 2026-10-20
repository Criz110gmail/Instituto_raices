package escuela.personal.service;

import escuela.auditoria.entity.AccionAuditoria;
import escuela.auditoria.service.RegistroAuditoriaService;
import escuela.common.exception.ReglaNegocioException;
import escuela.common.service.ValidacionVersion;
import escuela.institucion.repository.PlantelRepository;
import escuela.personal.dto.CambioPrincipalForm;
import escuela.personal.entity.*;
import escuela.personal.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.*;
import java.util.*;

@Service @Transactional(readOnly=true)
public class CambioPrincipalEmpleadoService {
    private final EmpleadoService empleados;
    private final EmpleadoRepository expedientes;
    private final EmpleadoPlantelRepository asignaciones;
    private final PlantelRepository planteles;
    private final RegistroAuditoriaService auditoria;

    @Autowired
    public CambioPrincipalEmpleadoService(EmpleadoService empleados, EmpleadoRepository expedientes,
            EmpleadoPlantelRepository asignaciones, PlantelRepository planteles, RegistroAuditoriaService auditoria) {
        this.empleados=empleados; this.expedientes=expedientes; this.asignaciones=asignaciones;
        this.planteles=planteles; this.auditoria=auditoria;
    }

    public record Vista(Long origenId, Long versionOrigen, String origen, Long destinoId, Long versionDestino,
                        Long plantelId, String destino, LocalDate fecha, LocalDate cierreAnterior,
                        LocalDate finNuevoPrincipal, LocalDate finAnteriorAdicional, boolean conservarAnterior) {}
    private record Preparacion(Empleado empleado, EmpleadoPlantel origen, EmpleadoPlantel destino,
                               escuela.institucion.entity.Plantel nuevo, Vista vista) {}

    public Vista visualizar(Long empleadoId, LocalDate fecha, Long plantelId, boolean conservar) {
        return preparar(empleadoId,fecha,plantelId,conservar).vista();
    }

    private Preparacion preparar(Long empleadoId, LocalDate fecha, Long plantelId, boolean conservar) {
        var empleado=empleados.autorizar(empleadoId,true,true);
        var hoy=LocalDate.now(ZoneId.of(empleado.getInstitucion().getZonaHoraria()));
        if(fecha==null || fecha.isBefore(hoy)) throw regla("El cambio debe comenzar hoy o en una fecha futura; el historial anterior no se reescribe.");
        if(plantelId==null) throw regla("Selecciona el nuevo plantel principal desde la ayuda.");
        var nuevo=planteles.findById(plantelId).orElseThrow(()->regla("Selecciona un plantel válido."));
        if(!Objects.equals(nuevo.getInstitucion().getId(),empleado.getInstitucion().getId())) throw new AccessDeniedException("Plantel fuera de tu institución");
        if(!nuevo.isActivo()) throw regla("El nuevo plantel debe estar activo.");
        var principales=asignaciones.findAll(cubre(empleadoId,fecha).and((r,q,b)->b.isTrue(r.get("principal"))),PageRequest.of(0,2));
        if(principales.getTotalElements()!=1) throw regla("Debe existir un único plantel principal en la fecha elegida. Revisa las asignaciones o registra primero el principal inicial.");
        var origen=principales.getContent().getFirst();
        if(Objects.equals(origen.getPlantel().getId(),plantelId)) throw regla("Ese plantel ya es principal en la fecha elegida; selecciona otro.");
        var destinos=asignaciones.findAll(cubre(empleadoId,fecha).and((r,q,b)->b.equal(r.get("plantel").get("id"),plantelId)),PageRequest.of(0,2));
        if(destinos.getTotalElements()>1) throw regla("Revisa las vigencias del nuevo plantel antes de continuar.");
        var destino=destinos.isEmpty()?null:destinos.getContent().getFirst();
        if(!fecha.isAfter(origen.getFechaInicio()) || destino!=null&&!fecha.isAfter(destino.getFechaInicio()))
            throw regla("La nueva etapa debe iniciar después del inicio de las asignaciones que reemplaza. Elige al menos el día siguiente para conservarlas en el historial.");
        LocalDate finNuevo=destino==null?origen.getFechaFin():destino.getFechaFin();
        var excluidos=destino==null?List.of(origen.getId()):List.of(origen.getId(),destino.getId());
        if(asignaciones.count(solapa(empleadoId,fecha,finNuevo,excluidos).and((r,q,b)->b.or(b.isTrue(r.get("principal")),b.equal(r.get("plantel").get("id"),plantelId))))>0)
            throw regla("El cambio se cruza con otra asignación o principal programado. Revisa las próximas vigencias antes de confirmar.");
        if(conservar && asignaciones.count(solapa(empleadoId,fecha,origen.getFechaFin(),excluidos).and((r,q,b)->b.equal(r.get("plantel").get("id"),origen.getPlantel().getId())))>0)
            throw regla("El plantel anterior ya tiene otra vigencia en ese rango. Revisa las próximas asignaciones.");
        var vista=new Vista(origen.getId(),origen.getVersion(),origen.getPlantel().getNombre(),
                destino==null?null:destino.getId(),destino==null?null:destino.getVersion(),plantelId,nuevo.getNombre(),
                fecha,fecha.minusDays(1),finNuevo,origen.getFechaFin(),conservar);
        return new Preparacion(empleado,origen,destino,nuevo,vista);
    }

    @Transactional
    public void confirmar(Long empleadoId,CambioPrincipalForm form) {
        empleados.autorizar(empleadoId,true,true);
        expedientes.bloquear(empleadoId).orElseThrow(()->regla("Empleado no disponible."));
        if(form.getMotivo()==null||form.getMotivo().isBlank()||form.getMotivo().length()>2000) throw regla("Explica el motivo del cambio, máximo 2000 caracteres.");
        var p=preparar(empleadoId,form.getFecha(),form.getPlantelId(),form.isConservarAnterior());
        if(!Objects.equals(form.getOrigenId(),p.origen().getId()) || !Objects.equals(form.getDestinoId(),p.vista().destinoId()))
            throw regla("Las asignaciones cambiaron desde la vista previa. Vuelve a visualizar antes de confirmar.");
        ValidacionVersion.verificar(p.origen(),form.getVersionOrigen(),"Plantel principal");
        if(p.destino()!=null) ValidacionVersion.verificar(p.destino(),form.getVersionDestino(),"Plantel adicional");
        else if(form.getVersionDestino()!=null) throw regla("Vuelve a visualizar el cambio de plantel.");
        p.origen().setFechaFin(p.vista().cierreAnterior());
        if(p.destino()!=null) p.destino().setFechaFin(p.vista().cierreAnterior());
        // Cerrar primero para respetar exclusiones inclusivas de PostgreSQL antes de insertar.
        asignaciones.flush();
        asignaciones.save(nueva(p.empleado(),p.nuevo(),form.getFecha(),p.vista().finNuevoPrincipal(),true));
        if(form.isConservarAnterior()) asignaciones.save(nueva(p.empleado(),p.origen().getPlantel(),form.getFecha(),p.vista().finAnteriorAdicional(),false));
        asignaciones.flush();
        auditoria.registrar(p.empleado().getInstitucion().getId(),AccionAuditoria.EMPLEADO_PRINCIPAL_CAMBIADO,
                "EMPLEADO",empleadoId,form.getMotivo().strip(),Map.of("origenAsignacionId",p.origen().getId(),
                "origenPlantelId",p.origen().getPlantel().getId(),"destinoPlantelId",p.nuevo().getId(),
                "fechaEfectiva",form.getFecha().toString(),"conservarAnterior",form.isConservarAnterior()));
    }

    private EmpleadoPlantel nueva(Empleado empleado,escuela.institucion.entity.Plantel plantel,LocalDate inicio,LocalDate fin,boolean principal) {
        var a=new EmpleadoPlantel();a.setEmpleado(empleado);a.setPlantel(plantel);a.setFechaInicio(inicio);a.setFechaFin(fin);a.setPrincipal(principal);return a;
    }
    private static Specification<EmpleadoPlantel> cubre(Long id,LocalDate fecha) {
        return (r,q,b)->b.and(b.equal(r.get("empleado").get("id"),id),b.lessThanOrEqualTo(r.get("fechaInicio"),fecha),
                b.or(b.isNull(r.get("fechaFin")),b.greaterThanOrEqualTo(r.get("fechaFin"),fecha)));
    }
    private static Specification<EmpleadoPlantel> solapa(Long id,LocalDate desde,LocalDate hasta,List<Long> excluidos) {
        return (r,q,b)->b.and(b.equal(r.get("empleado").get("id"),id),b.not(r.get("id").in(excluidos)),
                hasta==null?b.conjunction():b.lessThanOrEqualTo(r.get("fechaInicio"),hasta),
                b.or(b.isNull(r.get("fechaFin")),b.greaterThanOrEqualTo(r.get("fechaFin"),desde)));
    }
    private static ReglaNegocioException regla(String texto){return new ReglaNegocioException(texto);}
}
