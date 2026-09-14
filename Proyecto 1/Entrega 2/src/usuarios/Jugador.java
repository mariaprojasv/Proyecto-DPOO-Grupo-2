package usuarios;

import java.time.LocalDate;
import java.time.Period;

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
}
