package escuela.docente.service;

import escuela.docente.dto.PlaneacionDocumento;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.time.*;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

class PdfPlaneacionServiceTest {
    @Test void generaDocumentoLegibleConSecuenciaSemanal() throws Exception {
        var documento=new PlaneacionDocumento(1L,2L,1,"Instituto Raíces","Ana Docente","M-01","Centro","2026-2027","Tercero","A",LocalDate.of(2026,9,21),LocalDate.of(2026,9,25),"Publicada","Proyecto del agua","Comprender el ciclo del agua","Pensamiento crítico","Evaporación","Observar","Colaborar","Observación","Rúbrica","Cartulina","Lectura diaria","Apoyo visual","Cierre semanal",List.of(new PlaneacionDocumento.Materia(3L,"CN","Ciencias")),List.of(new PlaneacionDocumento.Alineacion(3L,"Ciencias","Saberes y pensamiento científico","Ciclo del agua","Explica sus etapas")),List.of(new PlaneacionDocumento.Actividad(3L,"Ciencias",LocalDate.of(2026,9,21),"Exploramos el agua","Recuperar ideas","Realizar experimento","Compartir conclusiones",45,"Dibujar el ciclo",null)),Instant.parse("2026-09-20T12:00:00Z"));
        ByteArrayOutputStream salida=new ByteArrayOutputStream();new PdfPlaneacionService().exportar(documento,salida);
        assertThat(salida.toByteArray()).startsWith("%PDF".getBytes());
        try(var pdf=Loader.loadPDF(salida.toByteArray())){String texto=new PDFTextStripper().getText(pdf);assertThat(texto).contains("PLANEACIÓN SEMANAL","Ana Docente","Ciencias","Exploramos el agua","Página 1 de");}
    }
}
