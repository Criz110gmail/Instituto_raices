package escuela.admin.service;

import escuela.archivo.dto.ArchivoDescarga;
import escuela.archivo.entity.Archivo;
import escuela.archivo.entity.EstadoArchivo;
import escuela.archivo.repository.ArchivoRepository;
import escuela.archivo.storage.AlmacenamientoArchivo;
import escuela.common.exception.ReglaNegocioException;
import escuela.institucion.entity.Institucion;
import escuela.institucion.repository.InstitucionRepository;
import escuela.tutor.dto.response.IdentificacionTutorResponse;
import escuela.tutor.dto.response.PortalTutorCuentaResponse;
import escuela.tutor.dto.response.TutorResponse;
import escuela.tutor.service.AccesoPortalTutorService;
import escuela.tutor.service.IdentificacionTutorService;
import escuela.tutor.service.TutorService;
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
import net.sf.jasperreports.engine.design.JRDesignGroup;
import net.sf.jasperreports.engine.design.JRDesignImage;
import net.sf.jasperreports.engine.design.JRDesignParameter;
import net.sf.jasperreports.engine.design.JRDesignSection;
import net.sf.jasperreports.engine.design.JRDesignTextElement;
import net.sf.jasperreports.engine.design.JRDesignTextField;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.type.HorizontalTextAlignEnum;
import net.sf.jasperreports.engine.type.ModeEnum;
import net.sf.jasperreports.engine.type.OnErrorTypeEnum;
import net.sf.jasperreports.engine.type.PositionTypeEnum;
import net.sf.jasperreports.engine.type.ScaleImageEnum;
import net.sf.jasperreports.engine.type.SplitTypeEnum;
import net.sf.jasperreports.engine.type.TextAdjustEnum;
import net.sf.jasperreports.engine.type.VerticalTextAlignEnum;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Image;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class JasperFichaTutorService {

    private static final Color AZUL = new Color(15, 43, 73);
    private static final Color TURQUESA = new Color(17, 163, 164);
    private static final Color GRIS = new Color(86, 101, 114);
    private static final Color FONDO = new Color(241, 246, 248);
    private static final Color LINEA = new Color(218, 226, 232);
    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final TutorService tutores;
    private final IdentificacionTutorService identificaciones;
    private final AccesoPortalTutorService accesosPortal;
    private final InstitucionRepository instituciones;
    private final ArchivoRepository archivos;
    private final AlmacenamientoArchivo almacenamiento;
    private volatile JasperReport reporteCompilado;

    @Transactional(readOnly = true)
    public void exportar(Long tutorId, OutputStream salida) {
        TutorResponse tutor = tutores.obtener(tutorId);
        Institucion institucion = institucion(tutor.institucionId());
        IdentificacionTutorResponse identificacion = identificaciones.actual(tutorId);
        PortalTutorCuentaResponse portal = accesosPortal.obtener(tutorId).orElse(null);
        List<Map<String, ?>> filas = new ArrayList<>();
        seccion(filas, "Identificación oficial",
                fila("Documento vigente", identificacion == null ? "No registrada" : identificacion.tipoEtiqueta()),
                fila("Nombre del archivo", identificacion == null ? null : identificacion.nombreOriginal()),
                fila("Fecha de carga", identificacion == null ? null
                        : FECHA.format(identificacion.cargadaEn().atZone(zona(institucion)).toLocalDate())));
        seccion(filas, "Datos personales",
                fila("Nombre completo", nombre(tutor)),
                fila("Fecha de nacimiento", tutor.fechaNacimiento() == null ? null : FECHA.format(tutor.fechaNacimiento())),
                fila("Estado del expediente", tutor.activo() ? "Activo" : "Inactivo"));
        seccion(filas, "Contacto y domicilio",
                fila("Teléfono principal", tutor.telefonoPrincipal()),
                fila("Teléfono secundario", tutor.telefonoSecundario()),
                fila("Correo", tutor.email()),
                fila("Domicilio", domicilio(tutor)));
        seccion(filas, "Información laboral",
                fila("Ocupación", tutor.ocupacion()),
                fila("Lugar de trabajo", tutor.lugarTrabajo()),
                fila("Teléfono de trabajo", tutor.telefonoTrabajo()));
        seccion(filas, "Portal de familias",
                fila("Cuenta de acceso", portal == null ? "Sin cuenta" : portal.username()),
                fila("Correo de acceso", portal == null ? null : portal.email()),
                fila("Estado de la cuenta", portal == null ? "No configurada" : portal.estado().name()),
                fila("Contraseña", portal == null ? null
                        : (portal.credencialConfigurada() ? "Configurada" : "Pendiente de configurar")));
        exportar(salida, institucion, tutor, identificacion, filas);
    }

    private void exportar(OutputStream salida, Institucion institucion, TutorResponse tutor,
                          IdentificacionTutorResponse identificacion, List<Map<String, ?>> filas) {
        try {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("logo", imagenInstitucional(institucion));
            parametros.put("identificacion", imagenIdentificacion(tutor.id(), identificacion));
            parametros.put("institucion", institucion.getNombre());
            parametros.put("tutor", nombre(tutor));
            parametros.put("estado", tutor.activo() ? "EXPEDIENTE ACTIVO" : "EXPEDIENTE INACTIVO");
            parametros.put("identificacionResumen", identificacion == null
                    ? "SIN IDENTIFICACIÓN VIGENTE" : identificacion.tipoEtiqueta().toUpperCase());
            parametros.put("generado", "Generado por " + usuario() + " · "
                    + LocalDateTime.now(zona(institucion)).format(FECHA_HORA));
            JasperPrint impresion = JasperFillManager.fillReport(reporte(), parametros,
                    new JRMapCollectionDataSource(filas));
            JasperExportManager.exportReportToPdfStream(impresion, salida);
        } catch (JRException excepcion) {
            throw new IllegalStateException("No fue posible generar la ficha del tutor", excepcion);
        }
    }

    private JasperDesign diseno() throws JRException {
        JasperDesign d = new JasperDesign();
        d.setName("ficha_tutor");
        d.setPageWidth(595); d.setPageHeight(842); d.setLeftMargin(32); d.setRightMargin(32);
        d.setTopMargin(24); d.setBottomMargin(24); d.setColumnWidth(531);
        parametro(d, "logo", Image.class); parametro(d, "identificacion", Image.class);
        for (String nombre : List.of("institucion", "tutor", "estado", "identificacionResumen", "generado")) {
            parametro(d, nombre, String.class);
        }
        for (String nombre : List.of("seccion", "etiqueta", "valor")) {
            JRDesignField campo = new JRDesignField(); campo.setName(nombre);
            campo.setValueClass(String.class); d.addField(campo);
        }

        JRDesignBand titulo = new JRDesignBand(); titulo.setHeight(188);
        titulo.addElement(imagen(d, "$P{logo}", 0, 0, 78, 54));
        titulo.addElement(expresion("$P{institucion}", 90, 0, 315, 24, 14, true,
                AZUL, Color.WHITE, HorizontalTextAlignEnum.LEFT));
        titulo.addElement(expresion("\"DATOS PRIVADOS\"", 415, 4, 116, 25, 7, true,
                Color.WHITE, TURQUESA, HorizontalTextAlignEnum.CENTER));
        titulo.addElement(expresion("\"FICHA DEL TUTOR\"", 0, 69, 405, 30, 19, true,
                AZUL, Color.WHITE, HorizontalTextAlignEnum.LEFT));
        titulo.addElement(expresion("\"Identidad, contacto y acceso al portal de familias\"", 0, 99,
                405, 30, 8, false, GRIS, Color.WHITE, HorizontalTextAlignEnum.LEFT));
        titulo.addElement(expresion("$P{tutor}", 0, 139, 405, 24, 13, true,
                AZUL, FONDO, HorizontalTextAlignEnum.LEFT));
        titulo.addElement(expresion("$P{identificacionResumen} + \" · \" + $P{estado}", 0, 163,
                405, 18, 8, false, GRIS, FONDO, HorizontalTextAlignEnum.LEFT));
        titulo.addElement(imagen(d, "$P{identificacion}", 431, 69, 100, 112));
        d.setTitle(titulo);

        JRDesignGroup seccion = new JRDesignGroup(); seccion.setName("seccion");
        seccion.setExpression(new JRDesignExpression("$F{seccion}"));
        JRDesignBand encabezado = new JRDesignBand(); encabezado.setHeight(34);
        encabezado.addElement(expresion("$F{seccion}.toUpperCase()", 0, 7, 531, 24, 9, true,
                Color.WHITE, AZUL, HorizontalTextAlignEnum.LEFT));
        ((JRDesignSection) seccion.getGroupHeaderSection()).addBand(encabezado); d.addGroup(seccion);

        JRDesignBand detalle = new JRDesignBand(); detalle.setHeight(36);
        detalle.setSplitType(SplitTypeEnum.IMMEDIATE);
        JRDesignTextField etiqueta = expresion("$F{etiqueta}", 0, 0, 155, 34, 8, true,
                TURQUESA, Color.WHITE, HorizontalTextAlignEnum.LEFT);
        etiqueta.setPositionType(PositionTypeEnum.FLOAT);
        JRDesignTextField valor = expresion("$F{valor}", 155, 0, 376, 34, 9, false,
                AZUL, Color.WHITE, HorizontalTextAlignEnum.LEFT);
        valor.setPositionType(PositionTypeEnum.FLOAT); valor.setTextAdjust(TextAdjustEnum.STRETCH_HEIGHT);
        detalle.addElement(etiqueta); detalle.addElement(valor);
        ((JRDesignSection) d.getDetailSection()).addBand(detalle);

        JRDesignBand pie = new JRDesignBand(); pie.setHeight(27);
        pie.addElement(expresion("$P{generado}", 0, 7, 405, 16, 7, false,
                GRIS, Color.WHITE, HorizontalTextAlignEnum.LEFT));
        pie.addElement(expresion("\"Página \" + $V{PAGE_NUMBER}", 415, 7, 116, 16, 7, false,
                GRIS, Color.WHITE, HorizontalTextAlignEnum.RIGHT));
        d.setPageFooter(pie);
        return d;
    }

    private JasperReport reporte() throws JRException {
        JasperReport disponible = reporteCompilado;
        if (disponible == null) {
            synchronized (this) {
                disponible = reporteCompilado;
                if (disponible == null) {
                    disponible = JasperCompileManager.compileReport(diseno());
                    reporteCompilado = disponible;
                }
            }
        }
        return disponible;
    }

    private JRDesignImage imagen(JasperDesign d, String expresion, int x, int y, int ancho, int alto) {
        JRDesignImage imagen = new JRDesignImage(d);
        imagen.setX(x); imagen.setY(y); imagen.setWidth(ancho); imagen.setHeight(alto);
        imagen.setScaleImage(ScaleImageEnum.RETAIN_SHAPE); imagen.setOnErrorType(OnErrorTypeEnum.BLANK);
        imagen.setExpression(new JRDesignExpression(expresion));
        return imagen;
    }

    private void parametro(JasperDesign d, String nombre, Class<?> tipo) throws JRException {
        JRDesignParameter parametro = new JRDesignParameter(); parametro.setName(nombre);
        parametro.setValueClass(tipo); d.addParameter(parametro);
    }

    private JRDesignTextField expresion(String valor, int x, int y, int ancho, int alto,
                                        int tamano, boolean negrita, Color tinta, Color fondo,
                                        HorizontalTextAlignEnum alineacion) {
        JRDesignTextField campo = new JRDesignTextField(); campo.setExpression(new JRDesignExpression(valor));
        configurar(campo, x, y, ancho, alto, tamano, negrita, tinta, fondo, alineacion);
        return campo;
    }

    private void configurar(JRDesignTextElement campo, int x, int y, int ancho, int alto,
                            int tamano, boolean negrita, Color tinta, Color fondo,
                            HorizontalTextAlignEnum alineacion) {
        campo.setX(x); campo.setY(y); campo.setWidth(ancho); campo.setHeight(alto);
        campo.setFontName("SansSerif"); campo.setFontSize((float) tamano); campo.setBold(negrita);
        campo.setForecolor(tinta); campo.setBackcolor(fondo); campo.setMode(ModeEnum.OPAQUE);
        campo.setHorizontalTextAlign(alineacion); campo.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
        campo.getLineBox().setLeftPadding(6); campo.getLineBox().setRightPadding(6);
        campo.getLineBox().setTopPadding(3); campo.getLineBox().setBottomPadding(3);
        campo.getLineBox().getBottomPen().setLineColor(LINEA);
        campo.getLineBox().getBottomPen().setLineWidth(.35f);
    }

    @SafeVarargs
    private final void seccion(List<Map<String, ?>> destino, String nombre,
                               Map.Entry<String, String>... filas) {
        for (Map.Entry<String, String> fila : filas) {
            destino.add(Map.of("seccion", nombre, "etiqueta", fila.getKey(), "valor", texto(fila.getValue())));
        }
    }

    private Map.Entry<String, String> fila(String etiqueta, String valor) {
        return Map.entry(etiqueta, valor == null ? "" : valor);
    }

    private Institucion institucion(Long id) {
        return instituciones.findById(id)
                .orElseThrow(() -> new ReglaNegocioException("La institución del tutor ya no está disponible"));
    }

    private Image imagenInstitucional(Institucion institucion) {
        Image logo = imagenArchivo(institucion.getLogoArchivoId(), institucion.getId());
        if (logo != null) return logo;
        try (InputStream entrada = new ClassPathResource("reports/assets/logo-institucion-ejemplo.png").getInputStream()) {
            return ImageIO.read(entrada);
        } catch (Exception excepcion) {
            throw new IllegalStateException("No fue posible cargar el logotipo institucional", excepcion);
        }
    }

    private Image imagenArchivo(Long archivoId, Long institucionId) {
        if (archivoId == null) return null;
        Archivo archivo = archivos.findById(archivoId).orElse(null);
        if (archivo == null || archivo.getEstado() != EstadoArchivo.DISPONIBLE
                || !archivo.getInstitucion().getId().equals(institucionId)
                || archivo.getTipoMime() == null || !archivo.getTipoMime().startsWith("image/")) return null;
        try (InputStream entrada = almacenamiento.abrir(archivo.getClaveAlmacenamiento()).getInputStream()) {
            return ImageIO.read(entrada);
        } catch (Exception ignorada) {
            return null;
        }
    }

    private Image imagenIdentificacion(Long tutorId, IdentificacionTutorResponse identificacion) {
        if (identificacion == null || identificacion.tipoMime() == null
                || !identificacion.tipoMime().startsWith("image/")) return null;
        try {
            ArchivoDescarga descarga = identificaciones.descargar(tutorId, identificacion.id());
            try (InputStream entrada = descarga.recurso().getInputStream()) {
                return ImageIO.read(entrada);
            }
        } catch (Exception ignorada) {
            return null;
        }
    }

    private ZoneId zona(Institucion institucion) {
        try { return ZoneId.of(institucion.getZonaHoraria()); }
        catch (RuntimeException excepcion) { return ZoneId.of("America/Mexico_City"); }
    }

    private String nombre(TutorResponse tutor) {
        return Stream.of(tutor.nombres(), tutor.primerApellido(), tutor.segundoApellido())
                .filter(valor -> valor != null && !valor.isBlank())
                .reduce((primero, segundo) -> primero + " " + segundo).orElse("Tutor sin nombre");
    }

    private String domicilio(TutorResponse tutor) {
        String numero = Stream.of(tutor.numeroExterior(), tutor.numeroInterior())
                .filter(valor -> valor != null && !valor.isBlank())
                .reduce((primero, segundo) -> primero + " Int. " + segundo).orElse("");
        return Stream.of(tutor.calle(), numero, tutor.colonia(), tutor.ciudad(), tutor.estado(),
                        tutor.codigoPostal(), tutor.pais())
                .filter(valor -> valor != null && !valor.isBlank())
                .reduce((primero, segundo) -> primero + ", " + segundo).orElse(null);
    }

    private String texto(String valor) {
        return valor == null || valor.isBlank() ? "No registrado" : valor;
    }

    private String usuario() {
        var autenticacion = SecurityContextHolder.getContext().getAuthentication();
        return autenticacion == null ? "sistema" : autenticacion.getName();
    }
}
