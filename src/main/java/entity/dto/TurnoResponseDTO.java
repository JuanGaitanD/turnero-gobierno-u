package entity.dto;

import entity.turnos.EstadoTurno;
import entity.turnos.Servicio;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TurnoResponseDTO {
    private Long id;
    private String numeroTurno;
    private String usuarioNombre;
    private String usuarioDni;
    private Integer usuarioEdad;
    private Servicio servicio;
    private EstadoTurno estado;
    private Integer prioridad;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaAtencion;
}
