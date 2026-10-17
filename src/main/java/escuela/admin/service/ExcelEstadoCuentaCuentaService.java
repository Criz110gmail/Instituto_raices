package escuela.admin.service;

import escuela.admin.dto.*;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class ExcelEstadoCuentaCuentaService {
    private final EstadoCuentaCuentaService consulta;

    public void exportar(FiltroEstadoCuentaCuenta original, OutputStream salida) throws IOException {
        FiltroEstadoCuentaCuenta filtro = consulta.normalizar(original);
        ResultadoEstadoCuentaCuenta inicial = consulta.consultar(filtro.conPagina(0, 100));
        if (inicial.cuenta() == null) throw new IllegalArgumentException("Selecciona una cuenta financiera");
        try (SXSSFWorkbook libro = new SXSSFWorkbook(200)) {
            libro.setCompressTempFiles(true);
            Sheet hoja = libro.createSheet("Estado de cuenta");
            MarcaReporteFinanciero.excel(libro, hoja, 10, "Estado de cuenta financiera");
            CellStyle titulo = libro.createCellStyle();
            CellStyle dinero = libro.createCellStyle();
            dinero.setDataFormat(libro.createDataFormat().getFormat("\"$\"#,##0.00"));
            titulo.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            titulo.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            Font fuente = libro.createFont(); fuente.setBold(true); fuente.setColor(IndexedColors.WHITE.getIndex());
            titulo.setFont(fuente);
            Row cabecera = hoja.createRow(MarcaReporteFinanciero.FILAS);
            String[] columnas = {"Fecha", "Secuencia", "Plantel", "Clase", "Concepto", "Referencia",
                    "Folio pago", "Ingreso", "Egreso", "Moneda"};
            for (int c = 0; c < columnas.length; c++) {
                Cell celda = cabecera.createCell(c); celda.setCellValue(columnas[c]); celda.setCellStyle(titulo);
                hoja.setColumnWidth(c, (c == 4 ? 34 : 20) * 256);
            }
            int fila = MarcaReporteFinanciero.FILAS + 1;
            ResultadoEstadoCuentaCuenta bloque = inicial;
            int pagina = 0;
            do {
                for (MovimientoFinancieroFila m : bloque.movimientos().getContent()) {
                    Row r = hoja.createRow(fila++);
                    r.createCell(0).setCellValue(m.fecha());
                    r.createCell(1).setCellValue(m.secuenciaCuenta());
                    r.createCell(2).setCellValue(m.plantel());
                    r.createCell(3).setCellValue(m.clase());
                    r.createCell(4).setCellValue(m.concepto());
                    r.createCell(5).setCellValue(m.referencia());
                    r.createCell(6).setCellValue(m.folioPago());
                    numero(r, 7, m.direccion().equals("INGRESO") ? m.monto() : BigDecimal.ZERO);
                    numero(r, 8, m.direccion().equals("EGRESO") ? m.monto() : BigDecimal.ZERO);
                    r.getCell(7).setCellStyle(dinero); r.getCell(8).setCellStyle(dinero);
                    r.createCell(9).setCellValue(m.moneda());
                }
                pagina++;
                if (pagina < bloque.movimientos().getTotalPages())
                    bloque = consulta.consultar(filtro.conPagina(pagina, 100));
            } while (pagina < bloque.movimientos().getTotalPages());
            if (fila == MarcaReporteFinanciero.FILAS + 1) hoja.createRow(MarcaReporteFinanciero.FILAS + 1).createCell(0)
                    .setCellValue("Sin movimientos registrados en este periodo.");
            hoja.createFreezePane(0, MarcaReporteFinanciero.FILAS + 1);
            Sheet resumen = libro.createSheet("Resumen");
            MarcaReporteFinanciero.excel(libro, resumen, 2, "Estado de cuenta financiera");
            linea(resumen, 0, "Estado de cuenta interno", inicial.cuenta().etiqueta());
            DateTimeFormatter fecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            linea(resumen, 1, "Periodo", filtro.desde().format(fecha) + " al " + filtro.hasta().format(fecha));
            linea(resumen, 2, "Moneda", inicial.cuenta().moneda());
            linea(resumen, 3, "Movimientos", Long.toString(inicial.resumen().movimientos()));
            importe(resumen, 4, "Saldo de apertura", inicial.resumen().saldoApertura());
            importe(resumen, 5, "Ingresos", inicial.resumen().ingresosOperativos().add(inicial.resumen().traspasosEntrada()));
            importe(resumen, 6, "Egresos", inicial.resumen().egresosOperativos().add(inicial.resumen().traspasosSalida()));
            importe(resumen, 7, "Saldo de cierre", inicial.resumen().saldoCierre());
            importe(resumen, 8, "Saldo inicial de cuenta registrado en el periodo", inicial.aperturaRegistradaEnPeriodo());
            linea(resumen, 10, "Origen", "Movimientos registrados en Nexo Escolar; no es un estado bancario.");
            resumen.setColumnWidth(0, 48 * 256); resumen.setColumnWidth(1, 72 * 256);
            for (int numero = 4; numero <= 8; numero++) resumen.getRow(numero + MarcaReporteFinanciero.FILAS).getCell(1).setCellStyle(dinero);
            libro.setSheetOrder("Resumen", 0);
            hoja.setSelected(false);
            resumen.setSelected(true);
            libro.setActiveSheet(0);
            libro.setSelectedTab(0);
            libro.write(salida); libro.dispose();
        }
    }

    private void numero(Row fila, int columna, BigDecimal valor) {
        fila.createCell(columna).setCellValue(valor.doubleValue());
    }
    private void linea(Sheet hoja, int numero, String etiqueta, String valor) {
        Row fila = hoja.createRow(numero + MarcaReporteFinanciero.FILAS); fila.createCell(0).setCellValue(etiqueta);
        fila.createCell(1).setCellValue(valor);
    }
    private void importe(Sheet hoja, int numero, String etiqueta, BigDecimal valor) {
        Row fila = hoja.createRow(numero + MarcaReporteFinanciero.FILAS); fila.createCell(0).setCellValue(etiqueta);
        numero(fila, 1, valor);
    }
}
