package escuela.cobranza.dto.request;
import jakarta.validation.constraints.NotNull;import java.time.LocalDate;
public record GeneracionRecargosRequest(@NotNull Long institucionId,Long plantelId,@NotNull LocalDate fechaCorte,
        java.util.UUID seleccionId,@jakarta.validation.constraints.Size(max=5000) java.util.Set<String> excluidos,
        @jakarta.validation.constraints.Size(max=5000) java.util.Set<String> incluidos){
    public GeneracionRecargosRequest {excluidos=excluidos==null?java.util.Set.of():java.util.Set.copyOf(excluidos);incluidos=incluidos==null?null:java.util.Set.copyOf(incluidos);}
    public GeneracionRecargosRequest(Long institucionId,Long plantelId,LocalDate fechaCorte){this(institucionId,plantelId,fechaCorte,null,java.util.Set.of(),null);}
}
