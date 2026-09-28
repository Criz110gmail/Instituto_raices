package escuela.admin.service;

import escuela.admin.dto.BoletaCalificacionFila;
import escuela.admin.dto.BoletaDetalle;
import escuela.admin.dto.FiltroBoleta;
import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.design.*;
import net.sf.jasperreports.engine.type.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.IntFunction;

@Service
@RequiredArgsConstructor
public class JasperBoletaService {
    private static final int BLOQUE = 100;
    private static final Color AZUL = new Color(18, 45, 72);
    private static final Color AQUA = new Color(54, 214, 195);
    private static final Color GRIS = new Color(79, 94, 107);
    private static final Color FONDO = new Color(240, 245, 249);
    private final BoletaConsultaService consulta;

    public void individual(Long inscripcionId, OutputStream salida) {
        exportar(consulta.detalle(inscripcionId), salida);
    }

    public void exportar(BoletaDetalle detalle, OutputStream salida) {
        exportar(new BoletaDataSource(new PageImpl<>(List.of(detalle)), pagina -> Page.empty()), salida);
    }

    public void colectivo(FiltroBoleta original, OutputStream salida) {
        FiltroBoleta filtro = consulta.exigirExportacion(original);
        Page<BoletaDetalle> primera = consulta.bloqueDetallado(filtro.conPagina(0, BLOQUE));
        exportar(new BoletaDataSource(primera,
                pagina -> consulta.bloqueDetallado(filtro.conPagina(pagina, BLOQUE))), salida);
    }

    private void exportar(JRDataSource datos, OutputStream salida) {
        try {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("generado", "Generado por " + usuario() + " · "
                    + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            JasperReport reporte = JasperCompileManager.compileReport(diseno());
            JasperPrint impresion = JasperFillManager.fillReport(reporte, parametros, datos);
            JasperExportManager.exportReportToPdfStream(impresion, salida);
        } catch (JRException e) {
            throw new IllegalStateException("No fue posible generar la boleta PDF", e);
        }
    }

    private JasperDesign diseno() throws JRException {
        JasperDesign d = new JasperDesign();
        d.setName("boleta_academica_" + UUID.randomUUID());
        d.setPageWidth(595); d.setPageHeight(842); d.setLeftMargin(28); d.setRightMargin(28);
        d.setTopMargin(24); d.setBottomMargin(24); d.setColumnWidth(539);
        parametro(d, "generado");
        for (String campo : List.of("inscripcionId", "institucion", "alumno", "matricula", "numeroInscripcion",
                "plantel", "ciclo", "grado", "grupo", "periodo", "materia", "resultado", "escala", "observaciones")) {
            JRDesignField f = new JRDesignField(); f.setName(campo); f.setValueClass(String.class); d.addField(f);
        }

        JRDesignGroup estudiante = new JRDesignGroup();
        estudiante.setName("estudiante");
        estudiante.setExpression(new JRDesignExpression("$F{inscripcionId}"));
        estudiante.setStartNewPage(true);
        JRDesignBand cabecera = new JRDesignBand(); cabecera.setHeight(139);
        cabecera.addElement(expresion("$F{institucion}", 0, 0, 539, 20, 12, true, Color.WHITE, AZUL, HorizontalTextAlignEnum.LEFT));
        cabecera.addElement(constante("BOLETA DE CALIFICACIONES", 0, 20, 539, 30, 18, true, Color.WHITE, AZUL, HorizontalTextAlignEnum.LEFT));
        cabecera.addElement(expresion("$F{alumno}", 0, 58, 330, 22, 13, true, AZUL, Color.WHITE, HorizontalTextAlignEnum.LEFT));
        cabecera.addElement(expresion("\"Matrícula: \" + $F{matricula}", 339, 58, 200, 22, 9, false, GRIS, Color.WHITE, HorizontalTextAlignEnum.RIGHT));
        cabecera.addElement(expresion("$F{plantel} + \" · \" + $F{ciclo}", 0, 81, 330, 18, 9, false, GRIS, Color.WHITE, HorizontalTextAlignEnum.LEFT));
        cabecera.addElement(expresion("$F{grado} + \" · \" + $F{grupo}", 339, 81, 200, 18, 9, false, GRIS, Color.WHITE, HorizontalTextAlignEnum.RIGHT));
        cabecera.addElement(constante("Periodo", 0, 110, 88, 25, 8, true, Color.WHITE, AZUL, HorizontalTextAlignEnum.LEFT));
        cabecera.addElement(constante("Materia", 88, 110, 150, 25, 8, true, Color.WHITE, AZUL, HorizontalTextAlignEnum.LEFT));
        cabecera.addElement(constante("Resultado", 238, 110, 70, 25, 8, true, Color.WHITE, AZUL, HorizontalTextAlignEnum.CENTER));
        cabecera.addElement(constante("Escala", 308, 110, 117, 25, 8, true, Color.WHITE, AZUL, HorizontalTextAlignEnum.LEFT));
        cabecera.addElement(constante("Observaciones", 425, 110, 114, 25, 8, true, Color.WHITE, AZUL, HorizontalTextAlignEnum.LEFT));
        ((JRDesignSection) estudiante.getGroupHeaderSection()).addBand(cabecera);
        JRDesignBand separador = new JRDesignBand(); separador.setHeight(9);
        ((JRDesignSection) estudiante.getGroupFooterSection()).addBand(separador);
        d.addGroup(estudiante);

        JRDesignBand detalle = new JRDesignBand(); detalle.setHeight(30);
        detalle.addElement(expresion("$F{periodo}", 0, 0, 88, 30, 8, false, AZUL, Color.WHITE, HorizontalTextAlignEnum.LEFT));
        detalle.addElement(expresion("$F{materia}", 88, 0, 150, 30, 8, true, AZUL, Color.WHITE, HorizontalTextAlignEnum.LEFT));
        detalle.addElement(expresion("$F{resultado}", 238, 0, 70, 30, 10, true, AZUL, FONDO, HorizontalTextAlignEnum.CENTER));
        detalle.addElement(expresion("$F{escala}", 308, 0, 117, 30, 7, false, GRIS, Color.WHITE, HorizontalTextAlignEnum.LEFT));
        detalle.addElement(expresion("$F{observaciones}", 425, 0, 114, 30, 7, false, GRIS, Color.WHITE, HorizontalTextAlignEnum.LEFT));
        ((JRDesignSection) d.getDetailSection()).addBand(detalle);

        JRDesignBand pie = new JRDesignBand(); pie.setHeight(25);
        pie.addElement(expresion("$P{generado}", 0, 6, 410, 15, 7, false, GRIS, Color.WHITE, HorizontalTextAlignEnum.LEFT));
        pie.addElement(expresion("\"Página \" + $V{PAGE_NUMBER}", 420, 6, 119, 15, 7, false, GRIS, Color.WHITE, HorizontalTextAlignEnum.RIGHT));
        d.setPageFooter(pie);
        return d;
    }

    private void parametro(JasperDesign d, String nombre) throws JRException {
        JRDesignParameter p = new JRDesignParameter(); p.setName(nombre); p.setValueClass(String.class); d.addParameter(p);
    }

    private JRDesignTextField expresion(String valor, int x, int y, int ancho, int alto, int tamano,
                                         boolean negrita, Color tinta, Color fondo, HorizontalTextAlignEnum alineacion) {
        JRDesignTextField campo = new JRDesignTextField(); campo.setExpression(new JRDesignExpression(valor));
        configurar(campo, x, y, ancho, alto, tamano, negrita, tinta, fondo, alineacion); return campo;
    }

    private JRDesignStaticText constante(String valor, int x, int y, int ancho, int alto, int tamano,
                                          boolean negrita, Color tinta, Color fondo, HorizontalTextAlignEnum alineacion) {
        JRDesignStaticText campo = new JRDesignStaticText(); campo.setText(valor);
        configurar(campo, x, y, ancho, alto, tamano, negrita, tinta, fondo, alineacion); return campo;
    }

    private void configurar(JRDesignTextElement campo, int x, int y, int ancho, int alto, int tamano,
                            boolean negrita, Color tinta, Color fondo, HorizontalTextAlignEnum alineacion) {
        campo.setX(x); campo.setY(y); campo.setWidth(ancho); campo.setHeight(alto); campo.setFontName("SansSerif");
        campo.setFontSize((float) tamano); campo.setBold(negrita); campo.setForecolor(tinta); campo.setBackcolor(fondo);
        campo.setMode(ModeEnum.OPAQUE); campo.setHorizontalTextAlign(alineacion);
        campo.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE); campo.getLineBox().setLeftPadding(5);
        campo.getLineBox().setRightPadding(5); campo.getLineBox().getBottomPen().setLineColor(new Color(220, 226, 231));
        campo.getLineBox().getBottomPen().setLineWidth(.35f);
    }

