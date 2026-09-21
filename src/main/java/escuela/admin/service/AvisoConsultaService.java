package escuela.admin.service;

import escuela.admin.dto.*;
import escuela.comunicacion.entity.*;
import escuela.comunicacion.repository.AvisoRepository;
import escuela.common.exception.ReglaNegocioException;
import escuela.institucion.service.InstitucionService;
import escuela.seguridad.service.AlcanceDatosService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service @RequiredArgsConstructor @Transactional(readOnly=true)
public class AvisoConsultaService {
    private static final DateTimeFormatter F=DateTimeFormatter.ofPattern("dd MMM yyyy · HH:mm",new Locale("es","MX"));
    private final AvisoRepository repository; private final InstitucionService instituciones; private final AlcanceDatosService alcance;
    public Page<AvisoFila> consultar(FiltroAviso original){ FiltroAviso f=original.normalizado();
        if(f.institucionId()==null) throw new ReglaNegocioException("Selecciona una institución");
        alcance.validarInstitucion(f.institucionId()); if(f.plantelId()!=null) alcance.validarPlantel(f.plantelId());
        ZoneId zona=ZoneId.of(instituciones.obtener(f.institucionId()).zonaHoraria());
        Specification<Aviso> s=(r,q,c)->c.equal(r.get("institucion").get("id"),f.institucionId());
        s=s.and(alcance.especificacion(ModuloCatalogo.AVISOS));
        if(f.plantelId()!=null)s=s.and((r,q,c)->c.equal(r.get("plantel").get("id"),f.plantelId()));
        if(!f.texto().isBlank()){String p="%"+f.texto().toLowerCase(Locale.ROOT)+"%";s=s.and((r,q,c)->c.or(c.like(c.lower(r.get("titulo")),p),c.like(c.lower(r.get("contenido")),p)));}
        if(!f.estado().equals("TODOS"))s=s.and((r,q,c)->c.equal(r.get("estado"),EstadoAviso.valueOf(f.estado())));
        return repository.findAll(s,PageRequest.of(f.pagina(),f.tamanio(),Sort.by(Sort.Order.desc("actualizadoEn"),Sort.Order.desc("id"))))
                .map(a->new AvisoFila(a.getId(),a.getTitulo(),a.getInstitucion().getNombre(),a.getPlantel()==null?"Toda la institución":a.getPlantel().getNombre(),a.getEstado().name(),formato(a.getPublicadoEn(),zona),formato(a.getExpiraEn(),zona)));
    }
    private String formato(Instant i,ZoneId z){return i==null?"—":F.withZone(z).format(i);}
}
