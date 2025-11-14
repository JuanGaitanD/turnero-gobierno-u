package entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "usuarios")
public class usuarios {

    public enum Genero {
        H, M
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_usuario;
    
    @Column(name = "dni")
    private String dni;
    
    private String nombre_completo;

    @Enumerated(EnumType.STRING)
    private Genero genero;  
    
    private LocalDate fecha_nacimiento;
    private int edad;

}