    private String usuario() {
        var autenticacion = SecurityContextHolder.getContext().getAuthentication();
        return autenticacion == null ? "sistema" : autenticacion.getName();
    }

    private static final class BoletaDataSource implements JRDataSource {
        private Page<BoletaDetalle> pagina;
        private final IntFunction<Page<BoletaDetalle>> cargador;
        private int numeroPagina;
        private int indiceBoleta;
        private int indiceCalificacion = -1;
        private Map<String, String> actual = Map.of();

        private BoletaDataSource(Page<BoletaDetalle> primera, IntFunction<Page<BoletaDetalle>> cargador) {
            this.pagina = primera; this.cargador = cargador;
        }

        @Override public boolean next() {
            indiceCalificacion++;
            while (true) {
                if (indiceBoleta < pagina.getContent().size()) {
                    BoletaDetalle boleta = pagina.getContent().get(indiceBoleta);
                    if (indiceCalificacion < boleta.calificaciones().size()) {
                        actual = mapear(boleta, boleta.calificaciones().get(indiceCalificacion));
                        return true;
                    }
                    indiceBoleta++; indiceCalificacion = 0;
                    continue;
                }
                if (numeroPagina + 1 >= pagina.getTotalPages()) return false;
                pagina = cargador.apply(++numeroPagina); indiceBoleta = 0; indiceCalificacion = 0;
            }
        }

        @Override public Object getFieldValue(JRField field) { return actual.getOrDefault(field.getName(), ""); }

        private Map<String, String> mapear(BoletaDetalle b, BoletaCalificacionFila c) {
            Map<String, String> m = new HashMap<>();
            m.put("inscripcionId", b.inscripcionId().toString()); m.put("institucion", b.institucion());
            m.put("alumno", b.alumno()); m.put("matricula", b.matricula()); m.put("numeroInscripcion", b.numeroInscripcion());
            m.put("plantel", b.plantel()); m.put("ciclo", b.ciclo()); m.put("grado", b.grado()); m.put("grupo", b.grupo());
            m.put("periodo", c.periodo()); m.put("materia", c.materia()); m.put("resultado", c.resultado());
            m.put("escala", c.escala()); m.put("observaciones", c.observaciones()); return m;
        }
    }
}
