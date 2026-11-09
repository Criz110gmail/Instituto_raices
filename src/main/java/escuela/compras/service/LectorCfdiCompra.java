package escuela.compras.service;

import escuela.common.exception.ReglaNegocioException;
import org.springframework.stereotype.Component;
import org.w3c.dom.*;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/** Lectura estructural local; no consulta SAT ni valida sellos o vigencia fiscal. */
@Component
public class LectorCfdiCompra {
    public record Concepto(int orden,String producto,String unidad,String descripcion,BigDecimal cantidad,
                           BigDecimal precio,BigDecimal importe,BigDecimal descuento) {}
    public record Impuesto(Integer concepto,String tipo,String codigo,String factor,BigDecimal tasa,
                           BigDecimal base,BigDecimal importe) {}
    public record Relacion(String tipo,String uuid) {}
    public record Cfdi(String uuid,String version,String serie,String folio,LocalDateTime fecha,String emisorRfc,
                       String emisorNombre,String receptorRfc,String receptorNombre,String moneda,String tipo,
                       BigDecimal subtotal,BigDecimal descuento,BigDecimal total,List<Concepto> conceptos,
                       List<Impuesto> impuestos,List<Relacion> relaciones) {}
    public Cfdi leer(byte[] xml) {
        if(xml.length==0 || xml.length>2*1024*1024) throw error("El XML debe tener contenido y no superar 2 MB");
        try {
            var factory=DocumentBuilderFactory.newInstance(); factory.setNamespaceAware(true);
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING,true);
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl",true);
            factory.setFeature("http://xml.org/sax/features/external-general-entities",false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities",false);
            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD,"");factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA,"");
            factory.setAttribute("http://www.oracle.com/xml/jaxp/properties/maxElementDepth","64");
            factory.setXIncludeAware(false);factory.setExpandEntityReferences(false);
            var builder=factory.newDocumentBuilder();builder.setErrorHandler(new org.xml.sax.helpers.DefaultHandler(){
                @Override public void fatalError(org.xml.sax.SAXParseException e) throws org.xml.sax.SAXException {throw e;}
                @Override public void error(org.xml.sax.SAXParseException e) throws org.xml.sax.SAXException {throw e;}
            });
            Element root=builder.parse(new ByteArrayInputStream(xml)).getDocumentElement();
            String ns=root.getNamespaceURI(),version=attr(root,"Version",3,true);
            if(!"Comprobante".equals(root.getLocalName()) || !("4.0".equals(version)&&"http://www.sat.gob.mx/cfd/4".equals(ns)
                    ||"3.3".equals(version)&&"http://www.sat.gob.mx/cfd/3".equals(ns))) throw error("Selecciona un CFDI 4.0 o 3.3 válido");
            Element emisor=uno(root,ns,"Emisor"),receptor=uno(root,ns,"Receptor");
            Element complemento=uno(root,ns,"Complemento");
            Element timbre=uno(complemento,"http://www.sat.gob.mx/TimbreFiscalDigital","TimbreFiscalDigital");
            var conceptos=new ArrayList<Concepto>();var impuestos=new ArrayList<Impuesto>();var relaciones=new ArrayList<Relacion>();
            for(Element e:hijos(uno(root,ns,"Conceptos"),ns,"Concepto")) {
                int orden=conceptos.size()+1;if(orden>1000)throw error("El CFDI no puede superar mil conceptos");
                BigDecimal cantidad=numero(e,"Cantidad",true);if(cantidad.signum()<=0)throw error("La cantidad de cada concepto debe ser mayor que cero");
                conceptos.add(new Concepto(orden,attr(e,"ClaveProdServ",20,true),attr(e,"ClaveUnidad",20,true),attr(e,"Descripcion",1000,true),
                    cantidad,numero(e,"ValorUnitario",true),numero(e,"Importe",true),numero(e,"Descuento",false)));
                for(Element tax:hijos(e,ns,"Impuestos"))impuestos(tax,ns,orden,impuestos);
            }
            if(conceptos.isEmpty())throw error("El CFDI no contiene conceptos");
            for(Element tax:hijos(root,ns,"Impuestos"))impuestos(tax,ns,null,impuestos);
            for(Element rel:hijos(root,ns,"CfdiRelacionados"))for(Element e:hijos(rel,ns,"CfdiRelacionado")) {
                if(relaciones.size()>=1000)throw error("Demasiadas relaciones en el XML");
                relaciones.add(new Relacion(attr(rel,"TipoRelacion",2,true),uuid(attr(e,"UUID",36,true))));
            }
            String moneda=attr(root,"Moneda",3,true),tipo=attr(root,"TipoDeComprobante",1,true);
            if(!moneda.matches("[A-Z]{3}")||!Set.of("I","E","T","N","P").contains(tipo))throw error("Moneda o tipo de comprobante no válido");
            return new Cfdi(uuid(attr(timbre,"UUID",36,true)),version,attr(root,"Serie",100,false),attr(root,"Folio",100,false),
                LocalDateTime.parse(attr(root,"Fecha",19,true)),rfc(emisor),attr(emisor,"Nombre",300,false),rfc(receptor),
                attr(receptor,"Nombre",300,false),moneda,tipo,numero(root,"SubTotal",true),numero(root,"Descuento",false),
                numero(root,"Total",true),List.copyOf(conceptos),List.copyOf(impuestos),List.copyOf(relaciones));
        } catch(ReglaNegocioException e){throw e;} catch(Exception e){throw error("El XML no es un CFDI legible y completo; revisa su estructura y fechas");}
    }
    private void impuestos(Element e,String ns,Integer concepto,List<Impuesto> destino) {
        for(String grupo:List.of("Traslados","Retenciones"))for(Element container:hijos(e,ns,grupo))
            for(Element tax:hijos(container,ns,grupo.equals("Traslados")?"Traslado":"Retencion")) {
                if(destino.size()>=10000)throw error("Demasiados impuestos en el XML");
                destino.add(new Impuesto(concepto,grupo.equals("Traslados")?"TRASLADO":"RETENCION",attr(tax,"Impuesto",3,true),
                    attr(tax,"TipoFactor",10,false),opcional(tax,"TasaOCuota"),opcional(tax,"Base"),opcional(tax,"Importe")));
            }
    }
    private static BigDecimal opcional(Element e,String nombre){return e.hasAttribute(nombre)?numero(e,nombre,true):null;}
    private static BigDecimal numero(Element e,String nombre,boolean requerido) {
        String s=attr(e,nombre,26,requerido);if(s.isEmpty())return BigDecimal.ZERO;
        if(!s.matches("\\d{1,13}(\\.\\d{1,6})?"))throw error("Importe o cantidad inválida: "+nombre);
        return new BigDecimal(s);
    }
    private static String rfc(Element e){String s=attr(e,"Rfc",13,true).toUpperCase(Locale.ROOT);if(!s.matches("[A-ZÑ&]{3,4}[0-9]{6}[A-Z0-9]{3}"))throw error("El RFC del XML no es válido");return s;}
    private static String uuid(String s){if(!s.matches("[0-9a-fA-F]{8}(-[0-9a-fA-F]{4}){3}-[0-9a-fA-F]{12}"))throw error("El folio fiscal UUID no es válido");return s.toUpperCase(Locale.ROOT);}
    private static String attr(Element e,String nombre,int max,boolean requerido){String s=e.getAttribute(nombre).trim();if(s.length()>max||requerido&&s.isEmpty())throw error("Falta o no es válido el atributo "+nombre);return s;}
    private static Element uno(Element e,String ns,String nombre){var lista=hijos(e,ns,nombre);if(lista.size()!=1)throw error("El CFDI requiere un único nodo "+nombre);return lista.getFirst();}
    private static List<Element> hijos(Element e,String ns,String nombre){var r=new ArrayList<Element>();for(Node n=e.getFirstChild();n!=null;n=n.getNextSibling())if(n instanceof Element x&&nombre.equals(x.getLocalName())&&Objects.equals(ns,x.getNamespaceURI()))r.add(x);return r;}
    private static ReglaNegocioException error(String s){return new ReglaNegocioException(s);}
}
