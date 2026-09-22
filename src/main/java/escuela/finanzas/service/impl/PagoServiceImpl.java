package escuela.finanzas.service.impl;

import escuela.alumno.repository.AlumnoTutorRepository;
import escuela.archivo.dto.ArchivoDescarga;
import escuela.archivo.entity.Archivo;
import escuela.archivo.entity.EstadoArchivo;
import escuela.archivo.repository.ArchivoRepository;
import escuela.archivo.storage.AlmacenamientoArchivo;
import escuela.cobranza.entity.*;
import escuela.cobranza.repository.CargoRepository;
import escuela.common.exception.*;
import escuela.finanzas.dto.request.*;
import escuela.finanzas.dto.response.PagoResponse;
import escuela.finanzas.entity.*;
import escuela.finanzas.mapper.PagoMapper;
import escuela.finanzas.repository.*;
import escuela.finanzas.service.PagoService;
import escuela.institucion.entity.*;
import escuela.institucion.repository.*;
import escuela.tutor.entity.Tutor;
import escuela.tutor.repository.TutorRepository;
import escuela.seguridad.service.UsuarioPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.math.*;
import java.security.*;
import java.time.*;
import java.util.*;

import static escuela.common.mapper.NormalizacionTexto.codigo;
import static escuela.common.mapper.NormalizacionTexto.limpiar;
import static escuela.cobranza.support.CalculoCargo.saldo;

@Service
@RequiredArgsConstructor
@Transactional
public class PagoServiceImpl implements PagoService {
    static final long TAMANO_MAXIMO_COMPROBANTE = 10L * 1024 * 1024;
    static final int MAXIMO_COMPROBANTES = 5;

    private final PagoRepository pagoRepository;
    private final ComprobantePagoRepository comprobanteRepository;
    private final SolicitudAplicacionPagoRepository solicitudRepository;
    private final ArchivoRepository archivoRepository;
    private final InstitucionRepository institucionRepository;
    private final PlantelRepository plantelRepository;
    private final TutorRepository tutorRepository;
    private final CuentaFinancieraRepository cuentaRepository;
    private final CargoRepository cargoRepository;
    private final AlumnoTutorRepository vinculoRepository;
    private final AlmacenamientoArchivo almacenamiento;
    private final PagoMapper mapper;

    @Override
    public PagoResponse registrar(PagoRequest request, List<MultipartFile> archivosRecibidos) {
        return registrar(request, archivosRecibidos, OrigenRegistroPago.ADMINISTRACION, null);
    }

