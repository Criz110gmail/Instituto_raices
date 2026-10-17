package escuela.compras.service;

import escuela.admin.dto.*;
import escuela.admin.service.MotivoMovimientoBusquedaService;
import escuela.compras.entity.Proveedor;
import escuela.compras.repository.ProveedorRepository;
import escuela.finanzas.entity.*;
import escuela.finanzas.repository.*;
import escuela.seguridad.service.AlcanceDatosService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Locale;
import java.util.function.Function;

@Service @RequiredArgsConstructor @Transactional(readOnly=true)
public class CompraBusquedaService {
    private final ProveedorRepository proveedores;
    private final CuentaFinancieraRepository cuentas;
    private final MotivoFinancieroRepository motivos;
    private final MotivoMovimientoBusquedaService busquedaMotivos;
    private final AlcanceDatosService alcance;

    public ResultadoAutocompletado proveedores(Long institucionId,String q) {
        alcance.validarInstitucion(institucionId);
        if(corta(q)) return ResultadoAutocompletado.vacio();
        return resultado(proveedores.findAll(filtroProveedor(institucionId,q),pagina("razonSocial")),CompraBusquedaService::opcion);
    }
    public ResultadoAutocompletado cuentas(Long institucionId,Long plantelId,String q) {
        alcance.validarInstitucion(institucionId);
        if(plantelId!=null) alcance.validarPlantel(plantelId);
        if(corta(q)) return ResultadoAutocompletado.vacio();
        return resultado(cuentas.findAll(filtroCuenta(institucionId,plantelId,q),pagina("nombre")),CompraBusquedaService::opcion);
    }
    public ResultadoAutocompletado motivos(Long institucionId,String q) {
        return busquedaMotivos.buscar(institucionId,DireccionMovimiento.EGRESO,q);
    }
    // Historial: también incluye inactivos. Sin plantel elegido, todas las cuentas autorizadas.
    public ResultadoAutocompletado proveedoresFiltro(Long institucionId,String q) {
        alcance.validarInstitucion(institucionId);
        if(corta(q))return ResultadoAutocompletado.vacio();
        return resultado(proveedores.findAll(filtroProveedorHistorico(institucionId,q),pagina("razonSocial")),
            p->new OpcionAutocompletado(p.getId(),p.getRazonSocial(),(p.getRfc()==null?"Sin RFC":p.getRfc())+(p.isActivo()?"":" · Inactivo")));
    }
    public ResultadoAutocompletado cuentasFiltro(Long institucionId,Long plantelId,String q) {
        alcance.validarInstitucion(institucionId);
        if(plantelId!=null)alcance.validarPlantel(plantelId);
        if(corta(q))return ResultadoAutocompletado.vacio();
        return resultado(cuentas.findAll(filtroCuentaHistorica(institucionId,plantelId,q),pagina("nombre")),
            c->new OpcionAutocompletado(c.getId(),opcion(c).titulo(),c.getTipo()+" · "+c.getMoneda()+(c.isActivo()?"":" · Inactiva")));
    }
    public String proveedorFiltroSeleccionado(Long institucionId,Long id) {
        if(institucionId==null||id==null)return "";
        alcance.validarInstitucion(institucionId);
        return proveedores.findOne(filtroProveedorHistorico(institucionId,"").and((r,q,cb)->cb.equal(r.get("id"),id)))
            .map(Proveedor::getRazonSocial).orElse("");
    }
    public String cuentaFiltroSeleccionada(Long institucionId,Long plantelId,Long id) {
        if(institucionId==null||id==null)return "";
        alcance.validarInstitucion(institucionId);
        if(plantelId!=null)alcance.validarPlantel(plantelId);
        return cuentas.findOne(filtroCuentaHistorica(institucionId,plantelId,"").and((r,q,cb)->cb.equal(r.get("id"),id)))
            .map(c->opcion(c).titulo()).orElse("");
    }
    private Specification<Proveedor> filtroProveedorHistorico(Long i,String texto) {
        String p=patron(texto);
        return (r,q,cb)->cb.and(cb.equal(r.get("institucion").get("id"),i),
            cb.or(cb.like(cb.lower(r.get("razonSocial")),p,'\\'),cb.like(cb.lower(r.get("nombreComercial")),p,'\\'),cb.like(cb.lower(r.get("rfc")),p,'\\')));
    }
    private Specification<CuentaFinanciera> filtroCuentaHistorica(Long i,Long pl,String texto) {
        String p=patron(texto);
        Specification<CuentaFinanciera> filtro=(r,q,cb)->cb.and(cb.equal(r.get("institucion").get("id"),i),
            pl==null?cb.conjunction():cb.or(cb.isNull(r.get("plantel")),cb.equal(r.get("plantel").get("id"),pl)),
            cb.or(cb.like(cb.lower(r.get("codigo")),p,'\\'),cb.like(cb.lower(r.get("nombre")),p,'\\')));
        return alcance.<CuentaFinanciera>especificacion(ModuloCatalogo.CUENTAS_FINANCIERAS).and(filtro);
    }
    // Sólo recupera la selección puntual al editar o regresar de una validación.
    public String proveedorSeleccionado(Long institucionId,Long id) {
        if(institucionId==null||id==null)return "";
        alcance.validarInstitucion(institucionId);
        return proveedores.findOne(filtroProveedor(institucionId,"").and((r,q,cb)->cb.equal(r.get("id"),id)))
            .map(p->opcion(p).titulo()).orElse("");
    }
    public String cuentaSeleccionada(Long institucionId,Long plantelId,Long id) {
        if(institucionId==null||id==null)return "";
        alcance.validarInstitucion(institucionId);
        if(plantelId!=null)alcance.validarPlantel(plantelId);
        return cuentas.findOne(filtroCuenta(institucionId,plantelId,"").and((r,q,cb)->cb.equal(r.get("id"),id)))
            .map(c->opcion(c).titulo()).orElse("");
    }
    public String motivoSeleccionado(Long institucionId,Long id) {
        if(institucionId==null||id==null)return "";
        alcance.validarInstitucion(institucionId);
        return motivos.findById(id).filter(m->m.getInstitucion().getId().equals(institucionId)&&m.isActivo()
            &&m.getNaturaleza()!=NaturalezaMotivoFinanciero.INGRESO)
            .map(m->m.getCodigo()+" · "+m.getNombre()).orElse("");
    }
    private Specification<Proveedor> filtroProveedor(Long i,String texto) {
        String p=patron(texto);
        return (r,q,cb)->cb.and(cb.equal(r.get("institucion").get("id"),i),cb.isTrue(r.get("activo")),
            cb.or(cb.like(cb.lower(r.get("razonSocial")),p,'\\'),cb.like(cb.lower(r.get("nombreComercial")),p,'\\'),cb.like(cb.lower(r.get("rfc")),p,'\\')));
    }
    private Specification<CuentaFinanciera> filtroCuenta(Long i,Long pl,String texto) {
        String p=patron(texto);
        Specification<CuentaFinanciera> filtro=(r,q,cb)->cb.and(cb.equal(r.get("institucion").get("id"),i),cb.isTrue(r.get("activo")),
            pl==null?cb.isNull(r.get("plantel")):cb.or(cb.isNull(r.get("plantel")),cb.equal(r.get("plantel").get("id"),pl)),
            cb.or(cb.like(cb.lower(r.get("codigo")),p,'\\'),cb.like(cb.lower(r.get("nombre")),p,'\\')));
        return alcance.<CuentaFinanciera>especificacion(ModuloCatalogo.CUENTAS_FINANCIERAS).and(filtro);
    }
    private static boolean corta(String q){return q!=null&&!q.isBlank()&&q.trim().length()<3;}
    private static String patron(String q){String t=q==null?"":q.trim();return "%"+t.substring(0,Math.min(100,t.length())).toLowerCase(Locale.ROOT).replace("\\","\\\\").replace("%","\\%").replace("_","\\_")+"%";}
    private static Pageable pagina(String campo){return PageRequest.of(0,11,Sort.by(campo).ascending().and(Sort.by("id")));}
    private static <T> ResultadoAutocompletado resultado(Page<T> p,Function<T,OpcionAutocompletado> f){return new ResultadoAutocompletado(p.stream().limit(10).map(f).toList(),p.hasNext()||p.getNumberOfElements()>10);}
    private static OpcionAutocompletado opcion(Proveedor p){return new OpcionAutocompletado(p.getId(),p.getRazonSocial(),p.getRfc()==null?"Proveedor activo":p.getRfc());}
    private static OpcionAutocompletado opcion(CuentaFinanciera c){return new OpcionAutocompletado(c.getId(),c.getCodigo()+" · "+c.getNombre(),c.getTipo()+" · "+c.getMoneda());}
}
