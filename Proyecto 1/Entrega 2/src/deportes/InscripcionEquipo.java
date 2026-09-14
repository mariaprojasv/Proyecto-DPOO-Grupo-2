package deportes;

import usuarios.Jugador;

public class InscripcionEquipo {
	private Jugador jugador;
	private String posicion;
	private int numeroCamiseta;
	public InscripcionEquipo(Jugador jugador, String posicion, int numeroCamiseta) {
		this.jugador = jugador;
		this.posicion = posicion;
		this.numeroCamiseta = numeroCamiseta;
	}
	public Jugador getJugador() {
		return jugador;
	}
	public String getPosicion() {
		return posicion;
	}
	public int getNumeroCamiseta() {
		return numeroCamiseta;
	}
	
}
