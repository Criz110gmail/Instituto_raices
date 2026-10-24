package escuela.personal.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

/** Captura coordinada: las condiciones y los conceptos originales siguen siendo la fuente. */
@Getter @Setter
public class ConfiguracionEmpleadoNominaForm {
 private boolean cambiarSueldo;
 private boolean cambiarCalendario;
 private CondicionSalarialForm sueldo = new CondicionSalarialForm();
 private CalendarioEmpleadoForm calendario = new CalendarioEmpleadoForm();
 private List<ConceptoEmpleadoForm> conceptos = new ArrayList<>();
 private Long revisionConceptos;
 private Long versionLaboral;
 private String motivo;
}
