package escuela.compras.service;

import escuela.compras.dto.*;
import escuela.compras.entity.Proveedor;
import escuela.compras.repository.ProveedorRepository;
import escuela.common.exception.*;
import escuela.institucion.repository.InstitucionRepository;
import escuela.seguridad.service.AlcanceDatosService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import static escuela.common.mapper.NormalizacionTexto.*;
import static escuela.common.service.ValidacionVersion.verificar;

@Service @RequiredArgsConstructor @Transactional
public class ProveedorService {
    private final ProveedorRepository repository; private final InstitucionRepository instituciones; private final AlcanceDatosService alcance;
    public Long crear(ProveedorForm f){var i=instituciones.findById(f.getInstitucionId()).orElseThrow(()->new RecursoNoEncontradoException("la institución",f.getInstitucionId()));alcance.validarInstitucion(i.getId());validarRfc(i.getId(),f.getRfc(),0L);Proveedor p=new Proveedor();p.setInstitucion(i);copiar(p,f);return repository.saveAndFlush(p).getId();}
    public void actualizar(Long id,ProveedorForm f){Proveedor p=bloquear(id);alcance.validarInstitucion(p.getInstitucion().getId());verificar(p,f.getVersion(),"Proveedor");if(!p.getInstitucion().getId().equals(f.getInstitucionId()))throw new ReglaNegocioException("No se puede cambiar la institución del proveedor");validarRfc(p.getInstitucion().getId(),f.getRfc(),id);copiar(p,f);repository.saveAndFlush(p);}
    @Transactional(readOnly=true) public ProveedorForm formulario(Long id){Proveedor p=obtener(id);alcance.validarInstitucion(p.getInstitucion().getId());ProveedorForm f=new ProveedorForm();f.setInstitucionId(p.getInstitucion().getId());f.setRazonSocial(p.getRazonSocial());f.setNombreComercial(p.getNombreComercial());f.setRfc(p.getRfc());f.setContactoNombre(p.getContactoNombre());f.setTelefono(p.getTelefono());f.setCorreo(p.getCorreo());f.setDireccion(p.getDireccion());f.setNotas(p.getNotas());f.setActivo(p.isActivo());f.setVersion(p.getVersion());return f;}
    @Transactional(readOnly=true) public Page<ProveedorFila> listar(FiltroProveedor entrada){FiltroProveedor f=entrada.normalizado();if(f.institucionId()==null)throw new ReglaNegocioException("Selecciona una institución");alcance.validarInstitucion(f.institucionId());Specification<Proveedor>s=(r,q,cb)->cb.equal(r.get("institucion").get("id"),f.institucionId());if(f.activo()!=null)s=s.and((r,q,cb)->cb.equal(r.get("activo"),f.activo()));if(!f.texto().isBlank()){String p="%"+f.texto().toLowerCase(Locale.ROOT)+"%";s=s.and((r,q,cb)->cb.or(cb.like(cb.lower(r.get("razonSocial")),p),cb.like(cb.lower(cb.coalesce(r.get("nombreComercial"),"")),p),cb.like(cb.lower(cb.coalesce(r.get("rfc"),"")),p),cb.like(cb.lower(cb.coalesce(r.get("contactoNombre"),"")),p)));}return repository.findAll(s,PageRequest.of(f.pagina(),f.tamanio(),Sort.by("razonSocial").ascending().and(Sort.by("id")))).map(this::fila);}
    @Transactional(readOnly=true) public List<ProveedorFila> activos(Long institucionId){if(institucionId==null)return List.of();alcance.validarInstitucion(institucionId);return repository.findAllByInstitucionIdAndActivoTrueOrderByRazonSocialAsc(institucionId).stream().map(this::fila).toList();}
    private void copiar(Proveedor p,ProveedorForm f){p.setRazonSocial(limpiar(f.getRazonSocial()));p.setNombreComercial(limpiar(f.getNombreComercial()));p.setRfc(rfc(f.getRfc()));p.setContactoNombre(limpiar(f.getContactoNombre()));p.setTelefono(limpiar(f.getTelefono()));String correo=limpiar(f.getCorreo());p.setCorreo(correo==null?null:correo.toLowerCase(Locale.ROOT));p.setDireccion(limpiar(f.getDireccion()));p.setNotas(limpiar(f.getNotas()));p.setActivo(f.isActivo());}
    private String rfc(String r){String x=limpiar(r);return x==null?null:x.toUpperCase(Locale.ROOT);}
    private void validarRfc(Long i,String r,Long id){String x=rfc(r);if(x!=null&&repository.existsByInstitucionIdAndRfcIgnoreCaseAndIdNot(i,x,id))throw new RecursoDuplicadoException("Ya existe un proveedor con ese RFC en la institución");}
    private Proveedor bloquear(Long id){return repository.findByIdForUpdate(id).orElseThrow(()->new RecursoNoEncontradoException("el proveedor",id));}
    private Proveedor obtener(Long id){return repository.findById(id).orElseThrow(()->new RecursoNoEncontradoException("el proveedor",id));}
    private ProveedorFila fila(Proveedor p){return new ProveedorFila(p.getId(),p.getInstitucion().getId(),p.getRazonSocial(),p.getNombreComercial(),p.getRfc(),p.getContactoNombre(),p.getTelefono(),p.getCorreo(),p.isActivo(),p.getVersion());}
}
