package escuela.admin.dto;

import escuela.asistencia.dto.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;
import java.util.*;

@Getter @Setter
public class CapturaAsistenciaForm {
    @NotNull private Long institucionId;
    @NotNull private Long grupoId;
    private String grupoTexto;
    @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) private LocalDate fecha;
    @Valid private List<FilaAsistenciaForm> filas = new ArrayList<>();
    public CapturaAsistenciaRequest request() { return new CapturaAsistenciaRequest(grupoId, fecha, filas.stream().map(FilaAsistenciaForm::request).toList()); }
    public static CapturaAsistenciaForm desde(HojaAsistenciaResponse hoja, Long institucionId) {
        CapturaAsistenciaForm f = new CapturaAsistenciaForm(); f.institucionId=institucionId; f.grupoId=hoja.grupoId();
        f.grupoTexto=hoja.grupo()+" · "+hoja.grado(); f.fecha=hoja.fecha(); f.filas=hoja.filas().stream().map(FilaAsistenciaForm::desde).toList(); return f;
    }
}
