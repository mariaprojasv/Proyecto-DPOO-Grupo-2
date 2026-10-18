package usuarios;

public class Entrenador extends Empleado {
    private String especialidad;
    public Entrenador(String login, String password, String nombre, String especialidad) {
        super(login, password, nombre);
        this.especialidad = especialidad;
    }
    public String getEspecialidad() {
	return especialidad;
	}
}
