package escuela.alumno.service;

import escuela.admin.dto.ModuloCatalogo;
import escuela.alumno.dto.*;
import escuela.alumno.entity.*;
import escuela.alumno.repository.*;
import escuela.archivo.dto.ArchivoDescarga;
import escuela.archivo.entity.*;
import escuela.archivo.imagen.*;
import escuela.archivo.repository.ArchivoRepository;
import escuela.archivo.storage.AlmacenamientoArchivo;
import escuela.common.exception.*;
import escuela.seguridad.repository.UsuarioRepository;
import escuela.seguridad.service.*;
import escuela.tutor.entity.Tutor;
import escuela.tutor.repository.TutorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.security.*;
import java.time.*;
import java.util.*;

import static escuela.common.mapper.NormalizacionTexto.limpiar;

@Service
@RequiredArgsConstructor
@Transactional
public class ActualizacionExpedienteFamiliarService {
    public static final String CONSENTIMIENTO_VERSION = "EXPEDIENTE_FAMILIAR_V1";
    public static final String CONSENTIMIENTO_TEXTO = "Confirmo que la información y los documentos enviados son correctos, que cuento con autorización para compartirlos con la institución y que serán revisados antes de incorporarse al expediente oficial del alumno.";
    private static final long PDF_MAXIMO = 10L * 1024 * 1024;

    private final ActualizacionExpedienteFamiliarRepository solicitudes;
    private final AlumnoRepository alumnos;
    private final AlumnoTutorRepository vinculos;
    private final TutorRepository tutores;
    private final ArchivoRepository archivos;
    private final AlumnoDocumentoRepository documentos;
    private final FichaMedicaAlumnoRepository fichas;
    private final UsuarioRepository usuarios;
    private final AlmacenamientoArchivo almacenamiento;
    private final ProcesadorFotografia procesadorImagen;
    private final AlcanceDatosService alcance;

    public Long proponerDocumento(UsuarioPrincipal principal, Long alumnoId, PortalDocumentoPropuestaForm form) {
        ContextoPortal contexto = validarPortal(principal, alumnoId);
        if (!form.isConsentimiento()) throw new ReglaNegocioException("Debes aceptar el consentimiento para enviar la propuesta");
        if (form.getTipoDocumento() == null) throw new ReglaNegocioException("Selecciona el tipo de documento");
        if (form.getVigenteHasta() != null && form.getFechaDocumento() != null
                && form.getVigenteHasta().isBefore(form.getFechaDocumento())) {
            throw new ReglaNegocioException("La vigencia no puede ser anterior a la fecha del documento");
        }
        ArchivoPreparado preparado = prepararDocumento(form.getArchivo());
        String clave = contexto.alumno().getInstitucion().getId() + "/alumnos/" + alumnoId
                + "/propuestas-familiares/" + UUID.randomUUID() + ".bin";
        try (InputStream contenido = new ByteArrayInputStream(preparado.contenido())) {
            almacenamiento.guardar(clave, contenido);
        } catch (IOException excepcion) {
            throw new ReglaNegocioException("No fue posible guardar el documento propuesto");
        }
        try {
            Archivo archivo = archivo(contexto.alumno(), clave, preparado);
            ActualizacionExpedienteFamiliar solicitud = base(contexto, TipoActualizacionExpediente.DOCUMENTO,
                    form.getMensajeTutor());
            solicitud.setArchivo(archivo);
            solicitud.setTipoDocumento(form.getTipoDocumento());
            solicitud.setDescripcionDocumento(limpiar(form.getDescripcion()));
            solicitud.setFechaDocumento(form.getFechaDocumento());
            solicitud.setVigenteHasta(form.getVigenteHasta());
            return solicitudes.saveAndFlush(solicitud).getId();
        } catch (RuntimeException excepcion) {
            almacenamiento.eliminarSiExiste(clave);
            throw excepcion;
        }
    }

