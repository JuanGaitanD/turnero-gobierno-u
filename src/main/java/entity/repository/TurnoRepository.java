package entity.repository;

import entity.turnos;
import entity.turnos.EstadoTurno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TurnoRepository extends JpaRepository<turnos, Long> {
    
    List<turnos> findByEstadoOrderByPrioridadDescFechaCreacionAsc(EstadoTurno estado);
    
    @Query("SELECT t FROM turnos t WHERE t.estado = 'PENDIENTE' ORDER BY t.prioridad DESC, t.fechaCreacion ASC")
    Optional<turnos> findSiguienteTurno();
    
    Long countByEstado(EstadoTurno estado);
    
    List<turnos> findByEstado(EstadoTurno estado);
}
