package entity.dto;

import entity.usuarios.Genero;
import lombok.Data;
import java.time.LocalDate;

@Data
public class UsuarioRequestDTO {
    private String dni;
    private String nombreCompleto;
    private Genero genero;
    private LocalDate fechaNacimiento; // formato ISO (YYYY-MM-DD)
}