    @Override
    public PagoResponse registrarDesdePortal(PagoRequest request, List<MultipartFile> archivosRecibidos,
                                              UsuarioPrincipal principal) {
        if (principal == null || principal.usuarioId() == null || principal.institucionId() == null
                || principal.accesoRecuperacion()) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "El reporte requiere una cuenta familiar activa");
        }
        Tutor tutor = tutorRepository.findById(request.tutorId())
                .orElseThrow(() -> new RecursoNoEncontradoException("el tutor", request.tutorId()));
        if (tutor.getUsuario() == null || !principal.usuarioId().equals(tutor.getUsuario().getId())
                || !principal.institucionId().equals(tutor.getInstitucion().getId())) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "El tutor del pago no corresponde a la cuenta familiar");
        }
        return registrar(request, archivosRecibidos, OrigenRegistroPago.PORTAL_FAMILIAR,
                tutor.getUsuario());
    }

    private PagoResponse registrar(PagoRequest request, List<MultipartFile> archivosRecibidos,
                                   OrigenRegistroPago origen, escuela.seguridad.entity.Usuario reportante) {
        List<MultipartFile> comprobantes = archivosRecibidos == null ? List.of()
                : archivosRecibidos.stream().filter(a -> a != null && !a.isEmpty()).toList();
        if (comprobantes.size() > MAXIMO_COMPROBANTES) {
            throw new ReglaNegocioException("Puedes adjuntar como máximo cinco comprobantes por pago");
        }
        if (request.metodo() == MetodoPago.TRANSFERENCIA && comprobantes.isEmpty()) {
            throw new ReglaNegocioException("Una transferencia requiere al menos un comprobante JPEG, PNG o PDF");
        }

        Institucion institucion = institucionRepository.findById(request.institucionId())
                .orElseThrow(() -> new RecursoNoEncontradoException("la institución", request.institucionId()));
        Plantel plantel = plantelRepository.findById(request.plantelRegistroId())
                .orElseThrow(() -> new RecursoNoEncontradoException("el plantel", request.plantelRegistroId()));
        Tutor tutor = tutorRepository.findById(request.tutorId())
                .orElseThrow(() -> new RecursoNoEncontradoException("el tutor", request.tutorId()));
        validarPropietarios(request, institucion, plantel, tutor);
        CuentaFinanciera cuenta = obtenerCuenta(request.cuentaDeclaradaId(), institucion, plantel, request.metodo());

        String folio = codigo(request.folio());
        if (pagoRepository.existsByInstitucionIdAndFolioIgnoreCase(institucion.getId(), folio)) {
            throw new RecursoDuplicadoException("Ya existe un pago con ese folio en la institución");
        }

        List<CargoSolicitud> solicitudes = validarSolicitudes(request, institucion, tutor);
        BigDecimal totalSolicitado = solicitudes.stream().map(CargoSolicitud::monto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (totalSolicitado.compareTo(request.monto()) > 0) {
            throw new ReglaNegocioException("La distribución solicitada no puede superar el monto total del pago");
        }

        Pago pago = new Pago();
        pago.setInstitucion(institucion);
        pago.setPlantelRegistro(plantel);
        pago.setTutor(tutor);
        pago.setNombrePagador(limpiar(request.nombrePagador()));
        pago.setFolio(folio);
        pago.setClaveIdempotencia(UUID.randomUUID().toString());
        pago.setFechaPago(request.fechaPago());
        pago.setMonto(request.monto().setScale(2, RoundingMode.UNNECESSARY));
        pago.setMoneda(codigo(request.moneda()));
        pago.setMetodo(request.metodo());
        pago.setEstado(EstadoPago.PENDIENTE_VALIDACION);
        pago.setOrigenRegistro(origen);
        pago.setReportadoPor(reportante);
        pago.setCuentaDeclarada(cuenta);
        pago.setReferencia(limpiar(request.referencia()));
        pago.setObservaciones(limpiar(request.observaciones()));
        pago = pagoRepository.saveAndFlush(pago);

        for (CargoSolicitud dato : solicitudes) {
            SolicitudAplicacionPago solicitud = new SolicitudAplicacionPago();
            solicitud.setPago(pago);
            solicitud.setCargo(dato.cargo());
            solicitud.setMontoSolicitado(dato.monto());
            solicitudRepository.save(solicitud);
            pago.getSolicitudes().add(solicitud);
        }

        List<String> clavesGuardadas = new ArrayList<>();
        try {
            for (MultipartFile comprobante : comprobantes) {
                ComprobanteValidado validado = validarComprobante(comprobante);
                String clave = institucion.getId() + "/pagos/" + pago.getId() + "/"
                        + UUID.randomUUID() + ".bin";
                clavesGuardadas.add(clave);
                almacenamiento.guardar(clave, new ByteArrayInputStream(validado.contenido()));

                Archivo archivo = new Archivo();
                archivo.setInstitucion(institucion);
                archivo.setClaveAlmacenamiento(clave);
                archivo.setNombreOriginal(nombreSeguro(comprobante.getOriginalFilename(), validado.extension()));
                archivo.setTipoMime(validado.tipoMime());
                archivo.setTamanoBytes(validado.contenido().length);
                archivo.setChecksumSha256(checksum(validado.contenido()));
                archivo.setEstado(EstadoArchivo.DISPONIBLE);
                archivo = archivoRepository.saveAndFlush(archivo);

                ComprobantePago relacion = new ComprobantePago();
                relacion.setPago(pago);
                relacion.setArchivo(archivo);
                comprobanteRepository.save(relacion);
                pago.getComprobantes().add(relacion);
            }
            pagoRepository.flush();
            return mapper.respuesta(pago);
        } catch (RuntimeException excepcion) {
            clavesGuardadas.forEach(almacenamiento::eliminarSiExiste);
            throw excepcion;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PagoResponse obtener(Long id) {
        return mapper.respuesta(pagoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("el pago", id)));
    }

    @Override
    @Transactional(readOnly = true)
    public ArchivoDescarga descargarComprobante(Long pagoId, Long comprobanteId) {
        ComprobantePago comprobante = comprobanteRepository.findByIdAndPagoId(comprobanteId, pagoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("el comprobante", comprobanteId));
        Archivo archivo = comprobante.getArchivo();
        if (archivo.getEstado() != EstadoArchivo.DISPONIBLE) {
            throw new ReglaNegocioException("El comprobante no está disponible");
        }
        return new ArchivoDescarga(almacenamiento.abrir(archivo.getClaveAlmacenamiento()),
                archivo.getNombreOriginal(), archivo.getTipoMime(), archivo.getTamanoBytes());
    }

    private void validarPropietarios(PagoRequest request, Institucion institucion,
                                     Plantel plantel, Tutor tutor) {
        if (!institucion.isActivo()) throw new ReglaNegocioException("La institución debe estar activa");
        if (!plantel.isActivo() || !plantel.getInstitucion().getId().equals(institucion.getId())) {
            throw new ReglaNegocioException("El plantel de registro debe estar activo y pertenecer a la institución");
        }
        if (!tutor.isActivo() || !tutor.getInstitucion().getId().equals(institucion.getId())) {
            throw new ReglaNegocioException("El tutor debe estar activo y pertenecer a la institución");
        }
        if (!codigo(request.moneda()).equals(institucion.getMonedaPredeterminada())) {
            throw new ReglaNegocioException("La moneda debe coincidir con la moneda predeterminada de la institución");
        }
        if (request.fechaPago().isAfter(Instant.now())) {
            throw new ReglaNegocioException("La fecha y hora del pago no puede estar en el futuro");
        }
    }

    private CuentaFinanciera obtenerCuenta(Long id, Institucion institucion, Plantel plantel,
                                            MetodoPago metodo) {
        if (id == null) return null;
        CuentaFinanciera cuenta = cuentaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("la cuenta declarada", id));
        boolean alcanceValido = cuenta.getInstitucion().getId().equals(institucion.getId())
                && (cuenta.getPlantel() == null || cuenta.getPlantel().getId().equals(plantel.getId()));
        if (!cuenta.isActivo() || !alcanceValido) {
            throw new ReglaNegocioException("La cuenta declarada no está disponible para este plantel");
        }
        if (metodo == MetodoPago.EFECTIVO && cuenta.getTipo() != TipoCuentaFinanciera.CAJA) {
            throw new ReglaNegocioException("Un pago en efectivo sólo puede declarar una cuenta de tipo caja");
        }
        if (metodo == MetodoPago.TRANSFERENCIA && cuenta.getTipo() == TipoCuentaFinanciera.CAJA) {
            throw new ReglaNegocioException("Una transferencia debe declarar una cuenta bancaria o de inversión");
        }
        return cuenta;
    }

    private List<CargoSolicitud> validarSolicitudes(PagoRequest request, Institucion institucion, Tutor tutor) {
        Set<Long> ids = new HashSet<>();
        LocalDate fechaLocal = request.fechaPago().atZone(ZoneId.of(institucion.getZonaHoraria())).toLocalDate();
        List<CargoSolicitud> resultado = new ArrayList<>();
        for (SolicitudAplicacionPagoRequest solicitud : request.solicitudes()) {
            if (!ids.add(solicitud.cargoId())) {
                throw new ReglaNegocioException("Un cargo sólo puede aparecer una vez en la distribución solicitada");
            }
            Cargo cargo = cargoRepository.findById(solicitud.cargoId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("el cargo", solicitud.cargoId()));
            if (cargo.getEstadoRegistro() != EstadoRegistroCargo.EMITIDO
                    || !cargo.getInscripcion().getAlumno().getInstitucion().getId().equals(institucion.getId())) {
                throw new ReglaNegocioException("Todos los cargos deben estar emitidos y pertenecer a la institución del pago");
            }
            if (!cargo.getMoneda().equals(codigo(request.moneda()))) {
                throw new ReglaNegocioException("Todos los cargos deben usar la misma moneda del pago");
            }
            if (!vinculoRepository.tieneResponsabilidadFinancieraVigente(
                    cargo.getInscripcion().getAlumno().getId(), tutor.getId(), fechaLocal)) {
                throw new ReglaNegocioException("El tutor no tiene responsabilidad financiera vigente sobre uno de los alumnos seleccionados");
            }
            BigDecimal monto = solicitud.montoSolicitado().setScale(2, RoundingMode.UNNECESSARY);
            BigDecimal saldoActual = saldo(cargo);
            if (monto.compareTo(saldoActual) > 0) {
                throw new ReglaNegocioException("El monto solicitado para " + cargo.getDescripcion()
                        + " supera su saldo actual de " + saldoActual.toPlainString() + " " + cargo.getMoneda());
            }
            resultado.add(new CargoSolicitud(cargo, monto));
        }
        return resultado;
    }

    private ComprobanteValidado validarComprobante(MultipartFile archivo) {
        if (archivo.getSize() <= 0 || archivo.getSize() > TAMANO_MAXIMO_COMPROBANTE) {
            throw new ReglaNegocioException("Cada comprobante debe pesar entre 1 byte y 10 MB");
        }
        try {
            byte[] contenido = archivo.getBytes();
            if (esPng(contenido)) return new ComprobanteValidado(contenido, "image/png", "png");
            if (esJpeg(contenido)) return new ComprobanteValidado(contenido, "image/jpeg", "jpg");
            if (esPdf(contenido)) return new ComprobanteValidado(contenido, "application/pdf", "pdf");
        } catch (IOException excepcion) {
            throw new ReglaNegocioException("No fue posible leer uno de los comprobantes");
        }
        throw new ReglaNegocioException("Los comprobantes deben ser archivos JPEG, PNG o PDF válidos");
    }

    private boolean esPng(byte[] b) {
        byte[] firma = {(byte) 0x89, 0x50, 0x4e, 0x47, 0x0d, 0x0a, 0x1a, 0x0a};
        return b.length >= firma.length && Arrays.equals(Arrays.copyOf(b, firma.length), firma);
    }

    private boolean esJpeg(byte[] b) {
        return b.length >= 3 && (b[0] & 0xff) == 0xff && (b[1] & 0xff) == 0xd8 && (b[2] & 0xff) == 0xff;
    }

    private boolean esPdf(byte[] b) {
        return b.length >= 5 && b[0] == '%' && b[1] == 'P' && b[2] == 'D' && b[3] == 'F' && b[4] == '-';
    }

    private String checksum(byte[] contenido) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(contenido));
        } catch (NoSuchAlgorithmException excepcion) {
            throw new IllegalStateException("SHA-256 no está disponible", excepcion);
        }
    }

    private String nombreSeguro(String original, String extension) {
        String limpio = original == null ? "comprobante." + extension
                : original.replace('\\', '/').replaceAll("[\\p{Cntrl}]", "").trim();
        limpio = limpio.substring(limpio.lastIndexOf('/') + 1);
        if (limpio.isBlank()) limpio = "comprobante." + extension;
        return limpio.length() > 255 ? limpio.substring(limpio.length() - 255) : limpio;
    }

    private record CargoSolicitud(Cargo cargo, BigDecimal monto) {}
    private record ComprobanteValidado(byte[] contenido, String tipoMime, String extension) {}
}
