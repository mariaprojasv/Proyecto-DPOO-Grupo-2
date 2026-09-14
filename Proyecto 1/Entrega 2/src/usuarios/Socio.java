package usuarios;

public class Socio extends Usuario {
	private int puntosFidelidad;

	public Socio(String login, String password, String nombre, int puntosFidelidad) {
		super(login, password, nombre);
		this.puntosFidelidad = puntosFidelidad;
	}

	public int getPuntosFidelidad() {
		return puntosFidelidad;
	}
	
}
