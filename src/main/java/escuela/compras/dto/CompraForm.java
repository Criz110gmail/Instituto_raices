package escuela.compras.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import java.util.*;

@Getter @Setter
public class CompraForm {
    @NotNull(message="Selecciona la institución") private Long institucionId;
    private Long plantelId;
    @NotNull(message="Selecciona el proveedor") private Long proveedorId;
    @NotNull(message="Selecciona la cuenta de pago") private Long cuentaId;
    @NotNull(message="Selecciona la categoría financiera") private Long motivoFinancieroId;
    @NotNull(message="Indica la fecha de compra") @DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME) private LocalDateTime fechaOperacion;
    @Size(max=150) private String documentoReferencia;
    @Size(max=2000) private String observaciones;
    @Valid @NotEmpty(message="Agrega al menos una partida") @Size(max=100,message="Una compra admite máximo 100 partidas") private List<CompraPartidaForm> partidas=new ArrayList<>();
    private Long version;
}
