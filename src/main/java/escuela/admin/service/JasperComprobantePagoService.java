package escuela.admin.service;

import escuela.archivo.entity.EstadoArchivo;
import escuela.archivo.repository.ArchivoRepository;
import escuela.archivo.storage.AlmacenamientoArchivo;
import escuela.common.exception.ReglaNegocioException;
import escuela.finanzas.dto.response.AplicacionPagoResponse;
import escuela.finanzas.dto.response.PagoResponse;
import escuela.finanzas.entity.EstadoPago;
import escuela.institucion.entity.Institucion;
import escuela.institucion.repository.InstitucionRepository;
import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import net.sf.jasperreports.engine.design.JRDesignBand;
import net.sf.jasperreports.engine.design.JRDesignExpression;
import net.sf.jasperreports.engine.design.JRDesignField;
import net.sf.jasperreports.engine.design.JRDesignImage;
import net.sf.jasperreports.engine.design.JRDesignParameter;
import net.sf.jasperreports.engine.design.JRDesignSection;
import net.sf.jasperreports.engine.design.JRDesignStaticText;
import net.sf.jasperreports.engine.design.JRDesignTextElement;
import net.sf.jasperreports.engine.design.JRDesignTextField;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.type.HorizontalTextAlignEnum;
import net.sf.jasperreports.engine.type.ModeEnum;
import net.sf.jasperreports.engine.type.ScaleImageEnum;
import net.sf.jasperreports.engine.type.VerticalTextAlignEnum;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Image;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class JasperComprobantePagoService {
    private static final Color AZUL = new Color(15, 43, 73);
    private static final Color TURQUESA = new Color(17, 163, 164);
    private static final Color DORADO = new Color(222, 166, 45);
    private static final Color GRIS = new Color(86, 101, 114);
    private static final Color FONDO = new Color(241, 246, 248);
    private static final Color LINEA = new Color(218, 226, 232);
    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern(
            "dd 'de' MMMM 'de' yyyy · HH:mm", new Locale("es", "MX"));

    private final InstitucionRepository instituciones;
    private final ArchivoRepository archivos;
    private final AlmacenamientoArchivo almacenamiento;

    @Transactional(readOnly = true)
    public void exportar(PagoResponse pago, OutputStream salida) {
        if (pago.estado() != EstadoPago.VALIDADO) {
            throw new ReglaNegocioException("El comprobante oficial sólo está disponible para pagos validados");
        }
        Institucion institucion = instituciones.findById(pago.institucionId())
                .orElseThrow(() -> new ReglaNegocioException("La institución del pago ya no está disponible"));
        ZoneId zona = ZoneId.of(institucion.getZonaHoraria());
        try {
            Map<String, Object> parametros = parametros(pago, institucion, zona);
            JasperReport reporte = JasperCompileManager.compileReport(diseno());
            JasperPrint impresion = JasperFillManager.fillReport(reporte, parametros,
                    new JRMapCollectionDataSource(filas(pago)));
            JasperExportManager.exportReportToPdfStream(impresion, salida);
        } catch (JRException excepcion) {
            throw new IllegalStateException("No fue posible generar el comprobante de pago", excepcion);
        }
    }

    private Map<String, Object> parametros(PagoResponse pago, Institucion institucion, ZoneId zona) {
        Map<String, Object> p = new HashMap<>();
        p.put("logo", logo(institucion));
        p.put("institucion", institucion.getNombre());
        p.put("identidadFiscal", identidadFiscal(institucion));
        p.put("domicilio", domicilio(institucion));
        p.put("folio", pago.folio());
        p.put("identificador", "PAGO-" + pago.id());
        p.put("fechaPago", FECHA.format(pago.fechaPago().atZone(zona)));
        p.put("fechaValidacion", pago.validadoEn() == null ? "Sin fecha" : FECHA.format(pago.validadoEn().atZone(zona)));
        p.put("plantel", pago.plantelRegistroNombre());
        p.put("tutor", pago.tutorNombre());
        p.put("pagador", texto(pago.nombrePagador(), pago.tutorNombre()));
        p.put("metodo", pago.metodo().name().equals("EFECTIVO") ? "Efectivo" : "Transferencia");
        p.put("referencia", texto(pago.referencia(), "Sin referencia declarada"));
        p.put("cuenta", texto(pago.cuentaDestinoNombre(), "Sin cuenta identificada"));
        p.put("validadoPor", texto(pago.validadoPor(), "Sistema"));
        p.put("monto", moneda(pago.monto(), pago.moneda()));
        p.put("aplicado", moneda(pago.montoAplicado(), pago.moneda()));
        p.put("devuelto", moneda(pago.montoDevuelto(), pago.moneda()));
        p.put("disponible", moneda(pago.montoDisponible(), pago.moneda()));
        p.put("movimiento", pago.movimiento() == null ? "Sin referencia" :
                "Movimiento #" + pago.movimiento().id() + " · Secuencia " + pago.movimiento().secuenciaCuenta());
        p.put("generado", "Consulta generada el " + FECHA.format(LocalDateTime.now(zona)));
        return p;
    }

    private List<Map<String, ?>> filas(PagoResponse pago) {
        List<Map<String, ?>> filas = new ArrayList<>();
        if (pago.aplicaciones().isEmpty()) {
            filas.add(Map.of("alumno", "Saldo disponible", "matricula", "—",
                    "concepto", "Importe sin aplicar a cargos", "monto", moneda(pago.montoDisponible(), pago.moneda())));
            return filas;
        }
        for (AplicacionPagoResponse a : pago.aplicaciones()) {
            filas.add(Map.of("alumno", a.alumno(), "matricula", texto(a.matricula(), "—"),
                    "concepto", recortar(a.concepto() + " · " + a.descripcionCargo(), 78),
                    "monto", moneda(a.monto(), a.moneda())));
        }
        return filas;
    }

    private JasperDesign diseno() throws JRException {
        JasperDesign d = new JasperDesign();
        d.setName("comprobante_pago_" + UUID.randomUUID());
        d.setPageWidth(595); d.setPageHeight(842); d.setLeftMargin(32); d.setRightMargin(32);
        d.setTopMargin(24); d.setBottomMargin(24); d.setColumnWidth(531);
        parametro(d, "logo", Image.class);
        for (String nombre : List.of("institucion", "identidadFiscal", "domicilio", "folio", "identificador",
                "fechaPago", "fechaValidacion", "plantel", "tutor", "pagador", "metodo", "referencia",
                "cuenta", "validadoPor", "monto", "aplicado", "devuelto", "disponible", "movimiento", "generado")) {
            parametro(d, nombre, String.class);
        }
        for (String nombre : List.of("alumno", "matricula", "concepto", "monto")) {
            JRDesignField campo = new JRDesignField(); campo.setName(nombre); campo.setValueClass(String.class); d.addField(campo);
        }

        JRDesignBand titulo = new JRDesignBand(); titulo.setHeight(308);
        JRDesignImage logo = new JRDesignImage(d); logo.setX(0); logo.setY(0); logo.setWidth(82); logo.setHeight(58);
        logo.setScaleImage(ScaleImageEnum.RETAIN_SHAPE); logo.setExpression(new JRDesignExpression("$P{logo}"));
        titulo.addElement(logo);
        titulo.addElement(expresion("$P{institucion}", 92, 0, 320, 22, 14, true, AZUL, Color.WHITE, HorizontalTextAlignEnum.LEFT));
        titulo.addElement(expresion("$P{identidadFiscal}", 92, 23, 320, 16, 8, false, GRIS, Color.WHITE, HorizontalTextAlignEnum.LEFT));
        titulo.addElement(expresion("$P{domicilio}", 92, 40, 320, 18, 7, false, GRIS, Color.WHITE, HorizontalTextAlignEnum.LEFT));
        titulo.addElement(constante("PAGO VALIDADO", 423, 4, 108, 27, 9, true, Color.WHITE, TURQUESA, HorizontalTextAlignEnum.CENTER));
        titulo.addElement(constante("COMPROBANTE DE PAGO", 0, 76, 350, 30, 20, true, AZUL, Color.WHITE, HorizontalTextAlignEnum.LEFT));
        titulo.addElement(expresion("\"Folio \" + $P{folio}", 357, 76, 174, 19, 11, true, AZUL, Color.WHITE, HorizontalTextAlignEnum.RIGHT));
        titulo.addElement(expresion("$P{identificador}", 357, 96, 174, 14, 7, false, GRIS, Color.WHITE, HorizontalTextAlignEnum.RIGHT));
        titulo.addElement(constante("IMPORTE RECIBIDO", 0, 122, 531, 18, 8, true, Color.WHITE, AZUL, HorizontalTextAlignEnum.CENTER));
        titulo.addElement(expresion("$P{monto}", 0, 140, 531, 48, 25, true, AZUL, FONDO, HorizontalTextAlignEnum.CENTER));
        etiquetaValor(titulo, "Fecha del pago", "$P{fechaPago}", 0, 203, 258);
        etiquetaValor(titulo, "Fecha de validación", "$P{fechaValidacion}", 273, 203, 258);
        etiquetaValor(titulo, "Tutor responsable", "$P{tutor}", 0, 250, 258);
        etiquetaValor(titulo, "Plantel receptor", "$P{plantel}", 273, 250, 258);
        d.setTitle(titulo);

        JRDesignBand cabecera = new JRDesignBand(); cabecera.setHeight(30);
        cabecera.addElement(constante("ALUMNO / MATRÍCULA", 0, 0, 164, 26, 8, true, Color.WHITE, AZUL, HorizontalTextAlignEnum.LEFT));
        cabecera.addElement(constante("CONCEPTO APLICADO", 164, 0, 252, 26, 8, true, Color.WHITE, AZUL, HorizontalTextAlignEnum.LEFT));
        cabecera.addElement(constante("IMPORTE", 416, 0, 115, 26, 8, true, Color.WHITE, AZUL, HorizontalTextAlignEnum.RIGHT));
        d.setColumnHeader(cabecera);

        JRDesignBand detalle = new JRDesignBand(); detalle.setHeight(42);
        detalle.addElement(expresion("$F{alumno} + \"\\n\" + $F{matricula}", 0, 0, 164, 38, 8, true, AZUL, Color.WHITE, HorizontalTextAlignEnum.LEFT));
        detalle.addElement(expresion("$F{concepto}", 164, 0, 252, 38, 8, false, GRIS, Color.WHITE, HorizontalTextAlignEnum.LEFT));
        detalle.addElement(expresion("$F{monto}", 416, 0, 115, 38, 9, true, AZUL, Color.WHITE, HorizontalTextAlignEnum.RIGHT));
        ((JRDesignSection) d.getDetailSection()).addBand(detalle);

        JRDesignBand resumen = new JRDesignBand(); resumen.setHeight(258);
        resumen.addElement(constante("RESUMEN DEL PAGO", 0, 10, 531, 22, 9, true, Color.WHITE, AZUL, HorizontalTextAlignEnum.LEFT));
        resumen.addElement(expresion("\"Aplicado: \" + $P{aplicado}", 0, 38, 173, 28, 8, true, AZUL, FONDO, HorizontalTextAlignEnum.CENTER));
        resumen.addElement(expresion("\"Devuelto: \" + $P{devuelto}", 179, 38, 173, 28, 8, true, AZUL, FONDO, HorizontalTextAlignEnum.CENTER));
        resumen.addElement(expresion("\"Disponible: \" + $P{disponible}", 358, 38, 173, 28, 8, true, AZUL, FONDO, HorizontalTextAlignEnum.CENTER));
        etiquetaValor(resumen, "Método y referencia", "$P{metodo} + \" · \" + $P{referencia}", 0, 80, 258);
        etiquetaValor(resumen, "Cuenta receptora", "$P{cuenta}", 273, 80, 258);
        etiquetaValor(resumen, "Pagador", "$P{pagador}", 0, 127, 258);
        etiquetaValor(resumen, "Validado por", "$P{validadoPor}", 273, 127, 258);
        resumen.addElement(expresion("$P{movimiento}", 0, 174, 531, 18, 8, true, AZUL, Color.WHITE, HorizontalTextAlignEnum.LEFT));
        resumen.addElement(constante("Este documento acredita el registro y validación del pago en Nexo Escolar. No sustituye un comprobante fiscal.",
                0, 201, 531, 31, 8, false, GRIS, FONDO, HorizontalTextAlignEnum.CENTER));
        resumen.addElement(expresion("$P{generado}", 0, 238, 531, 16, 7, false, GRIS, Color.WHITE, HorizontalTextAlignEnum.CENTER));
        d.setSummary(resumen);

        JRDesignBand pie = new JRDesignBand(); pie.setHeight(20);
        pie.addElement(constante("Documento privado · Conserva este folio para cualquier aclaración", 0, 2, 400, 15, 7, false, GRIS, Color.WHITE, HorizontalTextAlignEnum.LEFT));
        pie.addElement(expresion("\"Página \" + $V{PAGE_NUMBER}", 410, 2, 121, 15, 7, false, GRIS, Color.WHITE, HorizontalTextAlignEnum.RIGHT));
        d.setPageFooter(pie);
        return d;
    }

    private void etiquetaValor(JRDesignBand banda, String etiqueta, String expresion,
                               int x, int y, int ancho) {
        banda.addElement(constante(etiqueta.toUpperCase(Locale.ROOT), x, y, ancho, 15, 7, true,
                TURQUESA, Color.WHITE, HorizontalTextAlignEnum.LEFT));
        banda.addElement(expresion(expresion, x, y + 15, ancho, 25, 9, true,
                AZUL, Color.WHITE, HorizontalTextAlignEnum.LEFT));
    }

    private void parametro(JasperDesign d, String nombre, Class<?> tipo) throws JRException {
        JRDesignParameter p = new JRDesignParameter(); p.setName(nombre); p.setValueClass(tipo); d.addParameter(p);
    }

    private JRDesignTextField expresion(String valor, int x, int y, int ancho, int alto, int tamano,
                                         boolean negrita, Color tinta, Color fondo,
                                         HorizontalTextAlignEnum alineacion) {
        JRDesignTextField campo = new JRDesignTextField(); campo.setExpression(new JRDesignExpression(valor));
        configurar(campo, x, y, ancho, alto, tamano, negrita, tinta, fondo, alineacion); return campo;
    }

    private JRDesignStaticText constante(String valor, int x, int y, int ancho, int alto, int tamano,
                                          boolean negrita, Color tinta, Color fondo,
                                          HorizontalTextAlignEnum alineacion) {
        JRDesignStaticText campo = new JRDesignStaticText(); campo.setText(valor);
        configurar(campo, x, y, ancho, alto, tamano, negrita, tinta, fondo, alineacion); return campo;
    }

    private void configurar(JRDesignTextElement campo, int x, int y, int ancho, int alto, int tamano,
                            boolean negrita, Color tinta, Color fondo, HorizontalTextAlignEnum alineacion) {
        campo.setX(x); campo.setY(y); campo.setWidth(ancho); campo.setHeight(alto); campo.setFontName("SansSerif");
        campo.setFontSize((float) tamano); campo.setBold(negrita); campo.setForecolor(tinta); campo.setBackcolor(fondo);
        campo.setMode(ModeEnum.OPAQUE); campo.setHorizontalTextAlign(alineacion);
        campo.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE); campo.getLineBox().setLeftPadding(5);
        campo.getLineBox().setRightPadding(5); campo.getLineBox().getBottomPen().setLineColor(LINEA);
        campo.getLineBox().getBottomPen().setLineWidth(.35f);
    }

    private Image logo(Institucion institucion) {
        if (institucion.getLogoArchivoId() != null) {
            var archivo = archivos.findById(institucion.getLogoArchivoId()).orElse(null);
            if (archivo != null && archivo.getEstado() == EstadoArchivo.DISPONIBLE
                    && archivo.getInstitucion().getId().equals(institucion.getId())
                    && archivo.getTipoMime().startsWith("image/")) {
                try (InputStream entrada = almacenamiento.abrir(archivo.getClaveAlmacenamiento()).getInputStream()) {
                    Image imagen = ImageIO.read(entrada);
                    if (imagen != null) return imagen;
                } catch (Exception ignorada) {
                    // El recurso institucional es opcional; se utiliza el respaldo incluido.
                }
            }
        }
        try (InputStream entrada = new ClassPathResource(
                "reports/assets/logo-institucion-ejemplo.png").getInputStream()) {
            return ImageIO.read(entrada);
        } catch (Exception excepcion) {
            throw new IllegalStateException("No fue posible cargar el logotipo del comprobante", excepcion);
        }
    }

    private String identidadFiscal(Institucion i) {
        String nombre = texto(i.getRazonSocial(), i.getNombreComercial());
        if (nombre == null || nombre.isBlank()) nombre = i.getNombre();
        return i.getRfc() == null || i.getRfc().isBlank() ? nombre : nombre + " · RFC " + i.getRfc();
    }

    private String domicilio(Institucion i) {
        return Stream.of(i.getDomicilioFiscal(), i.getCiudad(), i.getEstado(), i.getCodigoPostal())
                .filter(v -> v != null && !v.isBlank()).reduce((a, b) -> a + ", " + b).orElse("Domicilio no registrado");
    }

    private String moneda(BigDecimal monto, String moneda) {
        BigDecimal valor = monto == null ? BigDecimal.ZERO : monto;
        return "$" + new DecimalFormat("#,##0.00").format(valor) + " " + moneda;
    }

    private String texto(String valor, String respaldo) {
        return valor == null || valor.isBlank() ? respaldo : valor;
    }

    private String recortar(String valor, int maximo) {
        if (valor == null) return "";
        return valor.length() <= maximo ? valor : valor.substring(0, maximo - 1) + "…";
    }
}
