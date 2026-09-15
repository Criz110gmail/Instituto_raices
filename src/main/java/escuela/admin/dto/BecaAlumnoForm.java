package escuela.admin.dto;
import escuela.cobranza.dto.request.BecaAlumnoRequest;import escuela.cobranza.dto.response.BecaAlumnoResponse;import escuela.cobranza.entity.*;
import jakarta.validation.constraints.*;import lombok.Getter;import lombok.Setter;import org.springframework.format.annotation.DateTimeFormat;
import java.math.BigDecimal;import java.time.LocalDate;
@Getter @Setter public class BecaAlumnoForm {
 @NotNull private Long institucionId; @NotNull private Long plantelId; @NotNull private Long inscripcionId;
 @NotNull private Long tipoBecaId; @NotNull private Long conceptoCobroId; @NotNull private ModalidadBeca modalidad=ModalidadBeca.PORCENTAJE;
 private BigDecimal porcentaje; private BigDecimal montoFijo; @Size(max=3) private String moneda;
 @NotNull @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) private LocalDate fechaInicio;
 @NotNull @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) private LocalDate fechaFin;
 @NotBlank @Size(max=2000) private String motivo; @NotNull private EstadoBeca estado=EstadoBeca.ACTIVA; private Long version;
 public BecaAlumnoRequest request(){BigDecimal p=modalidad==ModalidadBeca.PORCENTAJE?porcentaje:null;BigDecimal m=modalidad==ModalidadBeca.MONTO_FIJO?montoFijo:null;String monedaFinal=modalidad==ModalidadBeca.MONTO_FIJO?moneda:null;return new BecaAlumnoRequest(inscripcionId,tipoBecaId,conceptoCobroId,modalidad,p,m,monedaFinal,fechaInicio,fechaFin,motivo,estado,version);}
 public static BecaAlumnoForm desde(BecaAlumnoResponse r){BecaAlumnoForm f=new BecaAlumnoForm();f.institucionId=r.institucionId();f.plantelId=r.plantelId();f.inscripcionId=r.inscripcionId();f.tipoBecaId=r.tipoBecaId();f.conceptoCobroId=r.conceptoCobroId();f.modalidad=r.modalidad();f.porcentaje=r.porcentaje();f.montoFijo=r.montoFijo();f.moneda=r.moneda();f.fechaInicio=r.fechaInicio();f.fechaFin=r.fechaFin();f.motivo=r.motivo();f.estado=r.estado();f.version=r.auditoria().version();return f;}
}
