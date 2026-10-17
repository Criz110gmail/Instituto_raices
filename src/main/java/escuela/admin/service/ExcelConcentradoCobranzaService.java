package escuela.admin.service;

import escuela.admin.dto.*;
import escuela.institucion.service.InstitucionService;
import escuela.institucion.service.PlantelService;
import java.io.*;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.*;

@Service @RequiredArgsConstructor
public class ExcelConcentradoCobranzaService {
    private final ReporteFinancieroConsultaService consulta;
    private final InstitucionService instituciones;
    private final PlantelService planteles;

    @Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
    public void exportar(FiltroConcentradoCobranza original, OutputStream salida) throws IOException {
        var filtro=consulta.normalizar(original).conPagina(0,100);
        // Validar institución/plantel y alcance antes de escribir el archivo.
        var bloque=consulta.concentradoCobranza(filtro);
        String institucion=instituciones.obtener(filtro.institucionId()).nombre();
        String plantel=filtro.plantelId()==null?"Todos autorizados":planteles.obtener(filtro.plantelId()).nombre();
        try(var libro=new SXSSFWorkbook(200)) {
            libro.setCompressTempFiles(true);
            Font cuerpo=libro.createFont();cuerpo.setFontName("Arial");cuerpo.setFontHeightInPoints((short)10);
            CellStyle texto=libro.createCellStyle();texto.setFont(cuerpo);texto.setVerticalAlignment(VerticalAlignment.CENTER);
            CellStyle dinero=libro.createCellStyle();dinero.cloneStyleFrom(texto);
            dinero.setDataFormat(libro.createDataFormat().getFormat("\"$\"#,##0.00;-\"$\"#,##0.00"));
            CellStyle fecha=libro.createCellStyle();fecha.cloneStyleFrom(texto);fecha.setDataFormat(libro.createDataFormat().getFormat("dd/mm/yyyy"));
            CellStyle etiqueta=libro.createCellStyle();etiqueta.cloneStyleFrom(texto);etiqueta.setWrapText(true);
            Font blanco=libro.createFont();blanco.setFontName("Arial");blanco.setFontHeightInPoints((short)10);blanco.setBold(true);blanco.setColor(IndexedColors.WHITE.getIndex());
            CellStyle encabezado=libro.createCellStyle();encabezado.setFont(blanco);encabezado.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());encabezado.setFillPattern(FillPatternType.SOLID_FOREGROUND);encabezado.setAlignment(HorizontalAlignment.CENTER);encabezado.setVerticalAlignment(VerticalAlignment.CENTER);
            Sheet resumen=libro.createSheet("Resumen");resumen.setDisplayGridlines(false);
            MarcaReporteFinanciero.excel(libro,resumen,2,"Concentrado de cobranza");
            resumen.setColumnWidth(0,32*256);resumen.setColumnWidth(1,60*256);
            poner(resumen.createRow(MarcaReporteFinanciero.FILAS),0,"Concentrado de cobranza",texto);
            poner(resumen.createRow(2+MarcaReporteFinanciero.FILAS),0,"Fecha de corte",texto);Cell corte=resumen.getRow(2+MarcaReporteFinanciero.FILAS).createCell(1);corte.setCellValue(filtro.fechaCorte());corte.setCellStyle(fecha);
            par(resumen,3,"Institución",institucion,texto,etiqueta);
            par(resumen,4,"Plantel",plantel,texto,etiqueta);
            par(resumen,5,"Agrupar por",agrupacion(filtro.agrupacion()),texto,etiqueta);
            Row cabecera=resumen.createRow(7+MarcaReporteFinanciero.FILAS);cabecera.setHeightInPoints(26);poner(cabecera,0,"Indicador",encabezado);poner(cabecera,1,"Total",encabezado);
            var totales=bloque.resumen();
            par(resumen,8,"Cargos",totales.cargos(),texto,texto);
            par(resumen,9,"Importe",totales.importeTotal(),texto,dinero);
            par(resumen,10,"Cobrado",totales.aplicado(),texto,dinero);
            par(resumen,11,"Por cobrar",totales.saldo(),texto,dinero);
            par(resumen,12,"Vencido",totales.saldoVencido(),texto,dinero);
            par(resumen,13,"Moneda",totales.moneda(),texto,texto);

            Sheet detalle=libro.createSheet("Concentrado");detalle.setDisplayGridlines(false);MarcaReporteFinanciero.excel(libro,detalle,7,"Concentrado de cobranza");detalle.createFreezePane(1,MarcaReporteFinanciero.FILAS+1);
            String[] columnas={agrupacion(filtro.agrupacion()),"Cargos","Importe","Cobrado","Por cobrar","Vencido","Moneda"};
            Row titulos=detalle.createRow(MarcaReporteFinanciero.FILAS);titulos.setHeightInPoints(28);
            for(int c=0;c<columnas.length;c++){poner(titulos,c,columnas[c],encabezado);detalle.setColumnWidth(c,(c==0?76:c==1||c==6?12:20)*256);}
            int fila=MarcaReporteFinanciero.FILAS+1,pagina=0;
            while(true) {
                for(var dato:bloque.pagina().getContent()) {
                    Row r=detalle.createRow(fila++);r.setHeightInPoints(Math.max(24,14*((dato.grupo().length()+69)/70)+8));
                    poner(r,0,dato.grupo(),etiqueta);poner(r,1,dato.cargos(),texto);
                    poner(r,2,dato.importe(),dinero);poner(r,3,dato.aplicado(),dinero);
                    poner(r,4,dato.saldo(),dinero);poner(r,5,dato.vencido(),dinero);poner(r,6,dato.moneda(),texto);
                }
                if(!bloque.pagina().hasNext())break;
                bloque=consulta.concentradoCobranza(filtro.conPagina(++pagina,100));
            }
            if(fila==MarcaReporteFinanciero.FILAS+1)poner(detalle.createRow(MarcaReporteFinanciero.FILAS+1),0,"Sin cartera para los filtros seleccionados.",texto);
            else detalle.setAutoFilter(new CellRangeAddress(MarcaReporteFinanciero.FILAS,fila-1,0,6));
            libro.setActiveSheet(0);libro.setSelectedTab(0);resumen.setSelected(true);
            libro.write(salida);libro.dispose();
        }
    }
    private static String agrupacion(String valor){return switch(valor){case "PLANTEL"->"Plantel";case "CICLO"->"Ciclo escolar";default->"Concepto de cobro";};}
    private static void par(Sheet hoja,int fila,String titulo,Object valor,CellStyle etiqueta,CellStyle formato){Row r=hoja.createRow(fila+MarcaReporteFinanciero.FILAS);r.setHeightInPoints(30);poner(r,0,titulo,etiqueta);poner(r,1,valor,formato);}
    private static void poner(Row fila,int columna,Object valor,CellStyle formato){Cell c=fila.createCell(columna);if(valor instanceof BigDecimal n)c.setCellValue(n.doubleValue());else if(valor instanceof Number n)c.setCellValue(n.doubleValue());else c.setCellValue(String.valueOf(valor));c.setCellStyle(formato);}
}
