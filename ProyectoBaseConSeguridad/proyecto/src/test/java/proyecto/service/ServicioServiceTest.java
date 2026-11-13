package proyecto.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import proyecto.entity.Chef;
import proyecto.entity.Cliente;
import proyecto.entity.Servicio;
import proyecto.respository.ServicioRepository;
import proyecto.security.JWTUtils;

class ServicioServiceTest {
    ServicioRepository repository=mock(ServicioRepository.class);
    ClienteService clientes=mock(ClienteService.class);
    ChefService chefs=mock(ChefService.class);
    JWTUtils jwt=mock(JWTUtils.class);
    ServicioService service;

    @BeforeEach void setup() { service=new ServicioService(repository,clientes,chefs,jwt); }
    Servicio solicitud(LocalDate fecha) {
        Servicio s=new Servicio();
        s.setFechaServicio(fecha);
        return s;
    }

    @Test void noAceptaFechasPasadas() {
        assertNull(service.contratarServicio(solicitud(LocalDate.now().minusDays(1)),3));
        verifyNoInteractions(repository,chefs);
    }

    @Test void noAceptaChefsDesactivados() {
        Chef chef=new Chef();
        chef.setActivo(false);
        when(chefs.findByIdForUpdate(3)).thenReturn(Optional.of(chef));
        assertNull(service.contratarServicio(solicitud(LocalDate.now().plusDays(3)),3));
        verify(repository,never()).save(any());
    }

    @Test void noReservaDosVecesAlMismoChefYDia() {
        Chef chef=new Chef();
        chef.setActivo(true);
        LocalDate fecha=LocalDate.now().plusDays(3);
        chef.setServiciosOfrecidos(List.of(solicitud(fecha)));
        when(chefs.findByIdForUpdate(3)).thenReturn(Optional.of(chef));
        when(jwt.<Cliente>userLogin()).thenReturn(new Cliente());
        assertNull(service.contratarServicio(solicitud(fecha),3));
        verify(repository,never()).save(any());
    }

    @Test void noPermitePuntuarAntesDeLaFechaDelServicio() {
        Servicio s=solicitud(LocalDate.now().plusDays(3));
        s.setId(22);
        when(repository.findById(22)).thenReturn(Optional.of(s));
        assertFalse(service.puntuarServicio(s,5));
        verify(repository,never()).save(any());
    }
}
