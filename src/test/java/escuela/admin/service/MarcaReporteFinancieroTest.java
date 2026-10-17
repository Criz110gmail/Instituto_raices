package escuela.admin.service;

import escuela.admin.dto.*;
import escuela.institucion.service.InstitucionService;
import escuela.institucion.dto.response.InstitucionResponse;
import java.io.*;
import java.math.BigDecimal;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;
import javax.imageio.ImageIO;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.util.Units;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class MarcaReporteFinancieroTest {
 @Test void cabeceraCentradaSinCuadriculaConservaDatosYPreferenciaDeLaHoja() throws Exception {
  var bytes=new ByteArrayOutputStream();
  try(var libro=new org.apache.poi.xssf.streaming.SXSSFWorkbook(200)) {
   for(boolean cuadricula:List.of(true,false)) {
    var hoja=libro.createSheet("Cabecera"+cuadricula);hoja.setDisplayGridlines(cuadricula);
    MarcaReporteFinanciero.excel(libro,hoja,11,"Balanza de movimientos y saldos");
    for(int r=4;r<305;r++)hoja.createRow(r).createCell(0).setCellValue(r);
   }
   libro.write(bytes);libro.dispose();
  }
  try(var libro=new XSSFWorkbook(new ByteArrayInputStream(bytes.toByteArray()))) {
   for(boolean cuadricula:List.of(true,false)) {
    var hoja=libro.getSheet("Cabecera"+cuadricula);
    assertThat(hoja.isDisplayGridlines()).isEqualTo(cuadricula);
    assertThat(hoja.getMergedRegions()).contains(new org.apache.poi.ss.util.CellRangeAddress(0,2,2,10));
    var titulo=hoja.getRow(0).getCell(2);
    assertThat(titulo.getStringCellValue()).isEqualTo("Balanza de movimientos y saldos");
    assertThat(titulo.getCellStyle().getAlignment()).isEqualTo(HorizontalAlignment.CENTER);
    assertThat(titulo.getCellStyle().getVerticalAlignment()).isEqualTo(VerticalAlignment.CENTER);
    for(int r=0;r<4;r++)for(int c=0;c<11;c++) {
     var estilo=hoja.getRow(r).getCell(c).getCellStyle();
     assertThat(estilo.getFillPattern()).isEqualTo(FillPatternType.SOLID_FOREGROUND);
     assertThat(estilo.getFillForegroundColor()).isEqualTo(IndexedColors.WHITE.getIndex());
    }
    assertThat(hoja.getRow(4).getCell(0).getCellStyle().getFillPattern()).isEqualTo(FillPatternType.NO_FILL);
    assertThat(hoja.getRow(304).getCell(0).getNumericCellValue()).isEqualTo(304);
   }
  }
 }
 @Test void logoUnicoEnCadaHojaMantieneProporcionYSitioParaLosDatos() throws Exception {
  try(var libro=new XSSFWorkbook()) {
   for(String nombre:List.of("Resumen","Detalle")) {
    var hoja=libro.createSheet(nombre);hoja.setColumnWidth(0,20*256);hoja.setColumnWidth(1,60*256);
    MarcaReporteFinanciero.excel(libro,hoja,2);
    hoja.createRow(MarcaReporteFinanciero.FILAS).createCell(0).setCellValue(300.50);
    assertThat(hoja.getDrawingPatriarch().getShapes()).hasSize(1);
    var xml=hoja.getDrawingPatriarch().getCTDrawing();
    assertThat(xml.sizeOfAbsoluteAnchorArray()).isEqualTo(1);
    assertThat(xml.sizeOfTwoCellAnchorArray()).isZero();
    var a=xml.getAbsoluteAnchorArray(0);
    double ratio=(double)a.getExt().getCx()/a.getExt().getCy();
    assertThat(ratio).isCloseTo((double)MarcaReporteFinanciero.imagen().getWidth()/MarcaReporteFinanciero.imagen().getHeight(),within(.02));
    assertThat(hoja.getRow(4).getCell(0).getNumericCellValue()).isEqualTo(300.50);
    assertThat(hoja.getRow(0).getHeightInPoints()).isEqualTo(30);
    assertThat(a.getPic().getNvPicPr().getCNvPicPr().getPicLocks().getNoChangeAspect()).isTrue();
   }
   assertThat(libro.getAllPictures()).hasSize(1);
  }
 }
 @Test void tamanoRealPersistidoNoDependeDeFilasNiColumnas() throws Exception {
  var bytes=new ByteArrayOutputStream();
  try(var libro=new XSSFWorkbook()) {
   var hoja=libro.createSheet("Logo");MarcaReporteFinanciero.excel(libro,hoja,2);
   hoja.setColumnWidth(0,76*256);hoja.getRow(0).setHeightInPoints(60);
   libro.write(bytes);
  }
  try(var libro=new XSSFWorkbook(new ByteArrayInputStream(bytes.toByteArray()))) {
   var dibujo=libro.getSheetAt(0).getDrawingPatriarch();
   assertThat(dibujo.getShapes()).hasSize(1);
   var a=dibujo.getCTDrawing().getAbsoluteAnchorArray(0);
   var logo=MarcaReporteFinanciero.imagen();double escala=Math.min(192.0/logo.getWidth(),128.0/logo.getHeight());
   assertThat(a.getExt().getCx()).isEqualTo(Units.pixelToEMU((int)Math.round(logo.getWidth()*escala)));
   assertThat(a.getExt().getCy()).isEqualTo(Units.pixelToEMU((int)Math.round(logo.getHeight()*escala)));
   assertThat(a.getPic().getSpPr().getXfrm().getExt().getCx()).isEqualTo(a.getExt().getCx());
   assertThat(a.getPic().getSpPr().getXfrm().getExt().getCy()).isEqualTo(a.getExt().getCy());
  }
 }
 @Test void tesoreriaYConcentradoPdfExcelIncluyenLogoSinCambiarImportes() throws Exception {
  var consulta=mock(ReporteFinancieroConsultaService.class);var instituciones=mock(InstitucionService.class);
  when(instituciones.obtener(1L)).thenReturn(new InstitucionResponse(1L,"QA","Institución de prueba",null,null,null,null,null,null,null,null,null,null,"MX",null,"America/Mexico_City","MXN",true,null));
  when(consulta.normalizar(any(FiltroReporteTesoreria.class))).thenAnswer(i->i.getArgument(0));
  when(consulta.normalizar(any(FiltroConcentradoCobranza.class))).thenAnswer(i->i.getArgument(0));
  var cero=BigDecimal.ZERO;var monto=new BigDecimal("300");var fecha=LocalDate.of(2026,10,16);
  var tesoreria=new ResultadoReporteTesoreria(new PageImpl<>(List.of(new ReporteTesoreriaFila(fecha,"Cuenta de prueba","BANCO","Institucional",1,cero,cero,cero,cero,monto,"MXN"))),
    new ResumenTesoreria(1,cero,cero,cero,cero,monto,new BigDecimal("2000"),new BigDecimal("1700"),true,"MXN"));
  when(consulta.tesoreria(any())).thenReturn(tesoreria);
  var concentrado=new ResultadoConcentradoCobranza(new PageImpl<>(List.of(new ConcentradoCobranzaFila("Material de prueba",1,monto,cero,monto,cero,"MXN"))),new ResumenEstadoCuenta(1,monto,cero,monto,cero,"MXN"));
  when(consulta.concentradoCobranza(any())).thenReturn(concentrado);
  var ft=new FiltroReporteTesoreria(1L,null,"",null,"MENSUAL",fecha.withDayOfMonth(1),fecha,0,100);
  var fc=new FiltroConcentradoCobranza(1L,null,"CONCEPTO",fecha,0,100);
  var pdf=new JasperReporteFinancieroService(consulta,mock(EstadoCuentaCuentaService.class),instituciones);
  Path dir=Path.of("target/qa-logo-financiero");Files.createDirectories(dir);
  for(String tipo:List.of("tesoreria","concentrado")) {
   var out=new ByteArrayOutputStream();if(tipo.equals("tesoreria"))pdf.tesoreria(ft,out);else pdf.concentradoCobranza(fc,out);
   Files.write(dir.resolve(tipo+".pdf"),out.toByteArray());
   try(var doc=Loader.loadPDF(out.toByteArray())) {
    assertThat(new PDFTextStripper().getText(doc)).contains("Institución de prueba","$300.00");
    assertThat(doc.getPage(0).getResources().getXObjectNames()).anySatisfy(n->{
     try{assertThat(doc.getPage(0).getResources().getXObject(n)).isInstanceOf(PDImageXObject.class);}catch(IOException e){throw new UncheckedIOException(e);}
    });
    ImageIO.write(new PDFRenderer(doc).renderImageWithDPI(0,120),"png",dir.resolve(tipo+".png").toFile());
   }
   var excel=new ByteArrayOutputStream();
   if(tipo.equals("tesoreria"))new ExcelReporteFinancieroService(consulta).tesoreria(ft,excel);
   else new ExcelConcentradoCobranzaService(consulta,instituciones,mock(escuela.institucion.service.PlantelService.class)).exportar(fc,excel);
   Files.write(dir.resolve(tipo+".xlsx"),excel.toByteArray());
   try(var libro=new XSSFWorkbook(new ByteArrayInputStream(excel.toByteArray()))) {
    for(var hoja:libro) assertThat(((XSSFSheet)hoja).getDrawingPatriarch().getShapes()).hasSize(1);
    assertThat(libro.getAllPictures()).hasSize(1);
    if(tipo.equals("tesoreria"))assertThat(libro.getSheetAt(0).getRow(5).getCell(9).getNumericCellValue()).isEqualTo(300);
    else assertThat(libro.getSheet("Resumen").getRow(13).getCell(1).getNumericCellValue()).isEqualTo(300);
   }
  }
 }
}
