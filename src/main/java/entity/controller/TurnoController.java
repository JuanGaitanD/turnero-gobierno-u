package entity.controller;

import entity.dto.TurnoRequestDTO;
import entity.dto.TurnoResponseDTO;
import entity.service.TurnoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de turnos
 * Endpoints disponibles:
 * - POST /api/turnos - Crear un nuevo turno
 * - GET /api/turnos - Obtener todos los turnos
 * - GET /api/turnos/pendientes - Obtener turnos pendientes
 * - GET /api/turnos/siguiente - Obtener el siguiente turno a atender
 * - PUT /api/turnos/{id}/finalizar - Finalizar un turno
 * - PUT /api/turnos/{id}/cancelar - Cancelar un turno
 * 
 * NOTA: CORS configurado globalmente en WebConfig, no se necesita @CrossOrigin aquí
 */
@RestController
@RequestMapping("/api/turnos")
@RequiredArgsConstructor
public class TurnoController {
    
    private final TurnoService turnoService;
    
    /**
     * Crea un nuevo turno
     * @param request DTO con usuarioId y servicio
     * @return Turno creado con su número y prioridad asignada
     */
    @PostMapping
    public ResponseEntity<TurnoResponseDTO> crearTurno(@RequestBody TurnoRequestDTO request) {
        try {
            TurnoResponseDTO turno = turnoService.crearTurno(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(turno);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Obtiene todos los turnos del sistema
     * @return Lista de todos los turnos
     */
    @GetMapping
    public ResponseEntity<List<TurnoResponseDTO>> obtenerTodosTurnos() {
        List<TurnoResponseDTO> turnos = turnoService.obtenerTodosTurnos();
        return ResponseEntity.ok(turnos);
    }
    
    /**
     * Obtiene los turnos pendientes ordenados por prioridad
     * @return Lista de turnos pendientes
     */
    @GetMapping("/pendientes")
    public ResponseEntity<List<TurnoResponseDTO>> obtenerTurnosPendientes() {
        List<TurnoResponseDTO> turnos = turnoService.obtenerTurnosPendientes();
        return ResponseEntity.ok(turnos);
    }
    
    /**
     * Obtiene el siguiente turno a atender según prioridad
     * Las prioridades son:
     * 1. Personas mayores de 60 años (Prioridad 3)
     * 2. PQRS con más de 10 minutos de espera (Prioridad 3)
     * 3. PQRS recientes (Prioridad 2)
     * 4. Resto de servicios (Prioridad 1)
     * 
     * @return Siguiente turno a atender
     */
    @GetMapping("/siguiente")
    public ResponseEntity<TurnoResponseDTO> obtenerSiguienteTurno() {
        try {
            System.out.println("📞 Llamada a /api/turnos/siguiente recibida");
            TurnoResponseDTO turno = turnoService.obtenerSiguienteTurno();
            System.out.println("✅ Turno obtenido: " + turno.getNumeroTurno());
            return ResponseEntity.ok(turno);
        } catch (RuntimeException e) {
            System.out.println("⚠️ No hay turnos pendientes: " + e.getMessage());
            return ResponseEntity.noContent().build();
        }
    }
    
    /**
     * Finaliza un turno marcándolo como ATENDIDO
     * @param id ID del turno a finalizar
     * @return Turno finalizado
     */
    @PutMapping("/{id}/finalizar")
    public ResponseEntity<TurnoResponseDTO> finalizarTurno(@PathVariable Long id) {
        try {
            TurnoResponseDTO turno = turnoService.finalizarTurno(id);
            return ResponseEntity.ok(turno);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Cancela un turno
     * @param id ID del turno a cancelar
     * @return Turno cancelado
     */
    @PutMapping("/{id}/cancelar")
    public ResponseEntity<TurnoResponseDTO> cancelarTurno(@PathVariable Long id) {
        try {
            TurnoResponseDTO turno = turnoService.cancelarTurno(id);
            return ResponseEntity.ok(turno);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
