package escuela.personal.dto;
public record EmpleadoVista(Long id,Long institucionId,String codigo,String nombres,String primerApellido,String segundoApellido,String email,String telefono,boolean activo,boolean foto,Long version,Long maestroId,boolean docenteActivo,String codigoDocente,String usuarioDocente){
 public String nombre(){return java.util.stream.Stream.of(nombres,primerApellido,segundoApellido).filter(x->x!=null&&!x.isBlank()).collect(java.util.stream.Collectors.joining(" "));}
 public EmpleadoForm formulario(){var f=new EmpleadoForm();f.setInstitucionId(institucionId);f.setCodigo(codigo);f.setNombres(nombres);f.setPrimerApellido(primerApellido);f.setSegundoApellido(segundoApellido);f.setEmail(email);f.setTelefono(telefono);f.setVersion(version);return f;}
}
