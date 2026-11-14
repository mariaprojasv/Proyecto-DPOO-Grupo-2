package torneos;

import java.util.List;
import usuarios.Usuario;

/** Quien compite en un torneo: un equipo, un jugador o una pareja de dobles. */
public abstract class Competidor {
    public abstract String getNombre();

    public abstract List<Usuario> getIntegrantes();

    public boolean incluye(Usuario usuario) {
        return usuario != null && getIntegrantes().contains(usuario);
    }
}
