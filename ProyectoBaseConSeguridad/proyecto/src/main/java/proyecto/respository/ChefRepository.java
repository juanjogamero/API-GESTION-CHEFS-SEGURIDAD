package proyecto.respository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import jakarta.persistence.LockModeType;
import proyecto.entity.Chef;

@Repository
public interface ChefRepository extends JpaRepository<Chef, Integer> {
    Optional<Chef> findByUsername(String username);

    @Query("SELECT c FROM Chef c WHERE c.activo = true AND c.baneado = false")
    List<Chef> getListChefActivos();

    @Query("SELECT c FROM Chef c JOIN c.serviciosOfrecidos s WHERE s.id = ?1")
    Chef findByServicioId(int idServicio);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Chef c WHERE c.id = :id")
    Optional<Chef> findByIdForUpdate(@Param("id") int id);
}