    public Long proponerFichaMedica(UsuarioPrincipal principal, Long alumnoId,
                                     PortalFichaMedicaPropuestaForm form) {
        ContextoPortal contexto = validarPortal(principal, alumnoId);
        if (!form.isConsentimiento()) throw new ReglaNegocioException("Debes aceptar el consentimiento para enviar la propuesta");
        if (form.getTipoSanguineo() == null) throw new ReglaNegocioException("Selecciona el tipo sanguíneo o indica que no se conoce");
        if (solicitudes.existsByAlumnoIdAndTipoAndEstado(alumnoId, TipoActualizacionExpediente.FICHA_MEDICA,
                EstadoActualizacionExpediente.ENVIADA)) {
            throw new ReglaNegocioException("Ya existe una propuesta médica pendiente para este alumno. Espera la respuesta de administración");
        }
        ActualizacionExpedienteFamiliar solicitud = base(contexto, TipoActualizacionExpediente.FICHA_MEDICA,
                form.getMensajeTutor());
        copiarMedica(solicitud, form);
        return solicitudes.saveAndFlush(solicitud).getId();
    }

    @Transactional(readOnly = true)
    public PortalFichaMedicaPropuestaForm formularioMedico(UsuarioPrincipal principal, Long alumnoId) {
        validarPortal(principal, alumnoId);
        PortalFichaMedicaPropuestaForm form = new PortalFichaMedicaPropuestaForm();
        fichas.findByAlumnoId(alumnoId).ifPresent(f -> {
            form.setTipoSanguineo(f.getTipoSanguineo()); form.setAlergias(f.getAlergias());
            form.setPadecimientos(f.getPadecimientos()); form.setMedicamentos(f.getMedicamentos());
            form.setDiscapacidadNecesidades(f.getDiscapacidadNecesidades());
            form.setRestriccionesFisicas(f.getRestriccionesFisicas());
            form.setRestriccionesAlimentarias(f.getRestriccionesAlimentarias());
            form.setServicioMedico(f.getServicioMedico()); form.setNumeroAfiliacion(f.getNumeroAfiliacion());
            form.setMedicoTratante(f.getMedicoTratante()); form.setContactoEmergencia(f.getContactoEmergencia());
            form.setTelefonoEmergencia(f.getTelefonoEmergencia()); form.setObservaciones(f.getObservaciones());
            form.setAutorizaAtencionEmergencia(f.isAutorizaAtencionEmergencia());
        });
        if (form.getTipoSanguineo() == null) form.setTipoSanguineo(TipoSanguineo.DESCONOCIDO);
        return form;
    }

    @Transactional(readOnly = true)
    public Page<ActualizacionExpedienteFila> listarPortal(UsuarioPrincipal principal, Long alumnoId, int pagina) {
        ContextoPortal contexto = validarPortal(principal, alumnoId);
        Specification<ActualizacionExpedienteFamiliar> spec = (r, q, cb) -> cb.and(
                cb.equal(r.get("tutor").get("id"), contexto.tutor().getId()),
                cb.equal(r.get("alumno").get("id"), alumnoId));
        return solicitudes.findAll(spec, PageRequest.of(Math.max(0, pagina), 10,
                Sort.by(Sort.Order.desc("creadoEn"), Sort.Order.desc("id")))).map(this::fila);
    }

    @Transactional(readOnly = true)
    public ArchivoDescarga archivoPortal(UsuarioPrincipal principal, Long alumnoId, Long id) {
        ContextoPortal contexto = validarPortal(principal, alumnoId);
        ActualizacionExpedienteFamiliar solicitud = solicitudes.findByIdAndTutorId(id, contexto.tutor().getId())
                .filter(s -> s.getAlumno().getId().equals(alumnoId))
                .orElseThrow(() -> new RecursoNoEncontradoException("la propuesta", id));
        return descarga(solicitud);
    }

