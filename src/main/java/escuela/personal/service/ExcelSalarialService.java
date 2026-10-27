package escuela.personal.service;
import escuela.personal.dto.FiltroSalarial;
import lombok.RequiredArgsConstructor;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.*;
import java.time.format.DateTimeFormatter;
import static escuela.personal.service.CondicionesSalarialesService.etiqueta;
@Service @RequiredArgsConstructor @Transactional(readOnly=true)
public class ExcelSalarialService {
 private final CondicionesSalarialesService service;
 private final EmpleadoService empleados;
 private static String fecha(java.time.LocalDate d){return d==null?"Sin fecha de fin":d.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));}
 public void condiciones(Long id,FiltroSalarial f,OutputStream out)throws IOException{
  var empleado=empleados.obtener(id);service.listar(id,f.pagina(0,10));
  try(var libro=new SXSSFWorkbook(100)){var hoja=libro.createSheet("Condiciones salariales");String[] columnas={"Código laboral","Empleado","Código puesto","Puesto histórico","Sueldo bruto base","Moneda","Base","Frecuencia de pago","Inicio","Fin","Estado registro","Motivo"};
   var cab=hoja.createRow(0);for(int c=0;c<columnas.length;c++){cab.createCell(c).setCellValue(columnas[c]);hoja.setColumnWidth(c,(c==3||c==11?45:25)*256);}
   var dinero=libro.createCellStyle();dinero.setDataFormat(libro.createDataFormat().getFormat("#,##0.00"));
   int fila=1,pagina=0;org.springframework.data.domain.Page<CondicionesSalarialesService.Condicion> bloque;
   do{bloque=service.listar(id,f.pagina(pagina++,100));for(var s:bloque){var r=hoja.createRow(fila++);String[] textos={empleado.codigo(),empleado.nombre(),s.puestoCodigo(),s.puestoNombre(),"",s.moneda(),etiqueta(s.base()),etiqueta(s.frecuencia()),fecha(s.inicio()),fecha(s.fin()),s.estado(),s.motivo()};for(int c=0;c<textos.length;c++)r.createCell(c).setCellValue(textos[c]);r.getCell(4).setCellValue(s.importeBruto().doubleValue());r.getCell(4).setCellStyle(dinero);}}while(bloque.hasNext());
   hoja.createFreezePane(0,1);hoja.setAutoFilter(new org.apache.poi.ss.util.CellRangeAddress(0,Math.max(0,fila-1),0,columnas.length-1));libro.write(out);
  }
 }
 public void puestos(String q,String estado,OutputStream out)throws IOException{puestos(q,estado,null,out);}
 public void puestos(String q,String estado,Long filtroPuestoId,OutputStream out)throws IOException{
  service.puestos(q,estado,0,10,filtroPuestoId);
  try(var libro=new SXSSFWorkbook(100)){var hoja=libro.createSheet("Puestos");String[] cols={"Código","Nombre","Descripción","Estado","Horas semanales","Sueldo mensual","Configuración"};var cab=hoja.createRow(0);for(int c=0;c<cols.length;c++){cab.createCell(c).setCellValue(cols[c]);hoja.setColumnWidth(c,35*256);}int fila=1,pag=0;org.springframework.data.domain.Page<CondicionesSalarialesService.Puesto> bloque;
   do{bloque=service.puestos(q,estado,pag++,100,filtroPuestoId);for(var p:bloque){var r=hoja.createRow(fila++);String[] valores={p.codigo(),p.nombre(),p.descripcion(),p.activo()?"Activo":"Inactivo"};for(int c=0;c<valores.length;c++)r.createCell(c).setCellValue(valores[c]==null?"":valores[c]);if(p.horasSemanales()!=null)r.createCell(4).setCellValue(p.horasSemanales().doubleValue());else r.createCell(4).setCellValue("Sin registrar");if(p.sueldoMensual()!=null)r.createCell(5).setCellValue(p.sueldoMensual().doubleValue());else r.createCell(5).setCellValue("Sin registrar");r.createCell(6).setCellValue(p.completo()?"Completo":"Pendiente de completar");}}while(bloque.hasNext());hoja.createFreezePane(0,1);libro.write(out);
  }
 }
}
