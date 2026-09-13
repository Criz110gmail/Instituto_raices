package escuela.archivo.repository;

import escuela.archivo.entity.Archivo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArchivoRepository extends JpaRepository<Archivo, Long> {
}
