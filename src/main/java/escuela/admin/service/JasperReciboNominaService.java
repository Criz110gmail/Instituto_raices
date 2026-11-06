package escuela.admin.service;

import escuela.archivo.entity.EstadoArchivo;
import escuela.archivo.repository.ArchivoRepository;
import escuela.archivo.storage.AlmacenamientoArchivo;
import escuela.institucion.repository.InstitucionRepository;
import escuela.personal.service.ReciboNominaService;
import escuela.personal.service.ReciboNominaService.Detalle;
import java.awt.*;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.*;
import javax.imageio.ImageIO;
import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import net.sf.jasperreports.engine.design.*;
import net.sf.jasperreports.engine.type.*;
import org.springframework.stereotype.Service;

/** Resumen compartible de datos históricos autorizados; excluye la trazabilidad administrativa. */
@Service @RequiredArgsConstructor
public class JasperReciboNominaService {
    private final ReciboNominaService recibos;
    private final InstitucionRepository instituciones;
    private final ArchivoRepository archivos;
    private final AlmacenamientoArchivo almacenamiento;
    private volatile JasperReport compilado;
    private static final Color AZUL=new Color(18,45,72),GRIS=new Color(79,94,107),FONDO=new Color(237,244,247);
    private static final DateTimeFormatter FECHA=DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public void exportar(String tipo,Long id,OutputStream salida){
        exportarAutorizado(recibos.obtener(tipo,id),salida);
    }
    /** El llamador debe haber validado pertenencia y acceso vigentes en el servicio. */
    public void exportarAutorizado(Detalle d,OutputStream salida){
        try{
            var p=new HashMap<String,Object>();p.put("institucion",d.institucion());p.put("folio",d.recibo().folio());
            p.put("estado",d.recibo().estadoTexto().toUpperCase(Locale.ROOT));p.put("logo",logo(d.institucionId()));
            p.put("identidad",d.recibo().folio()+" | "+d.recibo().codigo()+" | "+d.recibo().nombre());
            p.put("nota",d.recibo().estado().equals("CANCELADO")?"PAGO CANCELADO - Sin vigencia como comprobante de pago actual.":d.recibo().tipo().equals("CERO")?"El importe neto de este recibo es cero.":d.recibo().tipo().equals("HISTORICO")?"Documento de nómina histórica. No representa un pago adicional.":"Resumen de percepciones, deducciones e importe neto del periodo.");
            var print=JasperFillManager.fillReport(reporte(),p,new JRMapCollectionDataSource(filas(d)));
            JasperExportManager.exportReportToPdfStream(print,salida);
        }catch(JRException e){throw new IllegalStateException("No fue posible generar el recibo interno de nómina",e);}
    }
    private List<Map<String,?>> filas(Detalle d){
        List<Map<String,?>> filas=new ArrayList<>();var r=d.recibo();
        // Partidas salariales permitidas; nunca fuentes de dinero ni notas administrativas.
        fila(filas,"Datos del empleado","Empleado: "+r.nombre(),"");
        fila(filas,"Datos del empleado","Código laboral: "+r.codigo(),"");
        fila(filas,"Datos del empleado","Puesto: "+d.puestos(),"");
        fila(filas,"Datos del empleado","Periodo: "+r.inicio().format(FECHA)+" al "+r.fin().format(FECHA),"");
        conceptos(filas,d,"SUELDO","Percepciones",d.sueldo(),"Sueldo base");
        conceptos(filas,d,"PERCEPCION","Percepciones",d.percepciones(),"Otras percepciones (sin desglose histórico disponible)");
        conceptos(filas,d,"DEDUCCION","Deducciones",d.deducciones(),"Deducciones (sin desglose histórico disponible)");
        fila(filas,"Resumen del periodo","Total de percepciones",dinero(d.sueldo().add(d.percepciones()),r.moneda()));
        fila(filas,"Resumen del periodo","Total de deducciones",dinero(d.deducciones(),r.moneda()));
        fila(filas,"Resumen del periodo","NETO DEL RECIBO",dinero(r.neto(),r.moneda()));
        fila(filas,"Alcance del documento","Comprobante interno. No es CFDI ni recibo fiscal timbrado.","");
        return filas;
    }
    private void conceptos(List<Map<String,?>> filas,Detalle d,String naturaleza,String seccion,BigDecimal total,String sinDesglose){
        var partidas=d.lineas().stream().filter(l->naturaleza.equals(l.naturaleza())&&l.importe()!=null&&l.importe().signum()!=0).toList();
        for(var l:partidas)fila(filas,seccion,(naturaleza.equals("SUELDO")?"Sueldo base: ":"")+texto(l.nombre()),dinero(l.importe(),d.recibo().moneda()));
        // Fotografías antiguas: mostrar el total conocido sin inventar conceptos ni duplicarlo.
        if(partidas.isEmpty()&&total.signum()!=0)fila(filas,seccion,sinDesglose,dinero(total,d.recibo().moneda()));
    }
    private static void fila(List<Map<String,?>> filas,String seccion,String detalle,String importe){filas.add(Map.of("seccion",seccion,"detalle",texto(detalle),"importe",importe));}
    private static String texto(String s){return s==null?"":s;}
    private static String dinero(BigDecimal n,String moneda){return String.format(Locale.US,"$%,.2f",n==null?BigDecimal.ZERO:n)+" "+moneda;}
    private Image logo(Long institucion){
        var i=instituciones.findById(institucion).orElse(null);
        if(i!=null&&i.getLogoArchivoId()!=null){var a=archivos.findById(i.getLogoArchivoId()).orElse(null);
            if(a!=null&&a.getInstitucion().getId().equals(institucion)&&a.getEstado()==EstadoArchivo.DISPONIBLE&&a.getTipoMime().startsWith("image/")){
                try(var in=almacenamiento.abrir(a.getClaveAlmacenamiento()).getInputStream()){var imagen=ImageIO.read(in);if(imagen!=null)return imagen;}catch(Exception ignored){/* Mismo respaldo visual institucional que los demás reportes. */}
            }
        }
        return MarcaReporteFinanciero.imagen();
    }
    private JasperReport reporte()throws JRException{var r=compilado;if(r==null)synchronized(this){if(compilado==null)compilado=JasperCompileManager.compileReport(diseno());r=compilado;}return r;}
    private JasperDesign diseno()throws JRException{
        var d=new JasperDesign();d.setName("ReciboInternoNomina");d.setPageWidth(595);d.setPageHeight(842);d.setLeftMargin(36);d.setRightMargin(36);d.setTopMargin(30);d.setBottomMargin(30);d.setColumnWidth(523);
        for(String n:List.of("institucion","folio","estado","nota","identidad")){var p=new JRDesignParameter();p.setName(n);p.setValueClass(String.class);d.addParameter(p);}
        var logo=new JRDesignParameter();logo.setName("logo");logo.setValueClass(Image.class);d.addParameter(logo);
        for(String n:List.of("seccion","detalle","importe")){var f=new JRDesignField();f.setName(n);f.setValueClass(String.class);d.addField(f);}
        var titulo=new JRDesignBand();titulo.setHeight(106);
        var imagen=new JRDesignImage(d);imagen.setX(0);imagen.setY(0);imagen.setWidth(80);imagen.setHeight(68);imagen.setScaleImage(ScaleImageEnum.RETAIN_SHAPE);imagen.setExpression(new JRDesignExpression("$P{logo}"));titulo.addElement(imagen);
        titulo.addElement(texto("$P{institucion}",94,0,429,30,15,true,AZUL,false));
        titulo.addElement(texto("\"RECIBO INTERNO DE NÓMINA\"",94,32,429,23,13,true,AZUL,false));
        titulo.addElement(texto("$P{folio}",94,58,429,18,10,false,GRIS,false));
        var estiloEstado=new JRDesignStyle();estiloEstado.setName("EstadoRecibo");estiloEstado.setForecolor(AZUL);var cancelado=new JRDesignConditionalStyle();cancelado.setConditionExpression(new JRDesignExpression("$P{estado}.equals(\"PAGO CANCELADO\")"));cancelado.setForecolor(new Color(139,41,51));estiloEstado.addConditionalStyle(cancelado);d.addStyle(estiloEstado);
        var estado=texto("$P{estado}",0,79,523,25,12,true,AZUL,false);estado.setForecolor(null);estado.setStyle(estiloEstado);estado.setBackcolor(FONDO);estado.setMode(ModeEnum.OPAQUE);titulo.addElement(estado);d.setTitle(titulo);
        var cab=new JRDesignBand();cab.setHeight(42);cab.addElement(texto("$P{identidad}",0,0,523,17,8,true,AZUL,false));var nota=texto("$P{nota}",0,18,523,24,8,false,GRIS,false);cab.addElement(nota);d.setPageHeader(cab);
        var grupo=new JRDesignGroup();grupo.setName("Seccion");grupo.setExpression(new JRDesignExpression("$F{seccion}"));grupo.setReprintHeaderOnEachPage(true);grupo.setMinHeightToStartNewPage(60);
        var gc=new JRDesignBand();gc.setHeight(23);var gt=texto("$F{seccion}",0,1,523,22,10,true,AZUL,false);gt.setBackcolor(FONDO);gt.setMode(ModeEnum.OPAQUE);gc.addElement(gt);((JRDesignSection)grupo.getGroupHeaderSection()).addBand(gc);d.addGroup(grupo);
        var detalle=new JRDesignBand();detalle.setHeight(20);detalle.setSplitType(SplitTypeEnum.PREVENT);
        var izquierda=texto("$F{detalle}",0,0,375,20,9,false,AZUL,false);izquierda.setTextAdjust(TextAdjustEnum.STRETCH_HEIGHT);detalle.addElement(izquierda);
        var derecha=texto("$F{importe}",375,0,148,20,9,true,AZUL,true);derecha.setTextAdjust(TextAdjustEnum.STRETCH_HEIGHT);detalle.addElement(derecha);((JRDesignSection)d.getDetailSection()).addBand(detalle);
        var pie=new JRDesignBand();pie.setHeight(28);pie.addElement(texto("\"Comprobante interno - No es CFDI\"",0,6,390,18,8,false,GRIS,false));pie.addElement(texto("\"Página \"+$V{PAGE_NUMBER}",390,6,133,18,8,false,GRIS,true));d.setPageFooter(pie);
        return d;
    }
    private JRDesignTextField texto(String expresion,int x,int y,int ancho,int alto,int tam,boolean negrita,Color color,boolean derecha){
        var t=new JRDesignTextField();t.setX(x);t.setY(y);t.setWidth(ancho);t.setHeight(alto);t.setFontName("DejaVu Sans");t.setFontSize((float)tam);t.setBold(negrita);t.setForecolor(color);
        t.setExpression(new JRDesignExpression(expresion));t.setTextAdjust(TextAdjustEnum.SCALE_FONT);t.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);t.setHorizontalTextAlign(derecha?HorizontalTextAlignEnum.RIGHT:HorizontalTextAlignEnum.LEFT);
        t.getLineBox().setLeftPadding(7);t.getLineBox().setRightPadding(7);t.getLineBox().setTopPadding(4);t.getLineBox().setBottomPadding(4);return t;
    }
}
