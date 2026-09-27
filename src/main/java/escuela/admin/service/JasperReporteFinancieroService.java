package escuela.admin.service;

import escuela.admin.dto.*;
import escuela.common.exception.ReglaNegocioException;
import escuela.common.support.FormatoMoneda;
import escuela.institucion.service.InstitucionService;
import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.design.*;
import net.sf.jasperreports.engine.type.*;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.OutputStream;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.function.IntFunction;

/**
 * Exportaciones financieras Jasper sin consultas SQL dentro del reporte. De esta
 * forma se conservan en Java el alcance, los permisos y las reglas del dominio.
 */
@Service
@RequiredArgsConstructor
public class JasperReporteFinancieroService {
    private static final int BLOQUE = 100;
    private static final Color AZUL = new Color(18, 45, 72);
    private static final Color AZUL_CLARO = new Color(226, 237, 245);
    private static final Color GRIS = new Color(79, 94, 107);
    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final ReporteFinancieroConsultaService consulta;
    private final EstadoCuentaCuentaService estadoCuentaCuenta;
    private final InstitucionService instituciones;

    public void tesoreria(FiltroReporteTesoreria original, OutputStream salida) {
        FiltroReporteTesoreria filtro = consulta.normalizar(original);
        if (filtro.plantelId() != null) {
            throw new ReglaNegocioException("La balanza requiere consultar cuentas completas; quita el filtro de plantel operativo");
        }
        ResultadoReporteTesoreria primero = consulta.tesoreria(filtro.conPagina(0, BLOQUE));
        ResumenTesoreria r = primero.resumen();
        List<Columna> columnas = List.of(
                new Columna("periodo", "Periodo", 62, HorizontalTextAlignEnum.LEFT),
                new Columna("cuenta", "Cuenta", 170, HorizontalTextAlignEnum.LEFT),
                new Columna("alcance", "Alcance", 100, HorizontalTextAlignEnum.LEFT),
                new Columna("movimientos", "Movs.", 42, HorizontalTextAlignEnum.RIGHT),
                new Columna("ingresos", "Cargos / ingresos", 94, HorizontalTextAlignEnum.RIGHT),
                new Columna("egresos", "Abonos / egresos", 94, HorizontalTextAlignEnum.RIGHT),
                new Columna("neto", "Variación neta", 88, HorizontalTextAlignEnum.RIGHT),
                new Columna("traspasos", "Traspasos netos", 88, HorizontalTextAlignEnum.RIGHT));
        PageDataSource<ReporteTesoreriaFila> datos = new PageDataSource<>(primero.pagina(),
                pagina -> consulta.tesoreria(filtro.conPagina(pagina, BLOQUE)).pagina(), fila -> Map.of(
                "periodo", fila.periodo().format(FECHA), "cuenta", fila.cuenta(),
                "alcance", fila.alcanceCuenta(), "movimientos", Long.toString(fila.movimientos()),
                "ingresos", moneda(fila.ingresosOperativos()), "egresos", moneda(fila.egresosOperativos()),
                "neto", moneda(fila.netoOperativo()),
                "traspasos", moneda(fila.traspasosEntrada().subtract(fila.traspasosSalida()))));
        exportar("Balanza de movimientos y saldos", parametros(filtro.institucionId(),
                "Del " + filtro.fechaDesde().format(FECHA) + " al " + filtro.fechaHasta().format(FECHA),
                "Saldo inicial", r.saldosDisponibles() ? moneda(r.saldoApertura()) : "No disponible",
                "Ingresos operativos", moneda(r.ingresosOperativos()), "Egresos operativos", moneda(r.egresosOperativos()),
                "Saldo final", r.saldosDisponibles() ? moneda(r.saldoCierre()) : "No disponible"), columnas, datos, salida);
    }