    @Transactional(readOnly = true)
    public Page<ActualizacionExpedienteFila> listarAdministracion(FiltroActualizacionExpediente entrada) {
        FiltroActualizacionExpediente filtro = entrada.normalizado();
        if (filtro.institucionId() == null) throw new ReglaNegocioException("Selecciona una institución");
        alcance.validarAdministracionInstitucional(filtro.institucionId());
        if (filtro.desde() != null && filtro.hasta() != null && filtro.hasta().isBefore(filtro.desde())) {
            throw new ReglaNegocioException("La fecha final debe ser igual o posterior a la inicial");
        }
        Specification<ActualizacionExpedienteFamiliar> spec = (r, q, cb) ->
                cb.equal(r.get("institucion").get("id"), filtro.institucionId());
        if (filtro.alumnoId() != null) spec = spec.and((r,q,cb)->cb.equal(r.get("alumno").get("id"),filtro.alumnoId()));
        if (filtro.tipo() != null) spec = spec.and((r,q,cb)->cb.equal(r.get("tipo"),filtro.tipo()));
        if (filtro.estado() != null) spec = spec.and((r,q,cb)->cb.equal(r.get("estado"),filtro.estado()));
        if (filtro.desde() != null) { Instant d=filtro.desde().atStartOfDay(ZoneOffset.UTC).toInstant(); spec=spec.and((r,q,cb)->cb.greaterThanOrEqualTo(r.get("creadoEn"),d)); }
        if (filtro.hasta() != null) { Instant h=filtro.hasta().plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant(); spec=spec.and((r,q,cb)->cb.lessThan(r.get("creadoEn"),h)); }
        if (!filtro.texto().isBlank()) { String p="%"+filtro.texto().toLowerCase(Locale.ROOT)+"%"; spec=spec.and((r,q,cb)->cb.or(
                cb.like(cb.lower(r.get("alumno").get("nombres")),p),cb.like(cb.lower(r.get("alumno").get("primerApellido")),p),
                cb.like(cb.lower(r.get("alumno").get("matricula")),p),cb.like(cb.lower(r.get("tutor").get("nombres")),p),
                cb.like(cb.lower(r.get("tutor").get("primerApellido")),p))); }
        return solicitudes.findAll(spec, PageRequest.of(filtro.pagina(), filtro.tamanio(),
                Sort.by(Sort.Order.desc("creadoEn"),Sort.Order.desc("id")))).map(this::fila);
    }

    @Transactional(readOnly = true)
    public ActualizacionExpedienteDetalle detalleAdministracion(Long id) {
        ActualizacionExpedienteFamiliar solicitud = obtener(id);
        alcance.validarAdministracionInstitucional(solicitud.getInstitucion().getId());
        return detalle(solicitud);
    }

    @Transactional(readOnly = true)
    public ArchivoDescarga archivoAdministracion(Long id) {
        ActualizacionExpedienteFamiliar solicitud = obtener(id);
        alcance.validarAdministracionInstitucional(solicitud.getInstitucion().getId());
        return descarga(solicitud);
    }

    public void aprobar(Long id, Long version, String respuesta, UsuarioPrincipal principal) {
        ActualizacionExpedienteFamiliar solicitud = bloquearPendiente(id, version);
        alcance.validarAdministracionInstitucional(solicitud.getInstitucion().getId());
        if (solicitud.getTipo() == TipoActualizacionExpediente.DOCUMENTO) aplicarDocumento(solicitud);
        else aplicarFichaMedica(solicitud);
        resolver(solicitud, EstadoActualizacionExpediente.APROBADA, respuesta, principal);
    }

    public void rechazar(Long id, Long version, String respuesta, UsuarioPrincipal principal) {
        ActualizacionExpedienteFamiliar solicitud = bloquearPendiente(id, version);
        alcance.validarAdministracionInstitucional(solicitud.getInstitucion().getId());
        resolver(solicitud, EstadoActualizacionExpediente.RECHAZADA, respuesta, principal);
    }

