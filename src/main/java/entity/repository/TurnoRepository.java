package entity.repository;

import entity.turnos;
import entity.turnos.EstadoTurno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TurnoRepository extends JpaRepository<turnos, Long> {
    
    List<turnos> findByEstadoOrderByFechaAtencionDescPrioridadAsc(EstadoTurno estado);
    
    // Usamos una consulta para obtener SOLAMENTE los datos que necesitamos
    @Query("SELECT t FROM turnos t WHERE t.estado = :estado ORDER BY t.prioridad DESC, t.fechaCreacion ASC LIMIT 1")
    Optional<turnos> findFirstByEstadoOrderByPrioridadDescFechaCreacionAsc(@Param("estado") EstadoTurno estado);
    
    Long countByEstado(EstadoTurno estado);
    
    List<turnos> findByEstadoOrderById(EstadoTurno estado);
}
