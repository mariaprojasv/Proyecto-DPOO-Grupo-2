package deportes;

import java.time.LocalDate;
import java.util.ArrayList;
import usuarios.Jugador;

public class AreaDeportiva {
	private ArrayList<Deporte> deportes;
	private ArrayList<Categoria> categorias;
	private ArrayList<Equipo> equipos;
	private ArrayList<Partido> partidos;
	private ArrayList<Competencia> competencias;

	public AreaDeportiva() {
		this.deportes = new ArrayList<>();
		this.categorias = new ArrayList<>();
		this.equipos = new ArrayList<>();
		this.partidos = new ArrayList<>();
		this.competencias = new ArrayList<>();
	}

	public ArrayList<Deporte> getDeportes() { return deportes; }
	public ArrayList<Categoria> getCategorias() { return categorias; }
	public ArrayList<Equipo> getEquipos() { return equipos; }
	public ArrayList<Partido> getPartidos() { return partidos; }
	public ArrayList<Competencia> getCompetencias() { return competencias; }

	public void agregarDeporte(Deporte d) {
		deportes.add(d);
	}

	public void agregarCategoria(Categoria c) {
		categorias.add(c);
	}

	public Equipo crearEquipo(String nombre, Deporte deporte, Categoria categoria, int cupoMaximo) {
		Equipo e = new Equipo(nombre, cupoMaximo, deporte, categoria);
		equipos.add(e);
		return e;
	}

	public ArrayList<Equipo> getEquiposDeJugador(Jugador j) {
		ArrayList<Equipo> resultado = new ArrayList<>();
		for (Equipo e : equipos) {
			if (e.getJugadores().contains(j)) {
				resultado.add(e);
			}
		}
		return resultado;
	}

	public boolean inscribirJugadorEnEquipo(Jugador j, Equipo equipo, String posicion, int numero) {
		for (Equipo e : getEquiposDeJugador(j)) {
			if (e.getDeporte() == equipo.getDeporte()) {
				return false;
			}
		}
		return equipo.agregarJugador(j, posicion, numero);
	}

	public void darDeBajaEquipo(Equipo e) {
		equipos.remove(e);
	}

	public boolean estaSuspendido(Jugador j, Equipo equipo) {
		Partido ultimo = null;
		for (Partido p : partidos) {
			if (p.getEquipoClub() == equipo) {
				ultimo = p;
			}
		}
		if (ultimo == null) {
			return false;
		}
		int amarillas = 0;
		for (Tarjeta t : ultimo.getTarjetas()) {
			if (t.getJugador() == j) {
				if (t.getTipo().equals("ROJA")) {
					return true;
				}
				amarillas += 1;
			}
		}
		return amarillas >= 2;
	}

	public PartidoInterno programarPartidoInterno(LocalDate fecha, Equipo local, Equipo visitante) {
		if (local.getDeporte() != visitante.getDeporte() || local.getCategoria() != visitante.getCategoria()) {
			return null;
		}
		PartidoInterno p = new PartidoInterno(fecha, local.getDeporte(), local, visitante);
		if (!p.puedeJugarse()) {
			return null;
		}
		partidos.add(p);
		return p;
	}

	public void crearCompetencia(Competencia c) {
		competencias.add(c);
	}

	public boolean inscribirEquipoEnCompetencia(Equipo e, Competencia c, ArrayList<Jugador> jugadores) {
		return c.inscribirEquipo(e, jugadores);
	}

	public void registrarPartidoCompetencia(Competencia c, PartidoCompetencia p) {
		c.registrarPartido(p);
		partidos.add(p);
	}
}