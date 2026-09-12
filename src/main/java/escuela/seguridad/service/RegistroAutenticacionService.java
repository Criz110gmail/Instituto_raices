package escuela.seguridad.service;

public interface RegistroAutenticacionService {
    void registrarExito(UsuarioPrincipal principal);
    void registrarFallo(String identificador);
}
