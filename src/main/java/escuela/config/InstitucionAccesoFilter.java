package escuela.config;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
/** El enlace institucional califica el usuario; no cambia sus permisos ni su institución. */
public final class InstitucionAccesoFilter extends OncePerRequestFilter {
 public static final String CONTEXTO="nexo.portal.institucion";
 public static boolean codigoValido(String s){return s!=null&&s.matches("[a-zA-Z0-9_-]{1,50}");}
 public static String continuar(HttpServletRequest r,String destino){var code=r.getParameter("institucionAcceso");if(!codigoValido(code)&&r.getAttribute(CONTEXTO) instanceof String context)code=context;return codigoValido(code)?destino+(destino.contains("?")?"&":"?")+"institucion="+code:destino;}
 @Override protected void doFilterInternal(HttpServletRequest r,HttpServletResponse s,FilterChain chain)throws ServletException,IOException{
  String path=r.getRequestURI().substring(r.getContextPath().length());
  if("POST".equals(r.getMethod())&&path.equals("/login")&&codigoValido(r.getParameter("institucionAcceso"))){String code=r.getParameter("institucionAcceso");chain.doFilter(new HttpServletRequestWrapper(r){@Override public String getParameter(String key){String value=super.getParameter(key);if(!key.equals("username")||value==null)return value;int split=value.indexOf('\\');return split>=0&&value.substring(0,split).equalsIgnoreCase(code)?value:code+"\\"+value;}},s);return;}
  if("POST".equals(r.getMethod())&&path.equals("/logout")){var session=r.getSession(false);String code=session==null?null:(String)session.getAttribute(CONTEXTO);if(codigoValido(code)){chain.doFilter(new HttpServletRequestWrapper(r){@Override public String getParameter(String key){return key.equals("institucionAcceso")?code:super.getParameter(key);}},s);return;}}
  chain.doFilter(r,s);
 }
}
