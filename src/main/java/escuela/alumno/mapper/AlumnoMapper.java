package escuela.alumno.mapper;

import escuela.alumno.dto.request.AlumnoRequest;
import escuela.alumno.dto.response.AlumnoResponse;
import escuela.alumno.entity.Alumno;
import escuela.institucion.entity.Institucion;
import org.springframework.stereotype.Component;

import static escuela.common.mapper.AuditoriaMapper.desde;
import static escuela.common.mapper.NormalizacionTexto.codigo;
import static escuela.common.mapper.NormalizacionTexto.email;
import static escuela.common.mapper.NormalizacionTexto.limpiar;

@Component
public class AlumnoMapper {

    public Alumno nuevo(AlumnoRequest dto, Institucion institucion) {
        Alumno entidad = new Alumno();
        actualizar(entidad, dto, institucion);
        return entidad;
    }

    public void actualizar(Alumno entidad, AlumnoRequest dto, Institucion institucion) {
        entidad.setInstitucion(institucion);
        entidad.setMatricula(codigo(dto.matricula()));
        entidad.setNombres(limpiar(dto.nombres()));
        entidad.setPrimerApellido(limpiar(dto.primerApellido()));
        entidad.setSegundoApellido(limpiar(dto.segundoApellido()));
        entidad.setCurp(codigo(dto.curp()));
        entidad.setFechaNacimiento(dto.fechaNacimiento());
        entidad.setSexo(limpiar(dto.sexo()));
        entidad.setLugarNacimiento(limpiar(dto.lugarNacimiento()));
        entidad.setNacionalidad(limpiar(dto.nacionalidad()));
        entidad.setTelefono(limpiar(dto.telefono()));
        entidad.setEmail(email(dto.email()));
        entidad.setCalle(limpiar(dto.calle()));
        entidad.setNumeroExterior(limpiar(dto.numeroExterior()));
        entidad.setNumeroInterior(limpiar(dto.numeroInterior()));
        entidad.setColonia(limpiar(dto.colonia()));
        entidad.setCiudad(limpiar(dto.ciudad()));
        entidad.setEstado(limpiar(dto.estado()));
        entidad.setCodigoPostal(limpiar(dto.codigoPostal()));
        entidad.setPais(codigo(dto.pais()));
        entidad.setFechaIngreso(dto.fechaIngreso());
        entidad.setObservaciones(limpiar(dto.observaciones()));
        entidad.setActivo(dto.activo());
    }

    public AlumnoResponse respuesta(Alumno e) {
        return new AlumnoResponse(e.getId(), e.getInstitucion().getId(), e.getMatricula(),
                e.getNombres(), e.getPrimerApellido(), e.getSegundoApellido(), e.getCurp(),
                e.getFechaNacimiento(), e.getSexo(), e.getLugarNacimiento(), e.getNacionalidad(),
                e.getTelefono(), e.getEmail(), e.getCalle(), e.getNumeroExterior(),
                e.getNumeroInterior(), e.getColonia(), e.getCiudad(), e.getEstado(),
                e.getCodigoPostal(), e.getPais(), e.getFechaIngreso(), e.getObservaciones(),
                e.isActivo(), e.getFotografiaArchivo() == null ? null : e.getFotografiaArchivo().getId(),
                desde(e));
    }
}
