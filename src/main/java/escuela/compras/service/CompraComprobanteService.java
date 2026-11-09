package escuela.compras.service;

import escuela.archivo.dto.ArchivoDescarga;
import escuela.archivo.entity.Archivo;
import escuela.archivo.imagen.ProcesadorFotografia;
import escuela.archivo.repository.ArchivoRepository;
import escuela.archivo.storage.AlmacenamientoArchivo;
import escuela.common.exception.*;
import escuela.compras.dto.*;
import escuela.compras.entity.*;
import escuela.compras.repository.CompraRepository;
import escuela.seguridad.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.*;

import java.io.*;
import java.math.*;
import java.security.*;
import java.time.Instant;
import java.util.*;

@Service @RequiredArgsConstructor @Transactional
public class CompraComprobanteService {
    private final CompraRepository compras;
    private final ArchivoRepository archivos;
    private final AlmacenamientoArchivo almacenamiento;
    private final ProcesadorFotografia imagenes;
    private final LectorCfdiCompra lector;
    private final AlcanceDatosService alcance;
    private final JdbcTemplate jdbc;
    private final jakarta.persistence.EntityManager entityManager;

    public record Documento(Long id,String tipo,String descripcion,String nombre,long tamano,Instant fecha,
                            Instant retirado,String motivoRetiro,String uuid,String justificacion) {}
    public record Respaldo(Long id,String tipo,String justificacion,Instant fecha) {}
    public record Expediente(Respaldo respaldo,Page<Documento> documentos,List<Respaldo> historial) {}
    public record Fiscal(Documento documento,LectorCfdiCompra.Cfdi cfdi,List<String> diferencias,
                         List<CompraPartidaForm> partidas,String impedimento) {}

    @Transactional(readOnly=true)
    public Expediente expediente(Long id,String tipo,String estado,String texto,int pagina) {
        Compra c=compra(id,false,false);String q=texto(texto,300).toLowerCase(Locale.ROOT);
        if(tipo!=null&&!tipo.isBlank()&&!Set.of("PDF","IMAGEN","XML").contains(tipo))throw regla("Selecciona un tipo de archivo válido");
        if(!Set.of("TODOS","VIGENTES","RETIRADOS").contains(estado))throw regla("Selecciona un estado de documento válido");
        var lista=documentos(c.getId()).stream().filter(d->tipo==null||tipo.isBlank()||d.tipo().equals(tipo))
            .filter(d->"TODOS".equals(estado)||("RETIRADOS".equals(estado)?d.retirado()!=null:d.retirado()==null))
            .filter(d->(d.nombre()+" "+d.descripcion()+" "+Objects.toString(d.uuid(),"")).toLowerCase(Locale.ROOT).contains(q)).toList();
        int p=Math.max(0,pagina),inicio=(int)Math.min((long)p*20,lista.size()),fin=Math.min(inicio+20,lista.size());
        var historia=historial(c.getId());
        return new Expediente(historia.isEmpty()?null:historia.getFirst(),new PageImpl<>(lista.subList(inicio,fin),PageRequest.of(p,20),lista.size()),historia);
    }

    public void clasificar(Long id,CompraRespaldoForm f) {
        Compra c=compra(id,true,true);confirmado(f.isConfirmado());
        var historia=historial(id);long actual=historia.isEmpty()?0:historia.getFirst().id();
        if(!Objects.equals(f.getVersion(),actual))throw regla("El respaldo cambió. Recarga antes de guardar");
        if(!Set.of("SIN_COMPROBANTE","TICKET_RECIBO","CFDI").contains(Objects.toString(f.getTipo(),"")))throw regla("Selecciona el tipo de respaldo");
        String motivo=texto(f.getJustificacion(),2000);
        if("SIN_COMPROBANTE".equals(f.getTipo())&&motivo.isBlank())throw regla("Explica por qué esta compra no tiene comprobante");
        if("CFDI".equals(f.getTipo())) {
            if(jdbc.queryForObject("SELECT count(*) FROM compra_documento d JOIN compra_cfdi x ON x.documento_id=d.id WHERE d.compra_id=? AND d.retirado_en IS NULL",Long.class,id)==0)
                throw regla("Primero adjunta un XML CFDI válido; el PDF por sí solo no sustituye al XML");
            for(Documento d:documentos(id))if(d.retirado()==null&&d.uuid()!=null&&!fiscal(id,d.id()).diferencias().isEmpty()&&motivo.isBlank())
                throw regla("Hay diferencias con el CFDI. Explica por qué se conserva como respaldo");
        }
        jdbc.update("INSERT INTO compra_respaldo(compra_id,tipo,justificacion,creado_por_id) VALUES (?,?,?,?)",c.getId(),f.getTipo(),motivo,actor());
    }

