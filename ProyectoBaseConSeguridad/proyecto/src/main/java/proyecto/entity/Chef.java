package proyecto.entity;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@Entity
public class Chef extends Actor {
    @Min(0) private Double precioServicio;
    @Min(0) @Max(5) private Double valoracionChef;
    private boolean activo;
    @JsonIgnore @OneToMany private List<Servicio> serviciosOfrecidos = new ArrayList<>();

    public Chef() {}
    public Chef(Double precioServicio, Double valoracionChef, boolean activo, List<Servicio> serviciosOfrecidos) {
        this.precioServicio=precioServicio; this.valoracionChef=valoracionChef; this.activo=activo;
        this.serviciosOfrecidos=serviciosOfrecidos;
    }
    public Double getPrecioServicio() { return precioServicio; }
    public void setPrecioServicio(Double precioServicio) { this.precioServicio=precioServicio; }
    public Double getValoracionChef() { return valoracionChef; }
    public void setValoracionChef(Double valoracionChef) { this.valoracionChef=valoracionChef; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo=activo; }
    public List<Servicio> getServiciosOfrecidos() { return serviciosOfrecidos; }
    public void setServiciosOfrecidos(List<Servicio> serviciosOfrecidos) { this.serviciosOfrecidos=serviciosOfrecidos; }
}
