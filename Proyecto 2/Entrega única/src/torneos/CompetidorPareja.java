package torneos;

import java.util.ArrayList;
import java.util.List;
import usuarios.Usuario;

/** Pareja inscrita en un torneo de dobles. */
public class CompetidorPareja extends Competidor {
    private final Usuario primero;
    private final Usuario segundo;

    public CompetidorPareja(Usuario primero, Usuario segundo) {
        if (primero == null || segundo == null || primero == segundo) {
            throw new IllegalArgumentException("Una pareja requiere dos participantes distintos");
        }
        this.primero = primero;
        this.segundo = segundo;
    }

    public Usuario getPrimero() { return primero; }
    public Usuario getSegundo() { return segundo; }

    @Override public String getNombre() { return primero.getNombre() + " / " + segundo.getNombre(); }

    @Override public List<Usuario> getIntegrantes() {
        List<Usuario> lista = new ArrayList<>();
        lista.add(primero);
        lista.add(segundo);
        return lista;
    }
}
