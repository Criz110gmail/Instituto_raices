package escuela.admin.dto;

import org.junit.jupiter.api.Test;
import org.springframework.format.annotation.DateTimeFormat;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;

class FormatoFechasFormularioTest {

    @Test
    void controlesDeFechaUsanElFormatoIsoQueExigeHtml() throws NoSuchFieldException {
        assertIso(AlumnoForm.class, "fechaNacimiento", "fechaIngreso");
        assertIso(CicloEscolarForm.class, "fechaInicio", "fechaFin");
        assertIso(PeriodoAcademicoForm.class, "fechaInicio", "fechaFin");
        assertIso(TutorForm.class, "fechaNacimiento");
    }

    private void assertIso(Class<?> tipo, String... campos) throws NoSuchFieldException {
        for (String campo : campos) {
            Field field = tipo.getDeclaredField(campo);
            DateTimeFormat formato = field.getAnnotation(DateTimeFormat.class);
            assertThat(formato)
                    .as("El campo %s.%s debe declarar @DateTimeFormat", tipo.getSimpleName(), campo)
                    .isNotNull();
            assertThat(formato.iso()).isEqualTo(DateTimeFormat.ISO.DATE);
        }
    }
}
