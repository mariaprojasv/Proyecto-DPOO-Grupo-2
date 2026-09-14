package deportes;

import usuarios.Jugador;

public class EstadisticaBaloncesto extends Estadistica {
	private int puntos;
	private int rebotes;
	
	public EstadisticaBaloncesto(Jugador jugador, int puntos, int rebotes) {
		super(jugador);
		this.puntos = puntos;
		this.rebotes = rebotes;
	}
	

	public int getPuntos() {
		return puntos;
	}


	public int getRebotes() {
		return rebotes;
	}


	@Override
	public void acumular(Estadistica otra) {
		EstadisticaBaloncesto o = (EstadisticaBaloncesto) otra;
		this.puntos += o.getPuntos();
		this.rebotes += o.getRebotes();

	}

	@Override
	public int getValorDestacado() {
		return this.puntos;
	}

}
