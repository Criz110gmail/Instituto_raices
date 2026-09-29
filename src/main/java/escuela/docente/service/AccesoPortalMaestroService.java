package escuela.docente.service;

import escuela.common.exception.*;
import escuela.docente.dto.*;
import escuela.docente.entity.Maestro;
import escuela.docente.repository.MaestroRepository;
import escuela.seguridad.entity.*;
import escuela.seguridad.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.text.Normalizer;
import java.util.*;
import static escuela.common.mapper.NormalizacionTexto.*;
import static escuela.common.service.ValidacionVersion.verificar;

@Service @RequiredArgsConstructor @Transactional
public class AccesoPortalMaestroService {
    private final MaestroRepository maestros; private final UsuarioRepository usuarios;
    @Transactional(readOnly=true) public Optional<PortalMaestroCuentaResponse> obtener(Long id){return Optional.ofNullable(maestro(id).getUsuario()).map(this::respuesta);}
    @Transactional(readOnly=true) public String sugerirUsername(Long id){Maestro m=maestro(id);String base=segmento(m.getNombres())+"."+segmento(m.getPrimerApellido());if(base.equals("."))base="maestro";String c=limitar(base,80);int s=2;while(usuarios.existsByInstitucionIdAndUsernameIgnoreCaseAndIdNot(m.getInstitucion().getId(),c,0L)){String x=String.valueOf(s++);c=limitar(base,80-x.length())+x;}return c;}
    public PortalMaestroCuentaResponse crear(Long id,PortalMaestroCuentaRequest r){Maestro m=maestros.findByIdForUpdate(id).orElseThrow(()->new RecursoNoEncontradoException("el maestro",id));if(!m.isActivo())throw new ReglaNegocioException("El maestro debe estar activo");if(m.getUsuario()!=null)throw new RecursoDuplicadoException("El maestro ya tiene una cuenta de acceso");String user=limpiar(r.username()),mail=email(r.email());Long inst=m.getInstitucion().getId();if(usuarios.existsByInstitucionIdAndUsernameIgnoreCaseAndIdNot(inst,user,0L))throw new RecursoDuplicadoException("Ese nombre de usuario ya existe");if(usuarios.existsByInstitucionIdAndEmailIgnoreCaseAndIdNot(inst,mail,0L))throw new RecursoDuplicadoException("Ese correo ya pertenece a otra cuenta");Usuario u=new Usuario();u.setInstitucion(m.getInstitucion());u.setUsername(user);u.setEmail(mail);u.setTipoCuenta(TipoCuentaUsuario.PORTAL_MAESTRO);u.setEstado(EstadoUsuario.INVITADO);usuarios.saveAndFlush(u);m.setUsuario(u);maestros.saveAndFlush(m);return respuesta(u);}
    public PortalMaestroCuentaResponse cambiarDisponibilidad(Long id,Long version,boolean activar){Maestro m=maestros.findByIdForUpdate(id).orElseThrow(()->new RecursoNoEncontradoException("el maestro",id));Usuario u=cuenta(m);verificar(u,version,"Cuenta del maestro");if(activar&&!m.isActivo())throw new ReglaNegocioException("Activa primero al maestro");u.setEstado(activar?(u.getPasswordHash()==null?EstadoUsuario.INVITADO:EstadoUsuario.ACTIVO):EstadoUsuario.INACTIVO);u.setBloqueoHasta(null);u.setIntentosFallidos(0);return respuesta(usuarios.saveAndFlush(u));}
    private Maestro maestro(Long id){return maestros.findById(id).orElseThrow(()->new RecursoNoEncontradoException("el maestro",id));}
    private Usuario cuenta(Maestro m){if(m.getUsuario()==null||m.getUsuario().getTipoCuenta()!=TipoCuentaUsuario.PORTAL_MAESTRO)throw new ReglaNegocioException("El maestro no tiene una cuenta del portal");return m.getUsuario();}
    private PortalMaestroCuentaResponse respuesta(Usuario u){return new PortalMaestroCuentaResponse(u.getId(),u.getUsername(),u.getEmail(),u.getEstado(),u.getPasswordHash()!=null,u.getVersion());}
    private String segmento(String t){if(t==null)return"";return Normalizer.normalize(t.trim().split("\\s+")[0],Normalizer.Form.NFD).replaceAll("\\p{M}","").toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9_-]","");}
    private String limitar(String t,int l){return t.length()<=l?t:t.substring(0,l);}
}