    private ContextoPortal validarPortal(UsuarioPrincipal principal, Long alumnoId) {
        if (principal == null || principal.usuarioId() == null || principal.institucionId() == null) {
            throw new AccessDeniedException("El portal familiar requiere una sesión activa");
        }
        Tutor tutor = tutores.findByUsuarioIdAndInstitucionIdAndActivoTrue(principal.usuarioId(), principal.institucionId())
                .orElseThrow(() -> new AccessDeniedException("La cuenta no está vinculada con un tutor activo"));
        Alumno alumno = alumnos.findById(alumnoId).filter(Alumno::isActivo)
                .orElseThrow(() -> new RecursoNoEncontradoException("el alumno", alumnoId));
        if (!alumno.getInstitucion().getId().equals(principal.institucionId())) throw new AccessDeniedException("El alumno no pertenece a la institución");
        vinculos.vinculoAutorizadorVigente(alumnoId, tutor.getId(), LocalDate.now())
                .orElseThrow(() -> new AccessDeniedException("Este vínculo familiar no tiene autorización para proponer cambios al expediente"));
        return new ContextoPortal(tutor, alumno);
    }

    private ActualizacionExpedienteFamiliar base(ContextoPortal contexto, TipoActualizacionExpediente tipo, String mensaje) {
        ActualizacionExpedienteFamiliar solicitud = new ActualizacionExpedienteFamiliar();
        solicitud.setInstitucion(contexto.alumno().getInstitucion()); solicitud.setAlumno(contexto.alumno());
        solicitud.setTutor(contexto.tutor()); solicitud.setTipo(tipo); solicitud.setEstado(EstadoActualizacionExpediente.ENVIADA);
        solicitud.setMensajeTutor(limpiar(mensaje)); solicitud.setConsentimientoVersion(CONSENTIMIENTO_VERSION);
        solicitud.setConsentimientoTexto(CONSENTIMIENTO_TEXTO); solicitud.setConsentimientoEn(Instant.now());
        return solicitud;
    }

    private ArchivoPreparado prepararDocumento(MultipartFile archivo) {
        if (archivo == null || archivo.isEmpty()) throw new ReglaNegocioException("Selecciona un documento");
        try {
            byte[] contenido = archivo.getBytes();
            if (esPdf(contenido)) {
                if (contenido.length > PDF_MAXIMO) throw new ReglaNegocioException("El PDF no puede superar 10 MB");
                return new ArchivoPreparado(contenido, nombreSeguro(archivo.getOriginalFilename(), "pdf"),
                        "application/pdf", checksum(contenido));
            }
            ImagenOptimizada imagen = procesadorImagen.procesarDocumento(archivo);
            return new ArchivoPreparado(imagen.contenido(), imagen.nombreArchivo(), imagen.tipoMime(), imagen.checksumSha256());
        } catch (IOException excepcion) {
            throw new ReglaNegocioException("No fue posible leer el documento seleccionado");
        }
    }

    private Archivo archivo(Alumno alumno, String clave, ArchivoPreparado preparado) {
        Archivo archivo = new Archivo(); archivo.setInstitucion(alumno.getInstitucion()); archivo.setClaveAlmacenamiento(clave);
        archivo.setNombreOriginal(preparado.nombre()); archivo.setTipoMime(preparado.mime());
        archivo.setTamanoBytes((long) preparado.contenido().length); archivo.setChecksumSha256(preparado.checksum());
        archivo.setEstado(EstadoArchivo.DISPONIBLE); return archivos.saveAndFlush(archivo);
    }

    private void aplicarDocumento(ActualizacionExpedienteFamiliar solicitud) {
        AlumnoDocumento documento = new AlumnoDocumento(); documento.setAlumno(solicitud.getAlumno());
        documento.setArchivo(solicitud.getArchivo()); documento.setTipo(solicitud.getTipoDocumento());
        documento.setDescripcion(solicitud.getDescripcionDocumento()); documento.setFechaDocumento(solicitud.getFechaDocumento());
        documento.setVigenteHasta(solicitud.getVigenteHasta()); solicitud.setDocumentoAplicado(documentos.saveAndFlush(documento));
    }

