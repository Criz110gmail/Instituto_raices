package escuela.cobranza.mapper;

import escuela.cobranza.dto.request.CuotaAlumnoRequest;
import escuela.cobranza.dto.response.CuotaAlumnoResponse;
import escuela.cobranza.entity.ConceptoCobro;
import escuela.cobranza.entity.CuotaAlumno;
import escuela.inscripcion.entity.Inscripcion;
import org.springframework.stereotype.Component;

import static escuela.common.mapper.AuditoriaMapper.desde;
import static escuela.common.mapper.NormalizacionTexto.codigo;
import static escuela.common.mapper.NormalizacionTexto.limpiar;

@Component
public class CuotaAlumnoMapper {

    public CuotaAlumno nueva(CuotaAlumnoRequest request, Inscripcion inscripcion,
                             ConceptoCobro concepto) {
        CuotaAlumno entidad = new CuotaAlumno();
        entidad.setInscripcion(inscripcion);
        entidad.setConceptoCobro(concepto);
        actualizar(entidad, request);
        return entidad;
    }

    public void actualizar(CuotaAlumno entidad, CuotaAlumnoRequest request) {
        entidad.setImporteBase(request.importeBase());
        entidad.setMoneda(codigo(request.moneda()));
        entidad.setFrecuencia(request.frecuencia());
        entidad.setFechaInicio(request.fechaInicio());
        entidad.setFechaFin(request.fechaFin());
        entidad.setDiaVencimiento(request.diaVencimiento());
        entidad.setFechaVencimientoUnico(request.fechaVencimientoUnico());
        entidad.setGeneracionAutomatica(request.generacionAutomatica());
        entidad.setMotivoImportePersonalizado(limpiar(request.motivoImportePersonalizado()));
        entidad.setEstado(request.estado());
    }

    public CuotaAlumnoResponse respuesta(CuotaAlumno entidad) {
        var inscripcion = entidad.getInscripcion();
        var alumno = inscripcion.getAlumno();
        return new CuotaAlumnoResponse(entidad.getId(), inscripcion.getId(),
                alumno.getInstitucion().getId(), inscripcion.getPlantel().getId(),
                inscripcion.getPlantel().getNombre(), alumno.getId(), alumno.getMatricula(),
                nombre(alumno.getNombres(), alumno.getPrimerApellido(), alumno.getSegundoApellido()),
                inscripcion.getNumeroInscripcion(), entidad.getConceptoCobro().getId(),
                entidad.getConceptoCobro().getCodigo(), entidad.getConceptoCobro().getNombre(),
                entidad.getImporteBase(), entidad.getMoneda(), entidad.getFrecuencia(),
                entidad.getFechaInicio(), entidad.getFechaFin(), entidad.getDiaVencimiento(),
                entidad.getFechaVencimientoUnico(), entidad.isGeneracionAutomatica(),
                entidad.getMotivoImportePersonalizado(), entidad.getEstado(), desde(entidad));
    }

    private String nombre(String... partes) {
        return java.util.stream.Stream.of(partes)
                .filter(valor -> valor != null && !valor.isBlank())
                .collect(java.util.stream.Collectors.joining(" "));
    }
}
