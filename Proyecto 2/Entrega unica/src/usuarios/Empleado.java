package usuarios;

public abstract class Empleado extends Usuario {
    public Empleado(String login, String password, String nombre) {
        super(login, password, nombre);
    }
}
