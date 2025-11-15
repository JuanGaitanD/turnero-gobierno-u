package entity.dto;

import entity.usuarios.Genero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponseDTO {
    private Long id;
    private String dni;
    private String nombreCompleto;
    private Genero genero;
    private LocalDate fechaNacimiento;
    private int edad;
}
