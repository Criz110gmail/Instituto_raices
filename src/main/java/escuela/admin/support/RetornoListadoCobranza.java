package escuela.admin.support;

import escuela.admin.dto.*;
import jakarta.servlet.http.*;
import java.net.URI;
import java.util.Map;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.*;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

/** Contexto explícito por navegación; no guarda búsquedas en sesión ni mezcla pestañas. */
@Component
public class RetornoListadoCobranza implements HandlerInterceptor,WebMvcConfigurer {
    private static final Map<String,String> RUTAS=Map.of("pagos","/admin/catalogos/pagos","cargos","/admin/catalogos/cargos","cuotas-alumno","/admin/catalogos/cuotas-alumno");
    public static String listado(ModuloCatalogo modulo,FiltroCatalogo f) {
        if(!RUTAS.containsKey(modulo.slug()))return null;
        return UriComponentsBuilder.fromPath(RUTAS.get(modulo.slug()))
            .queryParam("q",f.q()).queryParam("estado",f.estado()).queryParam("pagina",f.pagina()).queryParam("tamanio",f.tamanio())
            .queryParamIfPresent("fecha",java.util.Optional.ofNullable(f.fecha()))
            .queryParamIfPresent("desde",java.util.Optional.ofNullable(f.desde()))
            .queryParamIfPresent("hasta",java.util.Optional.ofNullable(f.hasta()))
            .queryParam("tipoFecha",f.tipoFecha()).queryParam("cobertura",f.cobertura())
            .queryParamIfPresent("anioCobertura",java.util.Optional.ofNullable(f.anioCobertura()))
            .queryParam("conceptoCobro",f.conceptoCobro()).build().encode().toUriString().replace("+","%2B");
    }
    static String validar(String valor,String rutaSolicitud) {
        if(valor==null||valor.length()>6000)return null;
        String listado=RUTAS.entrySet().stream().filter(e->rutaSolicitud.startsWith("/admin/"+e.getKey()+"/")).map(Map.Entry::getValue).findFirst().orElse(null);
        if(listado==null)return null;
        try {
            var uri=URI.create(valor);
            return !uri.isAbsolute()&&uri.getRawAuthority()==null&&uri.getRawFragment()==null&&listado.equals(uri.getRawPath())?valor:null;
        }catch(IllegalArgumentException e){return null;}
    }
    @Override public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(this).addPathPatterns("/admin/pagos/**","/admin/cargos/**","/admin/cuotas-alumno/**");
    }
    @Override public void postHandle(HttpServletRequest request,HttpServletResponse response,Object handler,ModelAndView mv) {
        if(mv==null)return;
        String candidato=request.getParameter("retorno");
        if(candidato!=null&&!request.getContextPath().isEmpty()&&candidato.startsWith(request.getContextPath()+"/admin/"))
            candidato=candidato.substring(request.getContextPath().length());
        String retorno=validar(candidato,request.getRequestURI().substring(request.getContextPath().length()));
        if(retorno==null)return;
        String vista=mv.getViewName();
        if(vista!=null&&vista.startsWith("redirect:/admin/")) {
            String destino=vista.substring("redirect:".length());
            if(destino.equals(URI.create(retorno).getPath()))mv.setViewName("redirect:"+retorno);
            else if(validar(retorno,destino.split("[?#]",2)[0])!=null)
                mv.setViewName("redirect:"+UriComponentsBuilder.fromUriString(destino).queryParam("retorno",retorno).build().encode().toUriString());
        }else mv.addObject("retornoListado",request.getContextPath()+retorno);
    }
}
