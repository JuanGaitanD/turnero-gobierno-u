package entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name="turnos")
public class turnos {
    public enum Servicio {
        tramite_vivienda, atencion_tributaria, pqrs
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime timestamp;
    private Boolean isPrioridad;
    
    @Enumerated(EnumType.STRING)
    private Servicio servicio;
    
    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private usuarios usuario;
}