    public void estadoCuenta(FiltroEstadoCuentaCuenta original, OutputStream salida) {
        FiltroEstadoCuentaCuenta filtro = estadoCuentaCuenta.normalizar(original);
        if (filtro.cuentaId() == null) throw new ReglaNegocioException("Selecciona una cuenta financiera");
        ResultadoEstadoCuentaCuenta primero = estadoCuentaCuenta.consultar(filtro.conPagina(0, BLOQUE));
        ResumenTesoreria r = primero.resumen();
        List<Columna> columnas = List.of(
                new Columna("fecha", "Fecha", 62, HorizontalTextAlignEnum.LEFT),
                new Columna("folio", "Folio", 78, HorizontalTextAlignEnum.LEFT),
                new Columna("concepto", "Concepto / referencia", 220, HorizontalTextAlignEnum.LEFT),
                new Columna("tipo", "Movimiento", 90, HorizontalTextAlignEnum.LEFT),
                new Columna("ingreso", "Ingreso", 90, HorizontalTextAlignEnum.RIGHT),
                new Columna("egreso", "Egreso", 90, HorizontalTextAlignEnum.RIGHT),
                new Columna("saldo", "Saldo", 94, HorizontalTextAlignEnum.RIGHT));
        PageDataSource<MovimientoFinancieroFila> datos = new PageDataSource<>(primero.movimientos(),
                pagina -> estadoCuentaCuenta.consultar(filtro.conPagina(pagina, BLOQUE)).movimientos(), fila -> Map.of(
                "fecha", fila.fecha(), "folio", texto(fila.folioPago()),
                "concepto", texto(fila.concepto()) + referencia(fila.referencia()),
                "tipo", fila.clase(),
                "ingreso", "INGRESO".equals(fila.direccion()) ? moneda(fila.monto()) : "-",
                "egreso", "EGRESO".equals(fila.direccion()) ? moneda(fila.monto()) : "-",
                "saldo", moneda(fila.saldoPosterior())));
        exportar("Estado de cuenta financiera", parametros(filtro.institucionId(),
                primero.cuenta().etiqueta() + " | " + filtro.desde().format(FECHA) + " al " + filtro.hasta().format(FECHA),
                "Saldo inicial", moneda(r.saldoApertura()), "Ingresos", moneda(r.ingresosOperativos().add(r.traspasosEntrada())),
                "Egresos", moneda(r.egresosOperativos().add(r.traspasosSalida())), "Saldo final", moneda(r.saldoCierre())),
                columnas, datos, salida);
    }

    public void estadoAlumno(FiltroEstadoCuentaAlumno original, OutputStream salida) {
        FiltroEstadoCuentaAlumno filtro = consulta.normalizar(original);
        if (filtro.alumnoId() == null) throw new ReglaNegocioException("Selecciona un alumno");
        ResultadoEstadoCuentaAlumno primero = consulta.estadoCuenta(filtro.conPagina(0, BLOQUE));
        ResumenEstadoCuenta r = primero.resumen();
        List<Columna> columnas = List.of(
                new Columna("vence", "Vencimiento", 70, HorizontalTextAlignEnum.LEFT),
                new Columna("plantel", "Plantel", 105, HorizontalTextAlignEnum.LEFT),
                new Columna("concepto", "Concepto", 205, HorizontalTextAlignEnum.LEFT),
                new Columna("total", "Cargo", 90, HorizontalTextAlignEnum.RIGHT),
                new Columna("aplicado", "Pagado", 90, HorizontalTextAlignEnum.RIGHT),
                new Columna("saldo", "Saldo", 90, HorizontalTextAlignEnum.RIGHT),
                new Columna("situacion", "Situación", 78, HorizontalTextAlignEnum.LEFT));
        PageDataSource<EstadoCuentaCargoFila> datos = new PageDataSource<>(primero.pagina(),
                pagina -> consulta.estadoCuenta(filtro.conPagina(pagina, BLOQUE)).pagina(), fila -> Map.of(
                "vence", fila.fechaVencimiento().format(FECHA), "plantel", fila.plantel(),
                "concepto", fila.concepto(), "total", moneda(fila.importeTotal()),
                "aplicado", moneda(fila.aplicado()), "saldo", moneda(fila.saldo()),
                "situacion", fila.situacion()));
        exportar("Estado de cuenta del alumno", parametros(filtro.institucionId(),
                primero.alumno() + " | Matrícula " + primero.matricula() + " | Corte " + filtro.fechaCorte().format(FECHA),
                "Cargos", Long.toString(r.cargos()), "Importe", moneda(r.importeTotal()),
                "Pagado", moneda(r.aplicado()), "Saldo pendiente", moneda(r.saldo())), columnas, datos, salida);
    }

