package deportes;

import java.time.LocalDate;

public class PartidoInterno extends Partido {
	private Equipo rival;

	public PartidoInterno(LocalDate fecha, Deporte deporte, Equipo equipoClub, Equipo rival) {
		super(fecha, deporte, equipoClub);
		this.rival = rival;
	}

	public Equipo getRival() {
		return rival;
	}

	@Override
	public boolean puedeJugarse() {
		return getEquipoClub().tieneMinimoJugadores() && rival.tieneMinimoJugadores();
	}
}