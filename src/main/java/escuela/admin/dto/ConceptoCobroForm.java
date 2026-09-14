package escuela.admin.dto;

import escuela.cobranza.dto.request.ConceptoCobroRequest;
import escuela.cobranza.dto.response.ConceptoCobroResponse;
import escuela.cobranza.entity.CategoriaConceptoCobro;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConceptoCobroForm {
    @NotNull private Long institucionId;
    @NotBlank @Size(max = 50) private String codigo;
    @NotBlank @Size(max = 150) private String nombre;
    @Size(max = 2000) private String descripcion;
    @NotNull private CategoriaConceptoCobro categoria = CategoriaConceptoCobro.COLEGIATURA;
    private boolean permiteBeca;
    private boolean permiteDescuento;
    private boolean permiteRecargo;
    private boolean activo = true;
    private Long version;

    public ConceptoCobroRequest request() {
        return new ConceptoCobroRequest(institucionId, codigo, nombre, descripcion, categoria,
                permiteBeca, permiteDescuento, permiteRecargo, activo, version);
    }

    public static ConceptoCobroForm desde(ConceptoCobroResponse concepto) {
        ConceptoCobroForm form = new ConceptoCobroForm();
        form.institucionId = concepto.institucionId();
        form.codigo = concepto.codigo();
        form.nombre = concepto.nombre();
        form.descripcion = concepto.descripcion();
        form.categoria = concepto.categoria();
        form.permiteBeca = concepto.permiteBeca();
        form.permiteDescuento = concepto.permiteDescuento();
        form.permiteRecargo = concepto.permiteRecargo();
        form.activo = concepto.activo();
        form.version = concepto.auditoria().version();
        return form;
    }
}
