package escuela.admin.service;

import escuela.admin.dto.*;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ExcelReporteFinancieroService {
    private final ReporteFinancieroConsultaService consulta;

    public void estadoCuenta(FiltroEstadoCuentaAlumno filtro, OutputStream salida) throws IOException {
        try (SXSSFWorkbook libro = libro()) {
            Sheet hoja = libro.createSheet("Estado de cuenta");
            CellStyle fecha = libro.createCellStyle();
            fecha.setDataFormat(libro.createDataFormat().getFormat("dd/mm/yyyy"));
            encabezado(hoja, libro, "Estado de cuenta del alumno", "Cargo", "Plantel", "Ciclo", "Concepto", "Descripción",
                    "Emisión", "Vencimiento", "Original", "Ajustes", "Total", "Aplicado",
                    "Saldo", "Situación", "Moneda");
            int fila = MarcaReporteFinanciero.FILAS + 1, pagina = 0;
            ResultadoEstadoCuentaAlumno bloque;
            do {
                bloque = consulta.estadoCuenta(filtro.conPagina(pagina++, 100));
                for (EstadoCuentaCargoFila dato : bloque.pagina().getContent()) {
                    Row r = hoja.createRow(fila++);
                    r.createCell(0).setCellValue(dato.cargoId()); r.createCell(1).setCellValue(dato.plantel());
                    r.createCell(2).setCellValue(dato.ciclo()); r.createCell(3).setCellValue(dato.concepto());
                    r.createCell(4).setCellValue(dato.descripcion());
                    fecha(r, 5, dato.fechaEmision(), fecha);
                    fecha(r, 6, dato.fechaVencimiento(), fecha); numero(r, 7, dato.importeOriginal());
                    numero(r, 8, dato.ajustes()); numero(r, 9, dato.importeTotal()); numero(r, 10, dato.aplicado());
                    numero(r, 11, dato.saldo()); r.createCell(12).setCellValue(dato.situacion());
                    r.createCell(13).setCellValue(dato.moneda());
                }
            } while (pagina < bloque.pagina().getTotalPages());
            terminar(hoja, 14); libro.write(salida); libro.dispose();
        }
    }

    public void tesoreria(FiltroReporteTesoreria filtro, OutputStream salida) throws IOException {
        try (SXSSFWorkbook libro = libro()) {
            Sheet hoja = libro.createSheet("Tesorería");
            encabezado(hoja, libro, "Balanza de movimientos y saldos", "Periodo", "Cuenta", "Tipo", "Alcance", "Movimientos",
                    "Ingresos operativos", "Egresos operativos", "Neto operativo",
                    "Traspasos entrada", "Traspasos salida", "Moneda");
            int fila = MarcaReporteFinanciero.FILAS + 1, pagina = 0;
            ResultadoReporteTesoreria bloque;
            do {
                bloque = consulta.tesoreria(filtro.conPagina(pagina++, 100));
                for (ReporteTesoreriaFila dato : bloque.pagina().getContent()) {
                    Row r = hoja.createRow(fila++);
                    r.createCell(0).setCellValue(dato.periodo().toString()); r.createCell(1).setCellValue(dato.cuenta());
                    r.createCell(2).setCellValue(dato.tipoCuenta()); r.createCell(3).setCellValue(dato.alcanceCuenta());
                    r.createCell(4).setCellValue(dato.movimientos()); numero(r, 5, dato.ingresosOperativos());
                    numero(r, 6, dato.egresosOperativos()); numero(r, 7, dato.netoOperativo());
                    numero(r, 8, dato.traspasosEntrada()); numero(r, 9, dato.traspasosSalida());
                    r.createCell(10).setCellValue(dato.moneda());
                }
            } while (pagina < bloque.pagina().getTotalPages());
            terminar(hoja, 11); libro.write(salida); libro.dispose();
        }
    }

    private SXSSFWorkbook libro() { SXSSFWorkbook l = new SXSSFWorkbook(200); l.setCompressTempFiles(true); return l; }
    private void encabezado(Sheet hoja, Workbook libro, String titulo, String... columnas) {
        MarcaReporteFinanciero.excel(libro, hoja, columnas.length, titulo);
        CellStyle estilo = libro.createCellStyle(); estilo.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        estilo.setFillPattern(FillPatternType.SOLID_FOREGROUND); Font f = libro.createFont();
        f.setBold(true); f.setColor(IndexedColors.WHITE.getIndex()); estilo.setFont(f);
        Row r = hoja.createRow(MarcaReporteFinanciero.FILAS); for (int i=0;i<columnas.length;i++) { Cell c=r.createCell(i);c.setCellValue(columnas[i]);c.setCellStyle(estilo); }
    }
    private void numero(Row fila, int indice, BigDecimal valor) { fila.createCell(indice).setCellValue(valor.doubleValue()); }
    private void fecha(Row fila, int indice, java.time.LocalDate valor, CellStyle estilo) {
        Cell celda = fila.createCell(indice);
        celda.setCellValue(valor);
        celda.setCellStyle(estilo);
    }
    private void terminar(Sheet hoja, int columnas) { hoja.createFreezePane(0,MarcaReporteFinanciero.FILAS+1); for(int i=0;i<columnas;i++) hoja.setColumnWidth(i,(i==4?32:20)*256); }
}
