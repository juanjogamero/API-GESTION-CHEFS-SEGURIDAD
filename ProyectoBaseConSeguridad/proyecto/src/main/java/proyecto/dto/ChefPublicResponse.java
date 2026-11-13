package proyecto.dto;

import proyecto.entity.Chef;

public record ChefPublicResponse(int id, String nombre, String primerApellido,
                                 String imagenPerfil, Double precioServicio, Double valoracionChef) {
    public static ChefPublicResponse from(Chef chef) {
        return new ChefPublicResponse(chef.getId(), chef.getNombre(), chef.getPrimerApellido(),
            chef.getImagenPerfil(), chef.getPrecioServicio(), chef.getValoracionChef());
    }
}
