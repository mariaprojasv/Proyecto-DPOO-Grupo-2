package deportes;

import usuarios.Jugador;

public class EstadisticaFutbol extends Estadistica {
	private int goles;
	private int asistencias;
	
	public EstadisticaFutbol(Jugador jugador, int goles, int asistencias) {
		super(jugador);
		this.goles = goles;
		this.asistencias = asistencias;
	}
	

	public int getGoles() {
		return goles;
	}


	public int getAsistencias() {
		return asistencias;
	}


	@Override
	public void acumular(Estadistica otra) {
		EstadisticaFutbol o = (EstadisticaFutbol) otra;
		this.goles += o.getGoles();
		this.asistencias += o.getAsistencias();
	}

	@Override
	public int getValorDestacado() {
		return this.goles;
	}
	@Override
	public Estadistica crearVacia() {
		return new EstadisticaFutbol(getJugador(), 0, 0);
	}
}
