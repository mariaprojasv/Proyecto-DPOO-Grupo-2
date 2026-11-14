package usuarios;

/** Estudiante de la universidad del club. Solo puede participar en torneos si esta matriculado. */
public class Estudiante extends Usuario {
    private final String codigoEstudiantil;
    private boolean matriculado;

    public Estudiante(String login, String password, String nombre, String codigoEstudiantil, boolean matriculado) {
        super(login, password, nombre);
        if (codigoEstudiantil == null || codigoEstudiantil.trim().isEmpty()) {
            throw new IllegalArgumentException("El codigo estudiantil es obligatorio");
        }
        this.codigoEstudiantil = codigoEstudiantil.trim();
        this.matriculado = matriculado;
    }

    public String getCodigoEstudiantil() {
        return codigoEstudiantil;
    }

    public boolean estaMatriculado() {
        return matriculado;
    }

    public void setMatriculado(boolean matriculado) {
        this.matriculado = matriculado;
    }
}
