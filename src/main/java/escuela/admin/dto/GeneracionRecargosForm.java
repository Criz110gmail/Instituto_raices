package escuela.admin.dto;
import escuela.cobranza.dto.request.GeneracionRecargosRequest;import jakarta.validation.constraints.NotNull;import lombok.Getter;import lombok.Setter;import org.springframework.format.annotation.DateTimeFormat;import java.time.LocalDate;
@Getter @Setter public class GeneracionRecargosForm{@NotNull private Long institucionId;private Long plantelId;@NotNull @DateTimeFormat(iso=DateTimeFormat.ISO.DATE)private LocalDate fechaCorte=LocalDate.now();public GeneracionRecargosRequest request(){return new GeneracionRecargosRequest(institucionId,plantelId,fechaCorte);}}
