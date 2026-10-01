package escuela.compras.dto;

public record ProveedorFila(Long id,Long institucionId,String razonSocial,String nombreComercial,String rfc,
                            String contacto,String telefono,String correo,boolean activo,Long version){}