    private void aplicarFichaMedica(ActualizacionExpedienteFamiliar s) {
        FichaMedicaAlumno ficha = fichas.findByAlumnoId(s.getAlumno().getId()).orElseGet(() -> { FichaMedicaAlumno f=new FichaMedicaAlumno();f.setAlumno(s.getAlumno());return f; });
        ficha.setTipoSanguineo(s.getTipoSanguineo()); ficha.setAlergias(s.getAlergias()); ficha.setPadecimientos(s.getPadecimientos());
        ficha.setMedicamentos(s.getMedicamentos()); ficha.setDiscapacidadNecesidades(s.getDiscapacidadNecesidades());
        ficha.setRestriccionesFisicas(s.getRestriccionesFisicas()); ficha.setRestriccionesAlimentarias(s.getRestriccionesAlimentarias());
        ficha.setServicioMedico(s.getServicioMedico()); ficha.setNumeroAfiliacion(s.getNumeroAfiliacion()); ficha.setMedicoTratante(s.getMedicoTratante());
        ficha.setContactoEmergencia(s.getContactoEmergencia()); ficha.setTelefonoEmergencia(s.getTelefonoEmergencia());
        ficha.setObservaciones(s.getObservacionesMedicas()); ficha.setAutorizaAtencionEmergencia(Boolean.TRUE.equals(s.getAutorizaAtencionEmergencia()));
        solicitudVersion(s, fichas.saveAndFlush(ficha).getVersion());
    }

    private void solicitudVersion(ActualizacionExpedienteFamiliar solicitud, Long version) { solicitud.setFichaMedicaVersionAplicada(version); }
    private void resolver(ActualizacionExpedienteFamiliar s, EstadoActualizacionExpediente estado, String respuesta, UsuarioPrincipal p) {
        String texto=limpiar(respuesta); if(texto==null||texto.isBlank())throw new ReglaNegocioException("Escribe una respuesta para la familia");
        var usuario=usuarios.findById(p.usuarioId()).orElseThrow(()->new RecursoNoEncontradoException("el usuario revisor",p.usuarioId()));
        s.setEstado(estado);s.setRespuestaAdmin(texto);s.setRevisadoPor(usuario);s.setRevisadoEn(Instant.now());solicitudes.saveAndFlush(s);
    }

