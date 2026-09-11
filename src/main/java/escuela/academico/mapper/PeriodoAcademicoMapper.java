package escuela.academico.mapper;

import escuela.academico.dto.request.PeriodoAcademicoRequest;
import escuela.academico.dto.response.PeriodoAcademicoResponse;
import escuela.academico.entity.CicloEscolar;
import escuela.academico.entity.NivelEducativo;
import escuela.academico.entity.PeriodoAcademico;
import org.springframework.stereotype.Component;

import static escuela.common.mapper.AuditoriaMapper.desde;
import static escuela.common.mapper.NormalizacionTexto.codigo;
import static escuela.common.mapper.NormalizacionTexto.limpiar;

@Component
public class PeriodoAcademicoMapper {

    public PeriodoAcademico nuevo(PeriodoAcademicoRequest dto, CicloEscolar ciclo, NivelEducativo nivel) {
        PeriodoAcademico entidad = new PeriodoAcademico();
        actualizar(entidad, dto, ciclo, nivel);
        return entidad;
    }

    public void actualizar(PeriodoAcademico entidad, PeriodoAcademicoRequest dto,
                           CicloEscolar ciclo, NivelEducativo nivel) {
        entidad.setCicloEscolar(ciclo);
        entidad.setNivelEducativo(nivel);
        entidad.setCodigo(codigo(dto.codigo()));
        entidad.setNombre(limpiar(dto.nombre()));
        entidad.setTipo(dto.tipo());
        entidad.setOrden(dto.orden());
        entidad.setFechaInicio(dto.fechaInicio());
        entidad.setFechaFin(dto.fechaFin());
        entidad.setEstado(dto.estado());
        entidad.setObservaciones(limpiar(dto.observaciones()));
    }

    public PeriodoAcademicoResponse respuesta(PeriodoAcademico e) {
        return new PeriodoAcademicoResponse(e.getId(), e.getCicloEscolar().getId(),
                e.getNivelEducativo().getId(), e.getCodigo(), e.getNombre(), e.getTipo(), e.getOrden(),
                e.getFechaInicio(), e.getFechaFin(), e.getEstado(), e.getObservaciones(), desde(e));
    }
}
