package escuela.admin.dto;

import escuela.academico.dto.request.CicloEscolarRequest;
import escuela.academico.dto.response.CicloEscolarResponse;
import escuela.academico.entity.EstadoAcademico;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CicloEscolarForm {
    @NotNull private Long institucionId;
    @NotBlank @Size(max = 50) private String codigo;
    @NotBlank @Size(max = 150) private String nombre;
    @NotNull private LocalDate fechaInicio;
    @NotNull private LocalDate fechaFin;
    @NotNull private EstadoAcademico estado = EstadoAcademico.PLANIFICADO;
    private boolean predeterminado;
    private Long version;

    public CicloEscolarRequest request() {
        return new CicloEscolarRequest(institucionId, codigo, nombre, fechaInicio, fechaFin,
                estado, predeterminado, version);
    }

    public static CicloEscolarForm desde(CicloEscolarResponse ciclo) {
        CicloEscolarForm form = new CicloEscolarForm();
        form.institucionId = ciclo.institucionId();
        form.codigo = ciclo.codigo();
        form.nombre = ciclo.nombre();
        form.fechaInicio = ciclo.fechaInicio();
        form.fechaFin = ciclo.fechaFin();
        form.estado = ciclo.estado();
        form.predeterminado = ciclo.predeterminado();
        form.version = ciclo.auditoria().version();
        return form;
    }
}
