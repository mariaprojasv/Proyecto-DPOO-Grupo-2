package deportes;

import usuarios.Jugador;

public abstract class Estadistica {
	private Jugador jugador;

	public Estadistica(Jugador jugador) {
		this.jugador = jugador;
	}

	public Jugador getJugador() {
		return jugador;
	}

	public abstract void acumular(Estadistica otra);

	public abstract int getValorDestacado();
	public abstract Estadistica crearVacia();
}
