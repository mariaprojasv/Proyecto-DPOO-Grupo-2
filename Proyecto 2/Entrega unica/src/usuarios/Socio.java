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
	
    public void acreditarPuntos(int puntos) {
        if (puntos <= 0) {
            throw new IllegalArgumentException("Los puntos a acreditar deben ser positivos");
        }
        puntosFidelidad += puntos;
    }

    public void redimirPuntos(int puntos) {
        if (puntos <= 0) {
            throw new IllegalArgumentException("Los puntos a redimir deben ser positivos");
        }
        if (puntos > puntosFidelidad) {
            throw new IllegalStateException("El socio no tiene puntos suficientes");
        }
        puntosFidelidad -= puntos;
    }
}
