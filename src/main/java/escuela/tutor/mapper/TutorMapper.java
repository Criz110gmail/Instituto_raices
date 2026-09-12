package escuela.tutor.mapper;

import escuela.institucion.entity.Institucion;
import escuela.seguridad.entity.Usuario;
import escuela.tutor.dto.request.TutorRequest;
import escuela.tutor.dto.response.TutorResponse;
import escuela.tutor.entity.Tutor;
import org.springframework.stereotype.Component;

import static escuela.common.mapper.AuditoriaMapper.desde;
import static escuela.common.mapper.NormalizacionTexto.codigo;
import static escuela.common.mapper.NormalizacionTexto.email;
import static escuela.common.mapper.NormalizacionTexto.limpiar;

@Component
public class TutorMapper {

    public Tutor nuevo(TutorRequest dto, Institucion institucion, Usuario usuario) {
        Tutor entidad = new Tutor();
        actualizar(entidad, dto, institucion, usuario);
        return entidad;
    }

    public void actualizar(Tutor entidad, TutorRequest dto, Institucion institucion, Usuario usuario) {
        entidad.setInstitucion(institucion);
        entidad.setUsuario(usuario);
        entidad.setNombres(limpiar(dto.nombres()));
        entidad.setPrimerApellido(limpiar(dto.primerApellido()));
        entidad.setSegundoApellido(limpiar(dto.segundoApellido()));
        entidad.setTelefonoPrincipal(limpiar(dto.telefonoPrincipal()));
        entidad.setTelefonoSecundario(limpiar(dto.telefonoSecundario()));
        entidad.setEmail(email(dto.email()));
        entidad.setFechaNacimiento(dto.fechaNacimiento());
        entidad.setCalle(limpiar(dto.calle()));
        entidad.setNumeroExterior(limpiar(dto.numeroExterior()));
        entidad.setNumeroInterior(limpiar(dto.numeroInterior()));
        entidad.setColonia(limpiar(dto.colonia()));
        entidad.setCiudad(limpiar(dto.ciudad()));
        entidad.setEstado(limpiar(dto.estado()));
        entidad.setCodigoPostal(limpiar(dto.codigoPostal()));
        entidad.setPais(codigo(dto.pais()));
        entidad.setOcupacion(limpiar(dto.ocupacion()));
        entidad.setLugarTrabajo(limpiar(dto.lugarTrabajo()));
        entidad.setTelefonoTrabajo(limpiar(dto.telefonoTrabajo()));
        entidad.setActivo(dto.activo());
    }

    public TutorResponse respuesta(Tutor e) {
        Usuario usuario = e.getUsuario();
        return new TutorResponse(e.getId(), e.getInstitucion().getId(),
                usuario == null ? null : usuario.getId(), usuario == null ? null : usuario.getUsername(),
                e.getNombres(), e.getPrimerApellido(), e.getSegundoApellido(),
                e.getTelefonoPrincipal(), e.getTelefonoSecundario(), e.getEmail(),
                e.getFechaNacimiento(), e.getCalle(), e.getNumeroExterior(),
                e.getNumeroInterior(), e.getColonia(), e.getCiudad(), e.getEstado(),
                e.getCodigoPostal(), e.getPais(), e.getOcupacion(), e.getLugarTrabajo(),
                e.getTelefonoTrabajo(), e.isActivo(), desde(e));
    }
}
