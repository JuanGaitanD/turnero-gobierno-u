package entity.dto;

import entity.turnos.Servicio;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TurnoRequestDTO {
    private Long usuarioId;
    private Servicio servicio;
}
