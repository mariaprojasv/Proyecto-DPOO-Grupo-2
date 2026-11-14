package torneos;

import java.util.ArrayList;
import java.util.List;
import usuarios.Usuario;

/** Jugador que compite solo en una disciplina individual. */
public class CompetidorIndividual extends Competidor {
    private final Usuario usuario;

    public CompetidorIndividual(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("El participante es obligatorio");
        }
        this.usuario = usuario;
    }

    public Usuario getUsuario() { return usuario; }

    @Override public String getNombre() { return usuario.getNombre(); }

    @Override public List<Usuario> getIntegrantes() {
        List<Usuario> lista = new ArrayList<>();
        lista.add(usuario);
        return lista;
    }
}
