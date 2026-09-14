package deportes;

import java.time.LocalDate;

public class PartidoCompetencia extends Partido {
	private String institucionRival;
	private String escenario;

	public PartidoCompetencia(LocalDate fecha, Deporte deporte, Equipo equipoClub, String institucionRival, String escenario) {
		super(fecha, deporte, equipoClub);
		this.institucionRival = institucionRival;
		this.escenario = escenario;
	}

	public String getInstitucionRival() {
		return institucionRival;
	}

	public String getEscenario() {
		return escenario;
	}

	@Override
	public boolean puedeJugarse() {
		return getEquipoClub().tieneMinimoJugadores();
	}
}