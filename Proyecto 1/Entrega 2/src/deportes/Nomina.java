package deportes;

import java.util.ArrayList;
import usuarios.Jugador;

public class Nomina {
	private Equipo equipo;
	private ArrayList<Jugador> jugadores;

	public Nomina(Equipo equipo) {
		this.equipo = equipo;
		this.jugadores = new ArrayList<>();
	}

	public Equipo getEquipo() {
		return equipo;
	}

	public ArrayList<Jugador> getJugadores() {
		return jugadores;
	}

	public void agregarJugador(Jugador j) {
		jugadores.add(j);
	}

	public boolean estaHabilitado(Jugador j) {
		return jugadores.contains(j);
	}
}
