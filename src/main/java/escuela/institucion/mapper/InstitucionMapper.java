package escuela.institucion.mapper;

import escuela.institucion.dto.request.InstitucionRequest;
import escuela.institucion.dto.response.InstitucionResponse;
import escuela.institucion.entity.Institucion;
import org.springframework.stereotype.Component;

import static escuela.common.mapper.AuditoriaMapper.desde;
import static escuela.common.mapper.NormalizacionTexto.codigo;
import static escuela.common.mapper.NormalizacionTexto.email;
import static escuela.common.mapper.NormalizacionTexto.limpiar;

@Component
public class InstitucionMapper {

    public Institucion nueva(InstitucionRequest dto) {
        Institucion entidad = new Institucion();
        actualizar(entidad, dto);
        return entidad;
    }

    public void actualizar(Institucion entidad, InstitucionRequest dto) {
        entidad.setCodigo(codigo(dto.codigo()));
        entidad.setNombre(limpiar(dto.nombre()));
        entidad.setNombreComercial(limpiar(dto.nombreComercial()));
        entidad.setRazonSocial(limpiar(dto.razonSocial()));
        entidad.setRfc(codigo(dto.rfc()));
        entidad.setEmail(email(dto.email()));
        entidad.setTelefono(limpiar(dto.telefono()));
        entidad.setSitioWeb(limpiar(dto.sitioWeb()));
        entidad.setDomicilioFiscal(limpiar(dto.domicilioFiscal()));
        entidad.setCiudad(limpiar(dto.ciudad()));
        entidad.setEstado(limpiar(dto.estado()));
        entidad.setCodigoPostal(limpiar(dto.codigoPostal()));
        entidad.setPais(codigo(dto.pais()));
        entidad.setZonaHoraria(limpiar(dto.zonaHoraria()));
        entidad.setMonedaPredeterminada(codigo(dto.monedaPredeterminada()));
        entidad.setActivo(dto.activo());
    }

    public InstitucionResponse respuesta(Institucion e) {
        return new InstitucionResponse(e.getId(), e.getCodigo(), e.getNombre(), e.getNombreComercial(),
                e.getRazonSocial(), e.getRfc(), e.getEmail(), e.getTelefono(), e.getSitioWeb(),
                e.getDomicilioFiscal(), e.getCiudad(), e.getEstado(), e.getCodigoPostal(), e.getPais(),
                e.getLogoArchivoId(), e.getZonaHoraria(), e.getMonedaPredeterminada(), e.isActivo(), desde(e));
    }
}