    /** Revisa de nuevo el respaldo elegido si el borrador cambió después de adjuntar el XML. */
    public void validarConfirmacion(Long id) {
        compra(id,false,true);var historia=historial(id);
        if(historia.isEmpty()||!historia.getFirst().tipo().equals("CFDI"))return;
        var vigentes=documentos(id).stream().filter(d->d.tipo().equals("XML")&&d.retirado()==null).toList();
        if(vigentes.isEmpty())throw regla("El respaldo CFDI requiere un XML vigente");
        for(var d:vigentes)if(!fiscal(id,d.id()).diferencias().isEmpty()&&d.justificacion().isBlank()&&historia.getFirst().justificacion().isBlank())
            throw regla("La compra presenta diferencias con el XML. Revisa Comprobantes y justifica el respaldo antes de confirmar");
    }

    public void agregar(Long id,CompraDocumentoForm f) {
        Compra c=compra(id,true,true);confirmado(f.isConfirmado());version(c,f.getVersion());
        var file=f.getArchivo();
        if(file==null||file.isEmpty())throw regla("Selecciona un archivo");
        if(file.getSize()>20L*1024*1024)throw regla("El archivo no puede superar 20 MB");
        if(documentos(id).size()>=200)throw regla("Esta compra alcanzó el límite de 200 documentos conservados");
        String tipo=Objects.toString(f.getTipo(),""),nombre=nombre(file.getOriginalFilename()),mime;byte[] contenido;
        LectorCfdiCompra.Cfdi cfdi=null;
        try {
            switch(tipo) {
                case "PDF" -> {
                    if(file.getSize()>10L*1024*1024)throw regla("El PDF no puede superar 10 MB");
                    contenido=file.getBytes();if(contenido.length<5||!new String(contenido,0,5,java.nio.charset.StandardCharsets.US_ASCII).equals("%PDF-"))throw regla("Selecciona un archivo PDF válido");
                    try(var pdf=org.apache.pdfbox.Loader.loadPDF(contenido)){if(pdf.getNumberOfPages()<1)throw regla("El PDF no contiene páginas");}
                    catch(IOException e){throw regla("El PDF no se puede abrir. Usa un documento válido y sin contraseña");}
                    mime="application/pdf";
                }
                case "IMAGEN" -> {var optimizada=imagenes.procesarDocumento(file);contenido=optimizada.contenido();mime=optimizada.tipoMime();nombre=optimizada.nombreArchivo();}
                case "XML" -> {
                    if(file.getSize()>2L*1024*1024)throw regla("El XML no puede superar 2 MB");
                    contenido=file.getBytes();cfdi=lector.leer(contenido);mime="application/xml";
                    if(jdbc.queryForObject("SELECT count(*) FROM compra_cfdi WHERE institucion_id=? AND uuid=?",Long.class,c.getInstitucion().getId(),cfdi.uuid())>0)
                        throw regla("Ese UUID ya está vinculado a una compra de esta institución, incluso si se retiró el documento");
                    var diferencias=diferencias(c,cfdi);
                    if(!diferencias.isEmpty()&&texto(f.getJustificacion(),2000).isBlank())throw regla("Revisa estas diferencias y escribe una justificación: "+String.join(". ",diferencias));
                }
                default -> throw regla("Selecciona PDF, fotografía o XML CFDI");
            }
        } catch(IOException e){throw regla("No fue posible leer el archivo seleccionado");}
        String sha=sha(contenido);
        if(jdbc.queryForObject("SELECT count(*) FROM compra_documento d JOIN archivo a ON a.id=d.archivo_id WHERE d.compra_id=? AND a.checksum_sha256=?",Long.class,id,sha)>0)
            throw regla("Este archivo ya está en el expediente, incluso si fue retirado");
        String clave=c.getInstitucion().getId()+"/compras/"+id+"/"+UUID.randomUUID()+".bin";
        almacenamiento.guardar(clave,new ByteArrayInputStream(contenido));
        if(TransactionSynchronizationManager.isSynchronizationActive())TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization(){
            @Override public void afterCompletion(int status){if(status!=STATUS_COMMITTED)almacenamiento.eliminarSiExiste(clave);}
        });
        try {
            Archivo a=new Archivo();a.setInstitucion(c.getInstitucion());a.setClaveAlmacenamiento(clave);a.setNombreOriginal(nombre);
            a.setTipoMime(mime);a.setTamanoBytes(contenido.length);a.setChecksumSha256(sha);a=archivos.saveAndFlush(a);
            Long doc=jdbc.queryForObject("INSERT INTO compra_documento(compra_id,archivo_id,tipo,descripcion,justificacion,creado_por_id) VALUES (?,?,?,?,?,?) RETURNING id",Long.class,
                id,a.getId(),tipo,texto(f.getDescripcion(),300),texto(f.getJustificacion(),2000),actor());
            if(cfdi!=null)guardarCfdi(doc,c.getInstitucion().getId(),cfdi);
        } catch(org.springframework.dao.DuplicateKeyException e){almacenamiento.eliminarSiExiste(clave);throw regla("El archivo o UUID ya fue registrado. Recarga el expediente");}
        catch(RuntimeException e){almacenamiento.eliminarSiExiste(clave);throw e;}
    }

    public void retirar(Long id,Long doc,String motivo,boolean confirmado) {
        compra(id,true,true);confirmado(confirmado);String razon=texto(motivo,2000);if(razon.isBlank())throw regla("Explica por qué retiras el comprobante");
        Documento d=documento(id,doc);if(d.retirado()!=null)throw regla("El comprobante ya está retirado");
        var respaldo=historial(id);
        if(d.tipo().equals("XML")&&!respaldo.isEmpty()&&respaldo.getFirst().tipo().equals("CFDI")
                &&documentos(id).stream().filter(x->x.retirado()==null&&x.tipo().equals("XML")).count()==1)
            throw regla("Antes de retirar el único XML vigente, cambia la clasificación del respaldo a Ticket o recibo o Sin comprobante");
        jdbc.update("UPDATE compra_documento SET retirado_en=CURRENT_TIMESTAMP,retirado_por_id=?,motivo_retiro=? WHERE id=? AND compra_id=? AND retirado_en IS NULL",actor(),razon,doc,id);
    }

    @Transactional(readOnly=true)
    public ArchivoDescarga abrir(Long id,Long doc) {
        compra(id,false,false);documento(id,doc);
        Long archivo=jdbc.queryForObject("SELECT archivo_id FROM compra_documento WHERE id=? AND compra_id=?",Long.class,doc,id);
        Archivo a=archivos.findById(archivo).orElseThrow(()->regla("El archivo no está disponible"));
        if(a.getEstado()!=escuela.archivo.entity.EstadoArchivo.DISPONIBLE)throw regla("El archivo no está disponible");
        return new ArchivoDescarga(almacenamiento.abrir(a.getClaveAlmacenamiento()),a.getNombreOriginal(),a.getTipoMime(),a.getTamanoBytes());
    }

    @Transactional(readOnly=true)
    public Fiscal fiscal(Long id,Long doc) {
        Compra c=compra(id,false,false);Documento documento=documento(id,doc);
        if(!"XML".equals(documento.tipo()))throw regla("Este documento no es un XML CFDI");
        try(var in=abrir(id,doc).recurso().getInputStream()) {
            var cfdi=lector.leer(in.readNBytes(2*1024*1024+1));var partidas=new ArrayList<CompraPartidaForm>();String impedimento="";
            try{partidas.addAll(partidas(cfdi));}catch(ReglaNegocioException e){impedimento=e.getMessage();}
            if(documento.retirado()!=null)impedimento="El documento fue retirado del expediente vigente";
            if(c.getEstado()!=EstadoCompra.BORRADOR)impedimento="Sólo se importan partidas en una compra en borrador";
            if(!cfdi.moneda().equals(c.getCuenta().getMoneda()))impedimento="No se importan partidas de una moneda diferente a la cuenta de la compra";
            return new Fiscal(documento,cfdi,diferencias(c,cfdi),partidas,impedimento);
        }catch(IOException e){throw regla("No fue posible leer el XML conservado");}
    }

    public void importar(Long id,Long doc,Long version,String motivo,boolean confirmado) {
        Compra c=compra(id,true,true);confirmado(confirmado);version(c,version);
        if(c.getEstado()!=EstadoCompra.BORRADOR)throw regla("Sólo se importan partidas en un borrador");
        String razon=texto(motivo,2000);if(razon.isBlank())throw regla("Explica el motivo de la importación");
        Fiscal vista=fiscal(id,doc);if(!vista.impedimento().isBlank())throw regla(vista.impedimento());
        if(!vista.cfdi().moneda().equals(c.getCuenta().getMoneda()))throw regla("No se importan partidas en una moneda diferente a la cuenta de la compra");
        c.getPartidas().clear();int orden=0;BigDecimal subtotal=BigDecimal.ZERO,impuesto=BigDecimal.ZERO;
        for(var p:vista.partidas()) {
            var d=new CompraDetalle();d.setCompra(c);d.setOrden(++orden);d.setDescripcion(p.getDescripcion());d.setCantidad(p.getCantidad());d.setPrecioUnitario(p.getPrecioUnitario());d.setPorcentajeImpuesto(p.getPorcentajeImpuesto());
            d.setSubtotal(p.getCantidad().multiply(p.getPrecioUnitario()).setScale(2,RoundingMode.HALF_UP));
            d.setImpuesto(d.getSubtotal().multiply(p.getPorcentajeImpuesto()).divide(new BigDecimal("100"),2,RoundingMode.HALF_UP));d.setTotal(d.getSubtotal().add(d.getImpuesto()));
            subtotal=subtotal.add(d.getSubtotal());impuesto=impuesto.add(d.getImpuesto());c.getPartidas().add(d);
        }
        c.setSubtotal(subtotal);c.setImpuesto(impuesto);c.setTotal(subtotal.add(impuesto));
        // El cambio fuerza la versión del borrador incluso si sus totales coinciden.
        String referencia=vista.cfdi().serie()+vista.cfdi().folio();
        if(!referencia.isBlank()&&c.getDocumentoReferencia()==null)c.setDocumentoReferencia(referencia.substring(0,Math.min(150,referencia.length())));
        compras.saveAndFlush(c);
        entityManager.lock(c,jakarta.persistence.LockModeType.OPTIMISTIC_FORCE_INCREMENT);
        jdbc.update("INSERT INTO compra_cfdi_importacion(documento_id,compra_id,version_anterior,motivo,creado_por_id) VALUES (?,?,?,?,?)",doc,id,version,razon,actor());
    }

    List<CompraPartidaForm> partidas(LectorCfdiCompra.Cfdi x) {
        String no="Este CFDI se conserva como respaldo, pero sus descuentos, impuestos o precisión requieren capturar las partidas manualmente";
        if(!x.tipo().equals("I")||x.descuento().signum()!=0||x.conceptos().size()>100)throw regla(no);
        if(x.impuestos().stream().anyMatch(t->t.tipo().equals("RETENCION")))throw regla(no);
        var r=new ArrayList<CompraPartidaForm>();BigDecimal total=BigDecimal.ZERO;
        for(var concepto:x.conceptos()) {
            var taxes=x.impuestos().stream().filter(t->Objects.equals(t.concepto(),concepto.orden())).toList();
            if(taxes.size()>1||concepto.descuento().signum()!=0||concepto.descripcion().length()>300)throw regla(no);
            BigDecimal tasa=BigDecimal.ZERO;
            if(!taxes.isEmpty()) {
                var t=taxes.getFirst();
                if(!t.codigo().equals("002")||!t.factor().equals("Tasa")||t.tasa()==null||t.importe()==null||t.base()==null)throw regla(no);
                tasa=t.tasa().multiply(new BigDecimal("100"));
                if(t.base().compareTo(concepto.importe())!=0)throw regla(no);
            }
            try {concepto.cantidad().setScale(3,RoundingMode.UNNECESSARY);concepto.precio().setScale(2,RoundingMode.UNNECESSARY);tasa.setScale(2,RoundingMode.UNNECESSARY);}catch(ArithmeticException e){throw regla(no);}
            if(concepto.cantidad().compareTo(new BigDecimal("999999999.999"))>0||tasa.compareTo(new BigDecimal("100"))>0)throw regla(no);
            var sub=concepto.cantidad().multiply(concepto.precio()).setScale(2,RoundingMode.HALF_UP);
            if(sub.compareTo(concepto.importe())!=0)throw regla(no);
            var tax=sub.multiply(tasa).divide(new BigDecimal("100"),2,RoundingMode.HALF_UP);
            if(!taxes.isEmpty()&&tax.compareTo(taxes.getFirst().importe())!=0)throw regla(no);
            total=total.add(sub).add(tax);
            var p=new CompraPartidaForm();p.setDescripcion(concepto.descripcion());p.setCantidad(concepto.cantidad());p.setPrecioUnitario(concepto.precio());p.setPorcentajeImpuesto(tasa);r.add(p);
        }
        BigDecimal impuestoGlobal=x.impuestos().stream().filter(t->t.concepto()==null).map(t->t.importe()==null?BigDecimal.ZERO:t.importe()).reduce(BigDecimal.ZERO,BigDecimal::add);
        if(total.signum()<=0||total.compareTo(x.total())!=0||x.subtotal().add(impuestoGlobal).compareTo(total)!=0||r.stream().map(p->p.getCantidad().multiply(p.getPrecioUnitario()).setScale(2,RoundingMode.HALF_UP)).reduce(BigDecimal.ZERO,BigDecimal::add).compareTo(x.subtotal())!=0)throw regla(no);
        return List.copyOf(r);
    }

    private List<String> diferencias(Compra c,LectorCfdiCompra.Cfdi x) {
        var r=new ArrayList<String>();
        if(c.getProveedor().getRfc()==null||!c.getProveedor().getRfc().equalsIgnoreCase(x.emisorRfc()))r.add("El RFC emisor no coincide con el proveedor o falta en su catálogo");
        if(c.getInstitucion().getRfc()==null||!c.getInstitucion().getRfc().equalsIgnoreCase(x.receptorRfc()))r.add("El RFC receptor no coincide con la institución o falta en su catálogo");
        if(!c.getCuenta().getMoneda().equals(x.moneda()))r.add("La moneda difiere de la cuenta de la compra");
        if(c.getTotal().compareTo(x.total())!=0)r.add("El total XML "+x.total().toPlainString()+" difiere del total de la compra "+c.getTotal().toPlainString());
        if(c.getSubtotal().compareTo(x.subtotal())!=0)r.add("El subtotal XML difiere de la compra");
        if(!x.tipo().equals("I"))r.add("El XML no es un comprobante de ingreso; revisa si corresponde a esta compra");
        return List.copyOf(r);
    }
    private void guardarCfdi(Long doc,Long inst,LectorCfdiCompra.Cfdi x) {
        jdbc.update("INSERT INTO compra_cfdi(documento_id,institucion_id,uuid,version_cfdi,serie,folio,fecha,emisor_rfc,emisor_nombre,receptor_rfc,receptor_nombre,moneda,tipo_comprobante,subtotal,descuento,total) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
            doc,inst,x.uuid(),x.version(),x.serie(),x.folio(),java.sql.Timestamp.valueOf(x.fecha()),x.emisorRfc(),x.emisorNombre(),x.receptorRfc(),x.receptorNombre(),x.moneda(),x.tipo(),x.subtotal(),x.descuento(),x.total());
        for(var p:x.conceptos())jdbc.update("INSERT INTO compra_cfdi_concepto(documento_id,orden,clave_producto,clave_unidad,descripcion,cantidad,valor_unitario,importe,descuento) VALUES (?,?,?,?,?,?,?,?,?)",doc,p.orden(),p.producto(),p.unidad(),p.descripcion(),p.cantidad(),p.precio(),p.importe(),p.descuento());
        for(var t:x.impuestos())jdbc.update("INSERT INTO compra_cfdi_impuesto(documento_id,concepto_orden,tipo,impuesto,factor,tasa,base,importe) VALUES (?,?,?,?,?,?,?,?)",doc,t.concepto(),t.tipo(),t.codigo(),t.factor(),t.tasa(),t.base(),t.importe());
        for(var r:x.relaciones())jdbc.update("INSERT INTO compra_cfdi_relacion(documento_id,tipo,uuid) VALUES (?,?,?)",doc,r.tipo(),r.uuid());
    }
    private List<Respaldo> historial(Long id){return jdbc.query("SELECT id,tipo,justificacion,creado_en FROM compra_respaldo WHERE compra_id=? ORDER BY id DESC",(r,n)->new Respaldo(r.getLong(1),r.getString(2),r.getString(3),r.getTimestamp(4).toInstant()),id);}
    private List<Documento> documentos(Long id){return jdbc.query("SELECT d.*,a.nombre_original,a.tamano_bytes,x.uuid FROM compra_documento d JOIN archivo a ON a.id=d.archivo_id LEFT JOIN compra_cfdi x ON x.documento_id=d.id WHERE d.compra_id=? ORDER BY d.id DESC",(r,n)->new Documento(r.getLong("id"),r.getString("tipo"),r.getString("descripcion"),r.getString("nombre_original"),r.getLong("tamano_bytes"),r.getTimestamp("creado_en").toInstant(),r.getTimestamp("retirado_en")==null?null:r.getTimestamp("retirado_en").toInstant(),r.getString("motivo_retiro"),r.getString("uuid"),r.getString("justificacion")),id);}
    private Documento documento(Long id,Long doc){return documentos(id).stream().filter(d->d.id().equals(doc)).findFirst().orElseThrow(()->new RecursoNoEncontradoException("el comprobante",doc));}
    private Compra compra(Long id,boolean escritura,boolean bloquear) {
        permiso(escritura);
        Compra c=(bloquear?compras.findByIdForUpdate(id):compras.findById(id)).orElseThrow(()->new RecursoNoEncontradoException("la compra",id));
        alcance.validarInstitucion(c.getInstitucion().getId());
        if(c.getPlantel()==null)alcance.validarAdministracionInstitucional(c.getInstitucion().getId());else alcance.validarPlantel(c.getPlantel().getId());
        if(escritura&&c.getEstado()==EstadoCompra.CANCELADA)throw regla("El expediente de una compra cancelada sólo se consulta");return c;
    }
    private static void permiso(boolean escritura){var a=SecurityContextHolder.getContext().getAuthentication();if(a==null||a.getAuthorities().stream().noneMatch(p->escritura?p.getAuthority().equals("COMPRA_ADMINISTRAR"):Set.of("COMPRA_LEER","COMPRA_ADMINISTRAR","COMPRA_CONFIRMAR","COMPRA_CANCELAR").contains(p.getAuthority())))throw new AccessDeniedException("No tienes permiso para gestionar este expediente");}
    private static Long actor(){var a=SecurityContextHolder.getContext().getAuthentication();if(a==null||!(a.getPrincipal() instanceof UsuarioPrincipal p)||p.usuarioId()==null)throw new AccessDeniedException("Se requiere un usuario identificado");return p.usuarioId();}
    private static void version(Compra c,Long v){if(v==null||!Objects.equals(c.getVersion(),v))throw regla("La compra cambió. Recarga y revisa el expediente antes de guardar");}
    private static void confirmado(boolean v){if(!v)throw regla("Revisa y confirma la operación");}
    private static String texto(String s,int max){String r=s==null?"":s.trim();if(r.length()>max)throw regla("El texto supera el máximo de "+max+" caracteres");return r;}
    private static String nombre(String s){String r=Objects.toString(s,"comprobante").replace('\\','/');r=r.substring(r.lastIndexOf('/')+1).replaceAll("[\\p{Cntrl}]","").trim();return r.isBlank()?"comprobante":r.substring(0,Math.min(255,r.length()));}
    private static String sha(byte[] b){try{return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(b));}catch(NoSuchAlgorithmException e){throw new IllegalStateException(e);}}
    private static ReglaNegocioException regla(String s){return new ReglaNegocioException(s);}
}
