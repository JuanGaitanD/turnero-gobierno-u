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
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TurnoService {
    /**
     * Obtiene el historial de turnos atendidos
     */
    @Transactional(readOnly = true)
    public List<TurnoResponseDTO> obtenerTurnosAtendidos() {
        return turnoRepository.findByEstadoOrderByFechaAtencionDescPrioridadAsc(EstadoTurno.ATENDIDO)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    private final TurnoRepository turnoRepository;
    private final UsuarioRepository usuarioRepository;

    /**
     * Crea un nuevo turno
     * Sistema de prioridades con envejecimiento:
     * - Prioridad 3: Personas mayores de 60 años (asignada inmediatamente)
     * - Prioridad 1: Todos los demás servicios (PQRS, trámite vivienda, atención tributaria)
     * 
     * Los turnos PQRS subirán automáticamente de prioridad 1 a 2 después de 10 minutos de espera
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
        return turnoRepository.findByEstadoOrderByFechaAtencionDescPrioridadAsc(EstadoTurno.PENDIENTE)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene el siguiente turno a atender Actualiza las prioridades de PQRS
     * con más de 10 minutos antes de seleccionar Valida que no haya un turno en
     * atención antes de obtener el siguiente
     */
    @Transactional
    public TurnoResponseDTO obtenerSiguienteTurno() {
        System.out.println("🔍 Verificando si hay turno en atención...");
        // Verificar si ya hay un turno en atención
        Optional<turnos> turnoEnAtencion = turnoRepository.findByEstadoOrderById(EstadoTurno.EN_ATENCION)
                .stream().findFirst();

        if (turnoEnAtencion.isPresent()) {
            System.out.println("⚠️ Ya hay un turno en atención: " + turnoEnAtencion.get().getNumeroTurno());
            throw new RuntimeException("Ya hay un turno en atención. Debe finalizarlo o cancelarlo primero.");
        }

        System.out.println("🔄 Actualizando prioridades PQRS...");
        // Actualizar prioridades de turnos PQRS con más de 10 minutos
        actualizarPrioridadesPQRS();

        System.out.println("🔍 Buscando siguiente turno pendiente...");
        turnos turno = turnoRepository.findFirstByEstadoOrderByPrioridadDescFechaCreacionAsc(EstadoTurno.PENDIENTE)
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
     * Prioridad 3: Mayores de 60 años (prioridad máxima inmediata)
     * Prioridad 1: Todos los demás servicios (incluyendo PQRS)
     * 
     * Los turnos PQRS subirán de prioridad 1 a 2 después de 10 minutos de espera
     */
    private Integer calcularPrioridad(usuarios usuario, Servicio servicio) {
        // Prioridad 3: Personas mayores de 60 años tienen prioridad máxima inmediata
        if (usuario.getEdad() >= 60) {
            return 3;
        }

        // Prioridad 1: Todos los servicios (PQRS, trámite de vivienda, atención tributaria)
        // Los PQRS subirán a prioridad 2 después de 10 minutos mediante envejecimiento
        return 1;
    }

    /**
     * Actualiza la prioridad de los turnos PQRS que tienen más de 10 minutos de espera
     * Los eleva de prioridad 1 a prioridad 2 (envejecimiento)
     */
    private void actualizarPrioridadesPQRS() {
        List<turnos> turnosPQRS = turnoRepository.findByEstadoOrderById(EstadoTurno.PENDIENTE)
                .stream()
                .filter(t -> t.getServicio() == Servicio.pqrs)
                .filter(t -> ChronoUnit.MINUTES.between(t.getFechaCreacion(), LocalDateTime.now()) > 10)
                .filter(t -> t.getPrioridad() < 2) // Solo actualizar si están en prioridad 1
                .collect(Collectors.toList());

        turnosPQRS.forEach(turno -> {
            System.out.println("⏰ Envejeciendo turno PQRS: " + turno.getNumeroTurno() + " de prioridad " + turno.getPrioridad() + " a 2");
            turno.setPrioridad(2);
            turnoRepository.save(turno);
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