    public void concentradoCobranza(FiltroConcentradoCobranza original, OutputStream salida) {
        FiltroConcentradoCobranza filtro=consulta.normalizar(original);
        ResultadoConcentradoCobranza primero=consulta.concentradoCobranza(filtro.conPagina(0,BLOQUE));
        ResumenEstadoCuenta r=primero.resumen();
        List<Columna> columnas=List.of(
                new Columna("grupo",etiquetaGrupo(filtro.agrupacion()),246,HorizontalTextAlignEnum.LEFT),
                new Columna("cargos","Cargos",70,HorizontalTextAlignEnum.RIGHT),
                new Columna("importe","Importe",115,HorizontalTextAlignEnum.RIGHT),
                new Columna("aplicado","Cobrado",115,HorizontalTextAlignEnum.RIGHT),
                new Columna("saldo","Por cobrar",115,HorizontalTextAlignEnum.RIGHT),
                new Columna("vencido","Vencido",115,HorizontalTextAlignEnum.RIGHT));
        PageDataSource<ConcentradoCobranzaFila> datos=new PageDataSource<>(primero.pagina(),
                pagina->consulta.concentradoCobranza(filtro.conPagina(pagina,BLOQUE)).pagina(), fila->Map.of(
                "grupo",fila.grupo(),"cargos",Long.toString(fila.cargos()),"importe",moneda(fila.importe()),
                "aplicado",moneda(fila.aplicado()),"saldo",moneda(fila.saldo()),"vencido",moneda(fila.vencido())));
        exportar("Concentrado de cobranza",parametros(filtro.institucionId(),
                "Agrupado por "+etiquetaGrupo(filtro.agrupacion()).toLowerCase()+" | Corte "+filtro.fechaCorte().format(FECHA),
                "Cargos",Long.toString(r.cargos()),"Importe",moneda(r.importeTotal()),
                "Cobrado",moneda(r.aplicado()),"Por cobrar",moneda(r.saldo())),columnas,datos,salida);
    }