    private ActualizacionExpedienteFamiliar bloquearPendiente(Long id,Long version){var s=solicitudes.findByIdForUpdate(id).orElseThrow(()->new RecursoNoEncontradoException("la propuesta",id));if(version==null||!version.equals(s.getVersion()))throw new ConflictoVersionException("La propuesta familiar",id);if(s.getEstado()!=EstadoActualizacionExpediente.ENVIADA)throw new ReglaNegocioException("La propuesta ya fue revisada");return s;}
    private ActualizacionExpedienteFamiliar obtener(Long id){return solicitudes.findById(id).orElseThrow(()->new RecursoNoEncontradoException("la propuesta",id));}
    private ArchivoDescarga descarga(ActualizacionExpedienteFamiliar s){if(s.getArchivo()==null||s.getArchivo().getEstado()!=EstadoArchivo.DISPONIBLE)throw new RecursoNoEncontradoException("el archivo de la propuesta",s.getId());Archivo a=s.getArchivo();return new ArchivoDescarga(almacenamiento.abrir(a.getClaveAlmacenamiento()),a.getNombreOriginal(),a.getTipoMime(),a.getTamanoBytes());}
    private boolean esPdf(byte[] c){return c.length>=5&&c[0]=='%'&&c[1]=='P'&&c[2]=='D'&&c[3]=='F'&&c[4]=='-';}
    private String checksum(byte[] c){try{return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(c));}catch(NoSuchAlgorithmException e){throw new IllegalStateException(e);}}
    private String nombreSeguro(String original,String ext){String n=original==null?"documento."+ext:original.replace('\\','/');n=n.substring(n.lastIndexOf('/')+1).replaceAll("[\\p{Cntrl}]","").trim();if(n.isBlank())n="documento."+ext;return n.length()>255?n.substring(n.length()-255):n;}
    private void copiarMedica(ActualizacionExpedienteFamiliar s,PortalFichaMedicaPropuestaForm f){s.setTipoSanguineo(f.getTipoSanguineo());s.setAlergias(limpiar(f.getAlergias()));s.setPadecimientos(limpiar(f.getPadecimientos()));s.setMedicamentos(limpiar(f.getMedicamentos()));s.setDiscapacidadNecesidades(limpiar(f.getDiscapacidadNecesidades()));s.setRestriccionesFisicas(limpiar(f.getRestriccionesFisicas()));s.setRestriccionesAlimentarias(limpiar(f.getRestriccionesAlimentarias()));s.setServicioMedico(limpiar(f.getServicioMedico()));s.setNumeroAfiliacion(limpiar(f.getNumeroAfiliacion()));s.setMedicoTratante(limpiar(f.getMedicoTratante()));s.setContactoEmergencia(limpiar(f.getContactoEmergencia()));s.setTelefonoEmergencia(limpiar(f.getTelefonoEmergencia()));s.setObservacionesMedicas(limpiar(f.getObservaciones()));s.setAutorizaAtencionEmergencia(f.isAutorizaAtencionEmergencia());}
    private ActualizacionExpedienteFila fila(ActualizacionExpedienteFamiliar s){String resumen=s.getTipo()==TipoActualizacionExpediente.DOCUMENTO?s.getTipoDocumento().getEtiqueta():"Ficha médica completa";return new ActualizacionExpedienteFila(s.getId(),s.getAlumno().getId(),nombre(s.getAlumno().getNombres(),s.getAlumno().getPrimerApellido(),s.getAlumno().getSegundoApellido()),s.getAlumno().getMatricula(),nombre(s.getTutor().getNombres(),s.getTutor().getPrimerApellido(),s.getTutor().getSegundoApellido()),s.getTipo(),s.getEstado(),resumen,s.getCreadoEn(),s.getRevisadoEn(),s.getRespuestaAdmin(),s.getArchivo()!=null,s.getVersion());}
    private ActualizacionExpedienteDetalle detalle(ActualizacionExpedienteFamiliar s){return new ActualizacionExpedienteDetalle(s.getId(),s.getAlumno().getId(),nombre(s.getAlumno().getNombres(),s.getAlumno().getPrimerApellido(),s.getAlumno().getSegundoApellido()),s.getAlumno().getMatricula(),nombre(s.getTutor().getNombres(),s.getTutor().getPrimerApellido(),s.getTutor().getSegundoApellido()),s.getTipo(),s.getEstado(),s.getMensajeTutor(),s.getConsentimientoTexto(),s.getConsentimientoEn(),s.getTipoDocumento(),s.getDescripcionDocumento(),s.getFechaDocumento(),s.getVigenteHasta(),s.getArchivo()==null?null:s.getArchivo().getNombreOriginal(),s.getTipoSanguineo(),s.getAlergias(),s.getPadecimientos(),s.getMedicamentos(),s.getDiscapacidadNecesidades(),s.getRestriccionesFisicas(),s.getRestriccionesAlimentarias(),s.getServicioMedico(),s.getNumeroAfiliacion(),s.getMedicoTratante(),s.getContactoEmergencia(),s.getTelefonoEmergencia(),s.getObservacionesMedicas(),s.getAutorizaAtencionEmergencia(),s.getRespuestaAdmin(),s.getRevisadoEn(),s.getRevisadoPor()==null?null:s.getRevisadoPor().getUsername(),s.getVersion());}
    private String nombre(String n,String a,String b){return String.join(" ",java.util.stream.Stream.of(n,a,b).filter(x->x!=null&&!x.isBlank()).toList());}
    private record ContextoPortal(Tutor tutor,Alumno alumno){}
    private record ArchivoPreparado(byte[] contenido,String nombre,String mime,String checksum){}
}
