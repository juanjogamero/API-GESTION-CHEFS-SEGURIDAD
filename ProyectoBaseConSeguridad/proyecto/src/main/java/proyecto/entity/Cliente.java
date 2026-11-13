package proyecto.entity;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;

@Entity
public class Cliente extends Actor {
    private String datosMedicos;
    @NotBlank private String direccionPostal;
    @JsonIgnore @OneToMany private List<Servicio> serviciosContratados = new ArrayList<>();
    public Cliente() {}
    public String getDatosMedicos() { return datosMedicos; }
    public void setDatosMedicos(String datosMedicos) { this.datosMedicos=datosMedicos; }
    public String getDireccionPostal() { return direccionPostal; }
    public void setDireccionPostal(String direccionPostal) { this.direccionPostal=direccionPostal; }
    public List<Servicio> getServiciosContratados() { return serviciosContratados; }
    public void setServiciosContratados(List<Servicio> serviciosContratados) { this.serviciosContratados=serviciosContratados; }
}
