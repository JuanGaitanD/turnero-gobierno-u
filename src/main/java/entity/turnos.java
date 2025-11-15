package entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name="turnos")
@NoArgsConstructor
@AllArgsConstructor
public class turnos {
    public enum Servicio {
        tramite_vivienda, atencion_tributaria, pqrs
    }
    
    public enum EstadoTurno {
        PENDIENTE, EN_ATENCION, ATENDIDO, CANCELADO
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private usuarios usuario;
    
    @Column(nullable = false, unique = true)
    private String numeroTurno;
    
    @Column(nullable = false)
    private LocalDateTime fechaCreacion;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Servicio servicio;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoTurno estado;
    
    @Column(nullable = false)
    private Integer prioridad;
    
    private LocalDateTime fechaAtencion;
    
    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        estado = EstadoTurno.PENDIENTE;
    }
}
