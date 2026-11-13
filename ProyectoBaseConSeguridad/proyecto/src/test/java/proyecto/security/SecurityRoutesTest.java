package proyecto.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityRoutesTest {
    @Autowired MockMvc mvc;

    @Test void visitantesSoloUsanLasRutasPublicas() throws Exception {
        mvc.perform(get("/chef/listadoActivos")).andExpect(status().isOk());
        mvc.perform(get("/chef")).andExpect(status().isOk());
        mvc.perform(get("/chef/miPerfil")).andExpect(status().isUnauthorized());
        mvc.perform(get("/cliente/miPerfil")).andExpect(status().isUnauthorized());
        mvc.perform(post("/noticia").contentType("application/json").content("{}"))
            .andExpect(status().isUnauthorized());
        mvc.perform(post("/chef/42").contentType("application/json").content("{}"))
            .andExpect(status().isUnauthorized());
        mvc.perform(put("/banear/42")).andExpect(status().isUnauthorized());
    }

    @Test @WithMockUser(username="clientePrueba",authorities={"CLIENTE"})
    void clienteNoPuedeAdministrarNiVerDatosDeChef() throws Exception {
        mvc.perform(put("/banear/42")).andExpect(status().isForbidden());
        mvc.perform(post("/noticia").contentType("application/json").content("{}"))
            .andExpect(status().isForbidden());
        mvc.perform(get("/chef/miPerfil")).andExpect(status().isForbidden());
        mvc.perform(get("/cliente")).andExpect(status().isForbidden());
    }

    @Test @WithMockUser(username="chefPrueba",authorities={"CHEF"})
    void chefNoPuedeContratarNiBanear() throws Exception {
        mvc.perform(put("/banear/42")).andExpect(status().isForbidden());
        mvc.perform(post("/servicio/contratar/1").contentType("application/json").content("{}"))
            .andExpect(status().isForbidden());
        mvc.perform(get("/cliente/miPerfil")).andExpect(status().isForbidden());
    }

    @Test @WithMockUser(username="adminPrueba",authorities={"ADMIN"})
    void administradorPuedeAccederAlPanelProtegido() throws Exception {
        mvc.perform(get("/cliente")).andExpect(status().isOk());
        mvc.perform(get("/chef/miPerfil")).andExpect(status().isForbidden());
    }
}
