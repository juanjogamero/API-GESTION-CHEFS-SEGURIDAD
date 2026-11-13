package proyecto.service;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import proyecto.entity.Noticia;
import proyecto.respository.NoticiaRepository;

@Service
public class NoticiaService {
    private final NoticiaRepository noticias;
    public NoticiaService(NoticiaRepository noticias) { this.noticias=noticias; }
    public List<Noticia> findAll() { return noticias.findAll(); }
    public Optional<Noticia> findById(int id) { return noticias.findById(id); }

    public Noticia save(Noticia noticia) {
        noticia.setId(0); noticia.setVersion(0);
        return noticias.save(noticia);
    }
    @Transactional
    public Noticia update(int id, Noticia updated) {
        Optional<Noticia> original = noticias.findById(id);
        if (original.isEmpty()) return null;
        Noticia noticia=original.get();
        noticia.setEncabezado(updated.getEncabezado());
        noticia.setCuerpoNoticia(updated.getCuerpoNoticia());
        noticia.setImagenNoticia(updated.getImagenNoticia());
        return noticias.save(noticia);
    }
    @Transactional
    public boolean deleteById(int id) {
        if (!noticias.existsById(id)) return false;
        noticias.deleteById(id);
        return true;
    }
}
