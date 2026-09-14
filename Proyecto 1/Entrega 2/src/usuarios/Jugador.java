package usuarios;

import java.time.LocalDate;
import java.time.Period;
import deportes.Estadistica;
import deportes.Competencia;
import deportes.PartidoCompetencia;

public class Jugador extends Socio {
	private LocalDate fechaNacimiento;

	public Jugador(String login, String password, String nombre, int puntosFidelidad, LocalDate fechaNacimiento) {
		super(login, password, nombre, puntosFidelidad);
		this.fechaNacimiento = fechaNacimiento;
	}

	public LocalDate getFechaNacimiento() {
		return fechaNacimiento;
	}

	public int getEdad() {
		return Period.between(fechaNacimiento, LocalDate.now()).getYears();
	}
	public Estadistica getEstadisticasAcumuladas(Competencia c) {
		Estadistica acumulada = null;
		for (PartidoCompetencia p : c.getPartidos()) {
			for (Estadistica est : p.getEstadisticas()) {
				if (est.getJugador() == this) {
					if (acumulada == null) {
						acumulada = est.crearVacia();
					}
					acumulada.acumular(est);
				}
			}
		}
		return acumulada;
	}
}
