package escuela.personal.service;
import escuela.personal.dto.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import java.io.*;
@Service @RequiredArgsConstructor public class ExcelEmpleadoService {
 private final EmpleadoService service;
 @Transactional(readOnly=true) public void exportar(FiltroEmpleado f,OutputStream out)throws IOException{
  try(var book=new SXSSFWorkbook(100)){try{var s=book.createSheet("Empleados");var head=s.createRow(0);String[] cols={"Código laboral","Nombre","Correo de contacto","Teléfono","Expediente","Código docente","Perfil docente"};for(int i=0;i<cols.length;i++){head.createCell(i).setCellValue(cols[i]);s.setColumnWidth(i,(i==1?42:26)*256);}int row=1,page=0;org.springframework.data.domain.Page<EmpleadoVista> result;
   do{result=service.listar(new FiltroEmpleado(f.institucionId(),f.q(),f.estado(),page++,100,f.empleadoId(),f.empleadoTexto()));for(var e:result){var r=s.createRow(row++);String[] values={e.codigo(),e.nombre(),e.email(),e.telefono(),e.activo()?"Activo":"Inactivo",e.codigoDocente(),e.maestroId()==null?"Sin perfil":e.docenteActivo()?"Activo":"Inactivo"};for(int i=0;i<values.length;i++)r.createCell(i).setCellValue(values[i]==null?"":values[i]);}}while(result.hasNext());s.createFreezePane(0,1);s.setAutoFilter(new org.apache.poi.ss.util.CellRangeAddress(0,Math.max(0,row-1),0,cols.length-1));book.write(out);
  }finally{book.dispose();}}
 }
}
