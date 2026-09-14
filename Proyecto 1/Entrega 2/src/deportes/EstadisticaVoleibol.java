package deportes;

import usuarios.Jugador;

public class EstadisticaVoleibol extends Estadistica {
	private int aces;
	private int bloqueos;
	
	public EstadisticaVoleibol(Jugador jugador, int aces, int bloqueos) {
		super(jugador);
		this.aces = aces;
		this.bloqueos = bloqueos;
	}
	

	public int getAces() {
		return aces;
	}


	public int getBloqueos() {
		return bloqueos;
	}


	@Override
	public void acumular(Estadistica otra) {
		EstadisticaVoleibol o = (EstadisticaVoleibol) otra;
		this.aces += o.getAces();
		this.bloqueos += o.getBloqueos();

	}

	@Override
	public int getValorDestacado() {
		return this.aces;
	}
	@Override
	public Estadistica crearVacia() {
		return new EstadisticaFutbol(getJugador(), 0, 0);
	}
}
