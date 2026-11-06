package escuela.admin.service;

import escuela.archivo.repository.ArchivoRepository;
import escuela.archivo.storage.AlmacenamientoArchivo;
import escuela.institucion.repository.InstitucionRepository;
import escuela.personal.service.*;
import escuela.personal.service.ReciboNominaService.*;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class JasperReciboNominaServiceTest {
    private Detalle ejemplo(String tipo,String estado,boolean largo){
        var percepciones=new BigDecimal(largo?"218":"200");var deducciones=tipo.equals("CERO")?percepciones:new BigDecimal("100");
        var r=new Fila(tipo,7L,8L,9L,10L,11L,"EMP-QA","Persona Ficticia de Capacitación", "NOV-2030-QA",estado,LocalDate.of(2030,11,1),LocalDate.of(2030,11,30),percepciones.subtract(deducciones),"MXN","06/11/2026 10:00");
        var lineas=new ArrayList<PreparacionNominaService.Linea>();
        lineas.add(new PreparacionNominaService.Linea("APOYO","Apoyo extraordinario de capacitación","PERCEPCION",new BigDecimal("200"),null,null));
        lineas.add(new PreparacionNominaService.Linea("PREST","Recuperación de préstamo de prueba","DEDUCCION",deducciones,null,null));
        lineas.add(new PreparacionNominaService.Linea("POSPUESTO","Concepto pospuesto sin efecto en el pago","DEDUCCION",BigDecimal.ZERO,null,null));
        lineas.add(new PreparacionNominaService.Linea("PATRONAL","Aportación privada de la institución","APORTACION_PATRONAL",new BigDecimal("500"),null,null));
        if(largo)for(int k=0;k<18;k++)lineas.add(new PreparacionNominaService.Linea("INFO-"+k,"Percepción de capacitación número "+k+" con una descripción extensa para verificar el salto de página y la legibilidad del comprobante histórico.","PERCEPCION",BigDecimal.ONE,null,null));
        return new Detalle(r,1L,"Institución Ficticia de Capacitación",BigDecimal.ZERO,percepciones,deducciones,new BigDecimal("500"),"Motivo privado de capacitación, sin efectos reales.","qa.ficticio",estado.equals("CANCELADO")?"07/11/2026 10:10 · DEV-QA · Fondos recuperados para la prueba":"","Coordinador académico",lineas,tipo.equals("CERO")?List.of():List.of(new Fuente("BANCO-QA · Cuenta ficticia","CHEQUE",r.neto(),"1001",estado.equals("CANCELADO")?"Cancelado · No reutilizable":"Emitido")),List.of(new Prestamo("PRE-000001","PREST · Préstamo ficticio",deducciones,deducciones,BigDecimal.ZERO)));
    }
    private byte[] generar(Detalle d){
        var recibos=mock(ReciboNominaService.class);when(recibos.obtener(d.recibo().tipo(),7L)).thenReturn(d);
        var instituciones=mock(InstitucionRepository.class);when(instituciones.findById(1L)).thenReturn(Optional.empty());
        var pdf=new JasperReciboNominaService(recibos,instituciones,mock(ArchivoRepository.class),mock(AlmacenamientoArchivo.class));
        var salida=new ByteArrayOutputStream();pdf.exportar(d.recibo().tipo(),7L,salida);return salida.toByteArray();
    }
    @Test void netoCeroIncluyeConceptosSinExponerDesgloseDePrestamo()throws Exception{
        var bytes=generar(ejemplo("CERO","NETO_CERO",false));
        try(var pdf=Loader.loadPDF(bytes)){var texto=new PDFTextStripper().getText(pdf);assertThat(texto).contains("RECIBO INTERNO DE NÓMINA","NETO CERO CONFIRMADO","$0.00 MXN","Apoyo extraordinario de capacitación","Recuperación de préstamo de prueba","Total de percepciones","Total de deducciones","Coordinador académico","01/11/2030 al 30/11/2030","Página","No es CFDI").doesNotContain("BANCO-QA","PRE-000001","Capital","Interés");assertThat(pdf.getNumberOfPages()).isEqualTo(1);}
        var dir=Files.createDirectories(Path.of("target/qa-recibos-nomina"));Files.write(dir.resolve("recibo-interno-ejemplo.pdf"),bytes);
    }
    @Test void canceladoMantieneAdvertenciaSinExponerChequeNiMotivo()throws Exception{
        try(var pdf=Loader.loadPDF(generar(ejemplo("PAGO","CANCELADO",false)))){assertThat(new PDFTextStripper().getText(pdf)).contains("PAGO CANCELADO","Sin vigencia","$100.00 MXN").doesNotContain("Fondos recuperados","Cheque 1001","DEV-QA","No reutilizable");}
    }
    @Test void conceptosLargosContinuanEnOtrasPaginasSinPerderUltimoNiTotales()throws Exception{
        var bytes=generar(ejemplo("PAGO","PAGADO",true));
        try(var pdf=Loader.loadPDF(bytes)){
            assertThat(pdf.getNumberOfPages()).isGreaterThan(1);assertThat(new PDFTextStripper().getText(pdf)).contains("Percepción de capacitación número 17","$218.00 MXN","$118.00 MXN","NETO DEL RECIBO","Alcance del documento").doesNotContain("INFO-17","Concepto pospuesto","Aportación privada");
            for(int pagina=1;pagina<=pdf.getNumberOfPages();pagina++){
                var lector=new PDFTextStripper();lector.setStartPage(pagina);lector.setEndPage(pagina);var texto=lector.getText(pdf).replaceAll("\\s+"," ");
                for(int k=0;k<18;k++)if(texto.contains("número "+k+" con"))assertThat(texto).contains("Percepción de capacitación número "+k+" con una descripción extensa para verificar el salto de página y la legibilidad del comprobante histórico.");
            }
        }
        var dir=Files.createDirectories(Path.of("target/qa-recibos-nomina"));Files.write(dir.resolve("conceptos-largos.pdf"),bytes);
    }
    @Test void todosLosEstadosMuestranConceptosPeroExcluyenDatosAdministrativos()throws Exception{
        var dir=Files.createDirectories(Path.of("target/qa-recibos-nomina"));
        for(var caso:List.of(List.of("PAGO","PAGADO"),List.of("PAGO","CANCELADO"),List.of("CERO","NETO_CERO"),List.of("HISTORICO","HISTORICO"))){
            var bytes=generar(ejemplo(caso.get(0),caso.get(1),false));
            try(var pdf=Loader.loadPDF(bytes)){var t=new PDFTextStripper().getText(pdf);assertThat(t).contains("Persona Ficticia","EMP-QA","RN-","Coordinador académico","Apoyo extraordinario de capacitación","Recuperación de préstamo de prueba").doesNotContain("qa.ficticio","Motivo privado","Fondos recuperados","BANCO-QA","CHEQUE","PRE-000001","PREST","APOYO","INFO-","Responsable","Registro:","BOR-","NOV-2030-QA","Capital","Interés","Origen del pago","Justificación","Concepto pospuesto","Aportación privada");}
            Files.write(dir.resolve("resumen-"+caso.get(1).toLowerCase(Locale.ROOT)+".pdf"),bytes);
        }
    }
    @Test void sueldoEsUnaPercepcionYSoloSeSumaUnaVez()throws Exception{
        var d=ejemplo("PAGO","PAGADO",false);var r=d.recibo();
        var r2=new Fila(r.tipo(),r.documentoId(),r.filaId(),r.nominaId(),r.empleadoId(),r.periodoId(),r.codigo(),r.nombre(),r.periodo(),r.estado(),r.inicio(),r.fin(),new BigDecimal("1100"),r.moneda(),r.fecha());
        var lineas=new ArrayList<>(d.lineas());lineas.add(new PreparacionNominaService.Linea("SUELDO-QA","Coordinación académica","SUELDO",new BigDecimal("1000"),null,null));
        var resumen=new Detalle(r2,d.institucionId(),d.institucion(),new BigDecimal("1000"),d.percepciones(),d.deducciones(),d.aportaciones(),d.motivo(),d.responsable(),d.cancelacion(),d.puestos(),lineas,d.fuentes(),d.prestamos());
        try(var pdf=Loader.loadPDF(generar(resumen))){var texto=new PDFTextStripper().getText(pdf);assertThat(texto).contains("Total de percepciones","$1,200.00 MXN","Total de deducciones","$100.00 MXN","NETO DEL RECIBO","$1,100.00 MXN","Sueldo base: Coordinación académica");assertThat(texto.split("Sueldo base:",-1)).hasSize(2);}
    }
    @Test void fotografiaSinPartidasNoInventaNombresDeConceptos()throws Exception{
        var d=ejemplo("HISTORICO","HISTORICO",false);
        var antiguo=new Detalle(d.recibo(),d.institucionId(),d.institucion(),d.sueldo(),d.percepciones(),d.deducciones(),d.aportaciones(),d.motivo(),d.responsable(),d.cancelacion(),d.puestos(),List.of(),d.fuentes(),d.prestamos());
        try(var pdf=Loader.loadPDF(generar(antiguo))){assertThat(new PDFTextStripper().getText(pdf).replaceAll("\\s+"," ")).contains("sin desglose histórico disponible","$200.00 MXN","$100.00 MXN").doesNotContain("Apoyo extraordinario","Recuperación de préstamo");}
    }
}
