package escuela.config;

import escuela.admin.dto.ModuloCatalogo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.util.List;

/** Sólo organiza enlaces ya autorizados; no interviene en permisos, rutas ni servicios. */
@Configuration
public class NavegacionPersonalConfig implements WebMvcConfigurer, HandlerInterceptor {
 @Override public void addInterceptors(InterceptorRegistry registry){registry.addInterceptor(this).addPathPatterns("/admin/**");}
 @Override public void postHandle(HttpServletRequest request,HttpServletResponse response,Object handler,ModelAndView view){
  if(view==null||view.getViewName()!=null&&view.getViewName().startsWith("redirect:"))return;
  Object lista=view.getModel().get("modulos");
  if(!(lista instanceof List<?> enlaces)||!enlaces.stream().allMatch(ModuloCatalogo.class::isInstance))return;
  var autorizados=enlaces.stream().map(ModuloCatalogo.class::cast).toList();
  // Si una lista parcial no contiene al padre, conservar el acceso autorizado original.
  view.addObject("modulos",autorizados.stream().filter(m->m.menuPersonal()==m||!autorizados.contains(m.menuPersonal())).toList());
  if(view.getModel().get("moduloActual") instanceof ModuloCatalogo actual){
   view.addObject("moduloPersonalOrigen",actual);
   if(autorizados.contains(actual.menuPersonal()))view.addObject("moduloActual",actual.menuPersonal());
  }
 }
}
