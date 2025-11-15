package entity.service;

import entity.dto.TurnoRequestDTO;
import entity.dto.TurnoResponseDTO;
import entity.repository.TurnoRepository;
import entity.repository.UsuarioRepository;
import entity.turnos;
import entity.turnos.EstadoTurno;
import entity.turnos.Servicio;
import entity.usuarios;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TurnoService {
    
    private final TurnoRepository turnoRepository;
    private final UsuarioRepository usuarioRepository;
    
    /**
     * Crea un nuevo turno
     * Calcula la prioridad según las reglas:
     * - Prioridad 3: Personas mayores de 60 años
     * - Prioridad 2: PQRS (puede aumentar a 3 después de 10 minutos)
     * - Prioridad 1: Resto de servicios
     */
    @Transactional
    public TurnoResponseDTO crearTurno(TurnoRequestDTO request) {
        usuarios usuario = usuarioRepository.findById(request.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + request.getUsuarioId()));
        
        turnos turno = new turnos();
        turno.setUsuario(usuario);
        turno.setServicio(request.getServicio());
        turno.setNumeroTurno(generarNumeroTurno());
        turno.setPrioridad(calcularPrioridad(usuario, request.getServicio()));
        
        turnos turnoGuardado = turnoRepository.save(turno);
        return convertirADTO(turnoGuardado);
    }
    
    /**
     * Obtiene todos los turnos del sistema
     */
    @Transactional(readOnly = true)
    public List<TurnoResponseDTO> obtenerTodosTurnos() {
        return turnoRepository.findAll().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Obtiene todos los turnos pendientes ordenados por prioridad
     */
    @Transactional(readOnly = true)
    public List<TurnoResponseDTO> obtenerTurnosPendientes() {
        return turnoRepository.findByEstadoOrderByPrioridadDescFechaCreacionAsc(EstadoTurno.PENDIENTE)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Obtiene el siguiente turno a atender
     * Actualiza las prioridades de PQRS con más de 10 minutos antes de seleccionar
     */
    @Transactional
    public TurnoResponseDTO obtenerSiguienteTurno() {
        System.out.println("🔄 Actualizando prioridades PQRS...");
        // Actualizar prioridades de turnos PQRS con más de 10 minutos
        actualizarPrioridadesPQRS();
        
        System.out.println("🔍 Buscando siguiente turno pendiente...");
        turnos turno = turnoRepository.findSiguienteTurno()
                .orElseThrow(() -> new RuntimeException("No hay turnos pendientes"));
        
        System.out.println("📝 Turno encontrado: " + turno.getNumeroTurno() + " (Prioridad: " + turno.getPrioridad() + ")");
        turno.setEstado(EstadoTurno.EN_ATENCION);
        turno.setFechaAtencion(LocalDateTime.now());
        
        turnos turnoActualizado = turnoRepository.save(turno);
        System.out.println("💾 Turno actualizado a EN_ATENCION");
        return convertirADTO(turnoActualizado);
    }
    
    /**
     * Finaliza un turno marcándolo como ATENDIDO
     */
    @Transactional
    public TurnoResponseDTO finalizarTurno(Long turnoId) {
        turnos turno = turnoRepository.findById(turnoId)
                .orElseThrow(() -> new RuntimeException("Turno no encontrado con ID: " + turnoId));
        
        turno.setEstado(EstadoTurno.ATENDIDO);
        turnos turnoActualizado = turnoRepository.save(turno);
        return convertirADTO(turnoActualizado);
    }
    
    /**
     * Cancela un turno
     */
    @Transactional
    public TurnoResponseDTO cancelarTurno(Long turnoId) {
        turnos turno = turnoRepository.findById(turnoId)
                .orElseThrow(() -> new RuntimeException("Turno no encontrado con ID: " + turnoId));
        
        turno.setEstado(EstadoTurno.CANCELADO);
        turnos turnoActualizado = turnoRepository.save(turno);
        return convertirADTO(turnoActualizado);
    }
    
    /**
     * Calcula la prioridad inicial del turno
     * Prioridad 3: Mayores de 60 años (tienen prioridad máxima inmediata)
     * Prioridad 2: PQRS (puede aumentar a 3 después de 10 minutos)
     * Prioridad 1: Resto de servicios
     */
    private Integer calcularPrioridad(usuarios usuario, Servicio servicio) {
        // Prioridad 3: Personas mayores de 60 años tienen prioridad máxima
        if (usuario.getEdad() >= 60) {
            return 3;
        }
        
        // Prioridad 2: PQRS (puede aumentar con el tiempo)
        if (servicio == Servicio.pqrs) {
            return 2;
        }
        
        // Prioridad 1: Servicios generales
        return 1;
    }
    
    /**
     * Actualiza la prioridad de los turnos PQRS que tienen más de 10 minutos de espera
     * Los eleva a prioridad 3 (máxima)
     */
    private void actualizarPrioridadesPQRS() {
        List<turnos> turnosPQRS = turnoRepository.findByEstado(EstadoTurno.PENDIENTE)
                .stream()
                .filter(t -> t.getServicio() == Servicio.pqrs)
                .filter(t -> ChronoUnit.MINUTES.between(t.getFechaCreacion(), LocalDateTime.now()) > 10)
                .collect(Collectors.toList());
        
        turnosPQRS.forEach(turno -> {
            if (turno.getPrioridad() < 3) {
                turno.setPrioridad(3);
                turnoRepository.save(turno);
            }
        });
    }
    
    /**
     * Genera un número de turno único secuencial
     */
    private String generarNumeroTurno() {
        Long count = turnoRepository.count();
        return String.format("T%04d", count + 1);
    }
    
    /**
     * Convierte una entidad turno a DTO
     */
    private TurnoResponseDTO convertirADTO(turnos turno) {
        TurnoResponseDTO dto = new TurnoResponseDTO();
        dto.setId(turno.getId());
        dto.setNumeroTurno(turno.getNumeroTurno());
        dto.setUsuarioNombre(turno.getUsuario().getNombre_completo());
        dto.setUsuarioDni(turno.getUsuario().getDni());
        dto.setUsuarioEdad(turno.getUsuario().getEdad());
        dto.setServicio(turno.getServicio());
        dto.setEstado(turno.getEstado());
        dto.setPrioridad(turno.getPrioridad());
        dto.setFechaCreacion(turno.getFechaCreacion());
        dto.setFechaAtencion(turno.getFechaAtencion());
        return dto;
    }
}
