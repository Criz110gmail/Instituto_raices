package escuela.alumno.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TipoDocumentoAlumno {
    ACTA_NACIMIENTO("Acta de nacimiento"),
    CURP("CURP"),
    COMPROBANTE_DOMICILIO("Comprobante de domicilio"),
    CONSTANCIA_CERTIFICADO("Constancia o certificado"),
    AUTORIZACION("Autorización"),
    DOCUMENTO_MEDICO("Documento médico"),
    OTRO("Otro documento");

    private final String etiqueta;
}

