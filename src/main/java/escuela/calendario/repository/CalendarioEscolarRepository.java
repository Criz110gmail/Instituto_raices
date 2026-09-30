package escuela.calendario.repository;

import escuela.calendario.entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;

public interface CalendarioEscolarRepository extends JpaRepository<CalendarioEscolarDetalle,Long>,JpaSpecificationExecutor<CalendarioEscolarDetalle>{
    @Query("""
      select count(c)>0 from CalendarioEscolarDetalle c where c.id<>:id and c.activo=true
      and c.cicloEscolar.id=:cicloId and c.fechaInicio<=:fin and c.fechaFin>=:inicio
      and c.tipo in (escuela.calendario.entity.TipoFechaCalendario.DIA_INHABIL,escuela.calendario.entity.TipoFechaCalendario.VACACIONES)
      and ((:plantelId is null and c.plantel is null) or c.plantel.id=:plantelId)
      and ((:nivelId is null and c.nivelEducativo is null) or c.nivelEducativo.id=:nivelId)
      """)
    boolean existeSuspensionSuperpuesta(@Param("id")Long id,@Param("cicloId")Long cicloId,
        @Param("plantelId")Long plantelId,@Param("nivelId")Long nivelId,
        @Param("inicio")LocalDate inicio,@Param("fin")LocalDate fin);
}
