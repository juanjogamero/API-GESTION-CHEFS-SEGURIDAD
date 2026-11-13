package proyecto.service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import proyecto.entity.Chef;
import proyecto.entity.Cliente;
import proyecto.entity.Servicio;
import proyecto.respository.ServicioRepository;
import proyecto.security.JWTUtils;

@Service
public class ServicioService {
    private final ServicioRepository servicios;
    private final ClienteService clientes;
    private final ChefService chefs;
    private final JWTUtils jwt;

    public ServicioService(ServicioRepository servicios, ClienteService clientes,
                           ChefService chefs, JWTUtils jwt) {
        this.servicios=servicios; this.clientes=clientes; this.chefs=chefs; this.jwt=jwt;
    }

    public Optional<Servicio> findById(int id) { return servicios.findById(id); }

    @Transactional
    public Servicio contratarServicio(Servicio solicitud, int chefId) {
        if (solicitud == null || solicitud.getFechaServicio() == null
            || !solicitud.getFechaServicio().isAfter(LocalDate.now())) return null;

        Chef chef = chefs.findByIdForUpdate(chefId).orElse(null);
        if (chef == null || !chef.isActivo() || chef.isBaneado()) return null;

        Cliente cliente = jwt.userLogin();
        if (cliente == null || cliente.isBaneado()) return null;
        if (chef.getServiciosOfrecidos().stream()
            .anyMatch(s -> solicitud.getFechaServicio().equals(s.getFechaServicio()))) return null;

        // No confiar en las fechas, identidades ni valoraciones suministradas por el cliente.
        Servicio servicio = new Servicio();
        servicio.setFechaServicio(solicitud.getFechaServicio());
        servicio.setComentario(solicitud.getComentario());
        servicio.setMomentoContratacion(new Date());
        servicio.setValoracionServicio(null);
        Servicio guardado = servicios.save(servicio);
        cliente.getServiciosContratados().add(guardado);
        clientes.saveBasico(cliente);
        chef.getServiciosOfrecidos().add(guardado);
        chefs.saveBasico(chef);
        return guardado;
    }

    @Transactional
    public boolean puntuarServicio(Servicio referencia, int puntuacion) {
        if (puntuacion < 1 || puntuacion > 5 || referencia == null) return false;
        Servicio servicio = servicios.findById(referencia.getId()).orElse(null);
        if (servicio == null || servicio.getFechaServicio() == null
            || !servicio.getFechaServicio().isBefore(LocalDate.now())
            || servicio.getValoracionServicio() != null) return false;

        Cliente cliente = jwt.userLogin();
        if (cliente == null || !cliente.getServiciosContratados().stream()
            .anyMatch(s -> s.getId() == servicio.getId())) return false;
        Chef chef = chefs.findByServicioId(servicio.getId());
        if (chef == null) return false;

        servicio.setValoracionServicio(puntuacion);
        servicios.save(servicio);
        double total=0; int num=0;
        for (Servicio s : chef.getServiciosOfrecidos()) {
            if (s.getValoracionServicio() != null) {
                total += s.getValoracionServicio(); num++;
            }
        }
        chef.setValoracionChef(num == 0 ? 0.0 : total / num);
        // Solo guardar la media, nunca volver a cifrar una contraseña ya cifrada.
        chefs.saveBasico(chef);
        return true;
    }

    @Transactional(readOnly=true)
    public List<Servicio> getAllByChefLogin() {
        Chef chef = jwt.userLogin();
        return chef == null ? List.of() : List.copyOf(chef.getServiciosOfrecidos());
    }
    @Transactional(readOnly=true)
    public List<Servicio> getAllByClienteLogin() {
        Cliente cliente = jwt.userLogin();
        return cliente == null ? List.of() : List.copyOf(cliente.getServiciosContratados());
    }
}
