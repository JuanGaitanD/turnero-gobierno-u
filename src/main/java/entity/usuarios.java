package entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.time.Period;

@Data
@Entity
@Table(name = "usuarios")
@NoArgsConstructor
@AllArgsConstructor
public class usuarios {

    public enum Genero {
        H, M
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_usuario;

    @Column(name = "dni", nullable = false, unique = true)
    private String dni;

    @Column(nullable = false)
    private String nombre_completo;

    @Enumerated(EnumType.STRING)
    private Genero genero;

    private LocalDate fecha_nacimiento;

    @Column(nullable = false)
    private int edad;

    @PrePersist
    @PreUpdate
    private void calcularEdad() {
        if (fecha_nacimiento != null) {
            this.edad = Period.between(fecha_nacimiento, LocalDate.now()).getYears();
        }
    }
}