    private Map<String, Object> parametros(Long institucionId, String subtitulo, String... resumen) {
        Map<String, Object> p = new HashMap<>();
        p.put("institucion", instituciones.obtener(institucionId).nombre());
        p.put("subtitulo", subtitulo);
        p.put("generado", "Generado por " + usuario() + " | " + java.time.LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        for (int i = 0; i < 4; i++) {
            p.put("r" + (i + 1) + "t", resumen[i * 2]);
            p.put("r" + (i + 1) + "v", resumen[i * 2 + 1]);
        }
        return p;
    }

    private void exportar(String titulo, Map<String, Object> parametros, List<Columna> columnas,
                          JRDataSource datos, OutputStream salida) {
        try {
            parametros.put("titulo", titulo);
            JasperReport reporte = JasperCompileManager.compileReport(diseno(columnas));
            JasperPrint impresion = JasperFillManager.fillReport(reporte, parametros, datos);
            JasperExportManager.exportReportToPdfStream(impresion, salida);
        } catch (JRException e) {
            throw new IllegalStateException("No fue posible generar el reporte PDF", e);
        }
    }

    private JasperDesign diseno(List<Columna> columnas) throws JRException {
        JasperDesign d = new JasperDesign();
        d.setName("reporte_financiero_" + UUID.randomUUID()); d.setPageWidth(842); d.setPageHeight(595);
        d.setOrientation(OrientationEnum.LANDSCAPE); d.setLeftMargin(28); d.setRightMargin(28);
        d.setTopMargin(24); d.setBottomMargin(24); d.setColumnWidth(786);
        for (String p : List.of("titulo", "institucion", "subtitulo", "generado",
                "r1t", "r1v", "r2t", "r2v", "r3t", "r3v", "r4t", "r4v")) parametro(d, p);
        for (Columna c : columnas) { JRDesignField f = new JRDesignField(); f.setName(c.campo()); f.setValueClass(String.class); d.addField(f); }

        JRDesignBand title = new JRDesignBand(); title.setHeight(112);
        title.addElement(textoExpresion("$P{institucion}", 0, 0, 786, 20, 15, true, Color.WHITE, AZUL, HorizontalTextAlignEnum.LEFT));
        title.addElement(textoExpresion("$P{titulo}", 0, 20, 786, 30, 21, true, Color.WHITE, AZUL, HorizontalTextAlignEnum.LEFT));
        title.addElement(textoExpresion("$P{subtitulo}", 0, 50, 786, 18, 10, false, Color.WHITE, AZUL, HorizontalTextAlignEnum.LEFT));
        for (int i=0;i<4;i++) {
            int x=i*196;
            title.addElement(textoExpresion("$P{r"+(i+1)+"t}", x, 76, 190, 13, 8, false, GRIS, AZUL_CLARO, HorizontalTextAlignEnum.LEFT));
            title.addElement(textoExpresion("$P{r"+(i+1)+"v}", x, 89, 190, 19, 12, true, AZUL, AZUL_CLARO, HorizontalTextAlignEnum.LEFT));
        }
        d.setTitle(title);

        JRDesignBand cabecera = new JRDesignBand(); cabecera.setHeight(23); int x=0;
        for (Columna c:columnas) { cabecera.addElement(textoConstante(c.titulo(), x,0,c.ancho(),23,8,true,Color.WHITE,AZUL,c.alineacion())); x+=c.ancho(); }
        d.setColumnHeader(cabecera);
        JRDesignBand detail = new JRDesignBand(); detail.setHeight(22); x=0;
        for (Columna c:columnas) { detail.addElement(textoExpresion("$F{"+c.campo()+"}",x,0,c.ancho(),22,8,false,new Color(35,48,58),Color.WHITE,c.alineacion())); x+=c.ancho(); }
        ((JRDesignSection)d.getDetailSection()).addBand(detail);
        JRDesignBand footer = new JRDesignBand(); footer.setHeight(22);
        footer.addElement(textoExpresion("$P{generado}",0,5,620,15,8,false,GRIS,Color.WHITE,HorizontalTextAlignEnum.LEFT));
        footer.addElement(textoExpresion("\"Página \" + $V{PAGE_NUMBER}",650,5,136,15,8,false,GRIS,Color.WHITE,HorizontalTextAlignEnum.RIGHT));
        d.setPageFooter(footer);
        return d;
    }

    private void parametro(JasperDesign d, String nombre) throws JRException { JRDesignParameter p=new JRDesignParameter();p.setName(nombre);p.setValueClass(String.class);d.addParameter(p); }
    private JRDesignTextField textoExpresion(String e,int x,int y,int w,int h,int size,boolean bold,Color fg,Color bg,HorizontalTextAlignEnum align) {
        JRDesignTextField t=new JRDesignTextField();t.setExpression(new JRDesignExpression(e)); configurar(t,x,y,w,h,size,bold,fg,bg,align); return t;
    }
    private JRDesignStaticText textoConstante(String s,int x,int y,int w,int h,int size,boolean bold,Color fg,Color bg,HorizontalTextAlignEnum align) {
        JRDesignStaticText t=new JRDesignStaticText();t.setText(s); configurar(t,x,y,w,h,size,bold,fg,bg,align); return t;
    }
    private void configurar(JRDesignTextElement t,int x,int y,int w,int h,int size,boolean bold,Color fg,Color bg,HorizontalTextAlignEnum align) {
        t.setX(x);t.setY(y);t.setWidth(w);t.setHeight(h);t.setFontName("SansSerif");t.setFontSize((float)size);t.setBold(bold);
        t.setForecolor(fg);t.setBackcolor(bg);t.setMode(ModeEnum.OPAQUE);t.setHorizontalTextAlign(align);t.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
        t.getLineBox().setLeftPadding(4);t.getLineBox().setRightPadding(4);t.getLineBox().getBottomPen().setLineColor(new Color(220,226,231));t.getLineBox().getBottomPen().setLineWidth(.35f);
    }
    private String usuario() { var a=SecurityContextHolder.getContext().getAuthentication(); return a==null?"sistema":a.getName(); }
    private static String moneda(java.math.BigDecimal v) { return FormatoMoneda.formatear(v); }
    private static String texto(String v) { return v==null?"":v; }
    private static String referencia(String v) { return v==null||v.isBlank()?"":" | "+v; }
    private static String etiquetaGrupo(String grupo) { return switch (grupo) { case "PLANTEL"->"Plantel";case "CICLO"->"Ciclo escolar";default->"Concepto de cobro"; }; }
    private record Columna(String campo,String titulo,int ancho,HorizontalTextAlignEnum alineacion) { }

    private static final class PageDataSource<T> implements JRDataSource {
        private Page<T> pagina; private final IntFunction<Page<T>> cargador; private final Function<T,Map<String,String>> mapper;
        private int numeroPagina; private int indice=-1; private Map<String,String> actual=Map.of();
        private PageDataSource(Page<T> primera, IntFunction<Page<T>> cargador, Function<T,Map<String,String>> mapper) { this.pagina=primera;this.cargador=cargador;this.mapper=mapper; }
        @Override public boolean next() {
            indice++;
            while (indice>=pagina.getContent().size()) {
                if (numeroPagina+1>=pagina.getTotalPages()) return false;
                pagina=cargador.apply(++numeroPagina); indice=0;
            }
            actual=mapper.apply(pagina.getContent().get(indice)); return true;
        }
        @Override public Object getFieldValue(JRField field) { return actual.getOrDefault(field.getName(),""); }
    }
}
