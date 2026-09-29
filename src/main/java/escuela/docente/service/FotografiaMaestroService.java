package escuela.docente.service;

import escuela.archivo.dto.ArchivoDescarga;
import escuela.archivo.entity.Archivo;
import escuela.archivo.entity.EstadoArchivo;
import escuela.archivo.repository.ArchivoRepository;
import escuela.archivo.storage.AlmacenamientoArchivo;
import escuela.common.exception.RecursoNoEncontradoException;
import escuela.common.exception.ReglaNegocioException;
import escuela.docente.dto.FotografiaMaestroResponse;
import escuela.docente.entity.Maestro;
import escuela.docente.repository.MaestroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.io.*;
import java.security.*;
import java.util.*;

@Service @RequiredArgsConstructor @Transactional
public class FotografiaMaestroService {
    private static final long MAXIMO=5L*1024*1024, PIXELES_MAXIMOS=25_000_000L;
    private final MaestroRepository maestros; private final ArchivoRepository archivos;
    private final AlmacenamientoArchivo almacenamiento;

    public FotografiaMaestroResponse asignar(Long maestroId,MultipartFile foto){
        String tipo=validar(foto); Maestro m=bloquear(maestroId);if(!m.isActivo())throw new ReglaNegocioException("No se puede cambiar la fotografía de un maestro inactivo");
        String extension=tipo.equals("image/png")?"png":"jpg";String clave=m.getInstitucion().getId()+"/maestros/"+maestroId+"/"+UUID.randomUUID()+".bin";
        try(InputStream in=foto.getInputStream()){almacenamiento.guardar(clave,in);}catch(IOException e){throw new ReglaNegocioException("No fue posible leer la fotografía seleccionada");}
        try{
            Archivo anterior=m.getFotografiaArchivo(); Archivo a=new Archivo();a.setInstitucion(m.getInstitucion());a.setClaveAlmacenamiento(clave);a.setNombreOriginal(nombre(foto.getOriginalFilename(),extension));a.setTipoMime(tipo);a.setTamanoBytes(foto.getSize());a.setChecksumSha256(checksum(foto));a.setEstado(EstadoArchivo.DISPONIBLE);a=archivos.saveAndFlush(a);m.setFotografiaArchivo(a);maestros.saveAndFlush(m);if(anterior!=null){anterior.setEstado(EstadoArchivo.RETIRADO);archivos.save(anterior);}return respuesta(a);
        }catch(RuntimeException e){almacenamiento.eliminarSiExiste(clave);throw e;}
    }
    public void retirar(Long id){Maestro m=bloquear(id);Archivo a=m.getFotografiaArchivo();if(a==null)throw new ReglaNegocioException("El maestro no tiene una fotografía actual");m.setFotografiaArchivo(null);a.setEstado(EstadoArchivo.RETIRADO);maestros.saveAndFlush(m);archivos.save(a);}
    @Transactional(readOnly=true) public FotografiaMaestroResponse actual(Long id){Maestro m=buscar(id);return m.getFotografiaArchivo()==null?null:respuesta(m.getFotografiaArchivo());}
    @Transactional(readOnly=true) public ArchivoDescarga descargar(Long id){Maestro m=buscar(id);Archivo a=m.getFotografiaArchivo();if(a==null||a.getEstado()!=EstadoArchivo.DISPONIBLE)throw new RecursoNoEncontradoException("la fotografía del maestro",id);return new ArchivoDescarga(almacenamiento.abrir(a.getClaveAlmacenamiento()),a.getNombreOriginal(),a.getTipoMime(),a.getTamanoBytes());}
    private String validar(MultipartFile f){if(f==null||f.isEmpty())throw new ReglaNegocioException("Selecciona una fotografía");if(f.getSize()>MAXIMO)throw new ReglaNegocioException("La fotografía no puede superar 5 MB");try(InputStream in=f.getInputStream()){byte[] h=in.readNBytes(8);String tipo=h.length>=3&&(h[0]&255)==255&&(h[1]&255)==216&&(h[2]&255)==255?"image/jpeg":Arrays.equals(h,new byte[]{(byte)137,80,78,71,13,10,26,10})?"image/png":null;if(tipo==null)throw invalida();try(InputStream in2=f.getInputStream()){var imagen=ImageIO.read(in2);if(imagen==null||imagen.getWidth()<=0||imagen.getHeight()<=0||(long)imagen.getWidth()*imagen.getHeight()>PIXELES_MAXIMOS)throw invalida();}return tipo;}catch(IOException e){throw invalida();}}
    private String checksum(MultipartFile f){try(InputStream in=f.getInputStream()){MessageDigest d=MessageDigest.getInstance("SHA-256");byte[] b=new byte[8192];for(int n;(n=in.read(b))>=0;)if(n>0)d.update(b,0,n);return HexFormat.of().formatHex(d.digest());}catch(IOException|NoSuchAlgorithmException e){throw new ReglaNegocioException("No fue posible verificar la fotografía");}}
    private String nombre(String original,String ext){String n=original==null?"fotografia."+ext:original.replace('\\','/');n=n.substring(n.lastIndexOf('/')+1).replaceAll("[\\p{Cntrl}]","").trim();if(n.isBlank())n="fotografia."+ext;return n.length()>255?n.substring(n.length()-255):n;}
    private Maestro buscar(Long id){return maestros.findById(id).orElseThrow(()->new RecursoNoEncontradoException("el maestro",id));}private Maestro bloquear(Long id){return maestros.findByIdForUpdate(id).orElseThrow(()->new RecursoNoEncontradoException("el maestro",id));}
    private FotografiaMaestroResponse respuesta(Archivo a){return new FotografiaMaestroResponse(a.getId(),a.getNombreOriginal(),a.getTipoMime(),a.getTamanoBytes());}private ReglaNegocioException invalida(){return new ReglaNegocioException("La fotografía debe ser un JPEG o PNG válido y seguro");}
}
