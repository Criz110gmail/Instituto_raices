package escuela.cobranza.dto.request;
import jakarta.validation.constraints.NotNull;import java.time.LocalDate;
public record GeneracionRecargosRequest(@NotNull Long institucionId,Long plantelId,@NotNull LocalDate fechaCorte){}
