package escuela.admin.service;

import escuela.admin.dto.*;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class PdfEstadoCuentaCuentaService {
    private static final PDType1Font NORMAL = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
    private static final PDType1Font NEGRITA = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
    private static final Color TINTA = new Color(11, 25, 48);
    private static final Color VERDE = new Color(11, 119, 93);
    private static final Color GRIS = new Color(91, 110, 130);
    private final EstadoCuentaCuentaService consulta;

    public void exportar(FiltroEstadoCuentaCuenta original, OutputStream salida) throws IOException {
        FiltroEstadoCuentaCuenta filtro = consulta.normalizar(original);
        ResultadoEstadoCuentaCuenta inicial = consulta.consultar(filtro.conPagina(0, 100));
        if (inicial.cuenta() == null) throw new IllegalArgumentException("Selecciona una cuenta financiera");
        try (PDDocument documento = new PDDocument()) {
            int pagina = 0, numeroHoja = 0;
            ResultadoEstadoCuentaCuenta bloque = inicial;
            PDPage hoja = nuevaHoja(documento);
            numeroHoja++;
            float y = encabezado(documento, hoja, filtro, inicial, numeroHoja);
            do {
                for (MovimientoFinancieroFila m : bloque.movimientos().getContent()) {
                    if (y < 75) {
                        hoja = nuevaHoja(documento); numeroHoja++;
                        y = encabezado(documento, hoja, filtro, inicial, numeroHoja);
                    }
                    fila(documento, hoja, m, y);
                    y -= 25;
                }
                pagina++;
                if (pagina < bloque.movimientos().getTotalPages())
                    bloque = consulta.consultar(filtro.conPagina(pagina, 100));
            } while (pagina < bloque.movimientos().getTotalPages());
            if (inicial.movimientos().isEmpty()) texto(documento, hoja, "Sin movimientos en el periodo.", 40, y - 10, 11, NORMAL, GRIS);
            documento.save(salida);
        }
    }

    private PDPage nuevaHoja(PDDocument documento) {
        PDPage hoja = new PDPage(PDRectangle.A4);
        documento.addPage(hoja);
        return hoja;
    }

    private float encabezado(PDDocument doc, PDPage hoja, FiltroEstadoCuentaCuenta filtro,
                             ResultadoEstadoCuentaCuenta resultado, int numero) throws IOException {
        float alto = hoja.getMediaBox().getHeight();
        try (PDPageContentStream lienzo = new PDPageContentStream(doc, hoja)) {
            lienzo.setNonStrokingColor(TINTA);
            lienzo.addRect(0, alto - 115, hoja.getMediaBox().getWidth(), 115);
            lienzo.fill();
        }
        texto(doc, hoja, "NEXO ESCOLAR / FINANZAS", 40, alto - 40, 10, NEGRITA, new Color(54, 214, 195));
        texto(doc, hoja, "Estado de cuenta interno", 40, alto - 68, 19, NEGRITA, Color.WHITE);
        texto(doc, hoja, "Pagina " + numero + "  |  " + filtro.desde() + " al " + filtro.hasta(), 40, alto - 93, 9, NORMAL, Color.WHITE);
        texto(doc, hoja, "Cuenta: " + recortar(resultado.cuenta().etiqueta(), 75), 40, alto - 142, 11, NEGRITA, TINTA);
        texto(doc, hoja, "Movimientos: " + resultado.resumen().movimientos() + "  |  Moneda: " + resultado.cuenta().moneda(), 40, alto - 160, 9, NORMAL, GRIS);
        String moneda = resultado.cuenta().moneda();
        texto(doc, hoja, "Apertura  " + dinero(resultado.resumen().saldoApertura()) + " " + moneda,
                40, alto - 190, 10, NEGRITA, TINTA);
        texto(doc, hoja, "Ingresos  " + dinero(ingresos(resultado.resumen())) + " " + moneda,
                40, alto - 207, 10, NEGRITA, VERDE);
        texto(doc, hoja, "Egresos  " + dinero(egresos(resultado.resumen())) + " " + moneda,
                310, alto - 190, 10, NEGRITA, TINTA);
        texto(doc, hoja, "Cierre  " + dinero(resultado.resumen().saldoCierre()) + " " + moneda,
                310, alto - 207, 10, NEGRITA, TINTA);
        if (resultado.aperturaRegistradaEnPeriodo().signum() != 0)
            texto(doc, hoja, "Saldo inicial registrado en el periodo: "
                    + dinero(resultado.aperturaRegistradaEnPeriodo()) + " " + moneda,
                    40, alto - 225, 8, NORMAL, GRIS);
        texto(doc, hoja, "FECHA / FOLIO", 40, alto - 244, 8, NEGRITA, GRIS);
        texto(doc, hoja, "CONCEPTO", 173, alto - 244, 8, NEGRITA, GRIS);
        texto(doc, hoja, "INGRESO", 422, alto - 244, 8, NEGRITA, GRIS);
        texto(doc, hoja, "EGRESO", 507, alto - 244, 8, NEGRITA, GRIS);
        texto(doc, hoja, "Generado con registros internos; no equivale al estado de cuenta del banco.",
                40, 42, 8, NORMAL, GRIS);
        return alto - 263;
    }

    private void fila(PDDocument doc, PDPage hoja, MovimientoFinancieroFila m, float y) throws IOException {
        texto(doc, hoja, recortar(m.fecha(), 21), 40, y, 8, NORMAL, TINTA);
        texto(doc, hoja, recortar(m.folioPago().equals("—") ? m.referencia() : m.folioPago(), 23), 40, y - 10, 7, NORMAL, GRIS);
        texto(doc, hoja, recortar(m.concepto(), 48), 173, y, 8, NEGRITA, TINTA);
        texto(doc, hoja, recortar(m.clase() + " / " + m.plantel(), 48), 173, y - 10, 7, NORMAL, GRIS);
        if (m.direccion().equals("INGRESO")) texto(doc, hoja, dinero(m.monto()), 422, y, 8, NEGRITA, VERDE);
        else texto(doc, hoja, dinero(m.monto()), 507, y, 8, NEGRITA, TINTA);
    }

    private void texto(PDDocument doc, PDPage hoja, String valor, float x, float y,
                       int tam, PDType1Font fuente, Color color) throws IOException {
        try (PDPageContentStream lienzo = new PDPageContentStream(doc, hoja,
                PDPageContentStream.AppendMode.APPEND, true)) {
            lienzo.beginText(); lienzo.setNonStrokingColor(color); lienzo.setFont(fuente, tam);
            lienzo.newLineAtOffset(x, y);
            lienzo.showText(limpiar(valor)); lienzo.endText();
        }
    }

    private String limpiar(String valor) {
        if (valor == null) return "";
        return valor.replace('—', '-').replace('·', '-')
                .replaceAll("[^\\x20-\\x7EáéíóúÁÉÍÓÚñÑüÜ]", "?");
    }

    private String recortar(String valor, int maximo) {
        if (valor == null) return "";
        return valor.length() <= maximo ? valor : valor.substring(0, maximo - 3) + "...";
    }

    private String dinero(BigDecimal monto) {
        NumberFormat formato = NumberFormat.getNumberInstance(new Locale("es", "MX"));
        formato.setMinimumFractionDigits(2);
        formato.setMaximumFractionDigits(2);
        return formato.format(monto);
    }

    private BigDecimal ingresos(ResumenTesoreria resumen) {
        return resumen.ingresosOperativos().add(resumen.traspasosEntrada());
    }

    private BigDecimal egresos(ResumenTesoreria resumen) {
        return resumen.egresosOperativos().add(resumen.traspasosSalida());
    }
}
