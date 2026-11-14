package torneos;

import java.util.ArrayList;
import java.util.List;
import deportes.Equipo;
import usuarios.Usuario;

/** Equipo completo inscrito en un torneo de deporte de conjunto (se guarda la plantilla al inscribirse). */
public class CompetidorEquipo extends Competidor {
    private final Equipo equipo;
    private final List<Usuario> plantilla;

    public CompetidorEquipo(Equipo equipo) {
        if (equipo == null) {
            throw new IllegalArgumentException("El equipo es obligatorio");
        }
        this.equipo = equipo;
        this.plantilla = new ArrayList<>();
        plantilla.addAll(equipo.getIntegrantes());
    }

    public Equipo getEquipo() { return equipo; }

    @Override public String getNombre() { return equipo.getNombre(); }

    @Override public List<Usuario> getIntegrantes() { return new ArrayList<>(plantilla); }
}
