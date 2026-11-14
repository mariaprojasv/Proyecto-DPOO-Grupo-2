package usuarios;

/** Persona ajena a la comunidad universitaria y al club que participa en torneos internos. */
public class PersonaExterna extends Usuario {
    public PersonaExterna(String login, String password, String nombre) {
        super(login, password, nombre);
    }
}
