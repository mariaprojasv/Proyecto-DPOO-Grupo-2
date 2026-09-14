package deportes;

import usuarios.Jugador;

public class Tarjeta {
	private Jugador jugador;
	private String tipo;
	public Tarjeta(Jugador jugador, String tipo) {
		this.jugador = jugador;
		this.tipo = tipo;
	}
	public Jugador getJugador() {
		return jugador;
	}
	public String getTipo() {
		return tipo;
	}
	
}
