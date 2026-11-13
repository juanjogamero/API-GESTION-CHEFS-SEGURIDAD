package proyecto.entity;

import java.time.LocalDate;
import java.util.Date;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Entity
public class Servicio extends DomainEntity {
    @NotNull private Date momentoContratacion;
    @NotNull private LocalDate fechaServicio;
    private String comentario;
    @Min(1) @Max(5) private Integer valoracionServicio;
    public Servicio() {}
    public Servicio(Date momentoContratacion,LocalDate fechaServicio,String comentario,Integer valoracionServicio) {
        this.momentoContratacion=momentoContratacion;this.fechaServicio=fechaServicio;
        this.comentario=comentario;this.valoracionServicio=valoracionServicio;
    }
    public Date getMomentoContratacion() { return momentoContratacion; }
    public void setMomentoContratacion(Date momentoContratacion) { this.momentoContratacion=momentoContratacion; }
    public LocalDate getFechaServicio() { return fechaServicio; }
    public void setFechaServicio(LocalDate fechaServicio) { this.fechaServicio=fechaServicio; }
    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario=comentario; }
    public Integer getValoracionServicio() { return valoracionServicio; }
    public void setValoracionServicio(Integer valoracionServicio) { this.valoracionServicio=valoracionServicio; }
}
