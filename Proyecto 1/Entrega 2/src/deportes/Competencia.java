package deportes;

import java.util.ArrayList;

import usuarios.Jugador;

public abstract class Competencia {
	private String nombre;
	private String organizador;
	private String temporada;
	private int nominaMinima;
	private Deporte deporte;
	private Categoria categoria;
	private ArrayList<Nomina> nominas;
	private ArrayList<PartidoCompetencia> partidos;
	public Competencia(String nombre, String organizador, String temporada, int nominaMinima, Deporte deporte, 
			Categoria categoria) {
		this.nombre = nombre;
		this.organizador = organizador;
		this.temporada = temporada;
		this.nominaMinima = nominaMinima;
		this.deporte = deporte;
		this.categoria = categoria;
		this.nominas = new ArrayList<>();
		this.partidos = new ArrayList<>();
	}
	public String getNombre() {
		return nombre;
	}
	public String getTemporada() {
		return temporada;
	}
	public String getOrganizador() {
		return organizador;
	}
	public int getNominaMinima() {
		return nominaMinima;
	}
	public Deporte getDeporte() {
		return deporte;
	}
	public Categoria getCategoria() {
		return categoria;
	}
	public ArrayList<Nomina> getNominas() {
		return nominas;
	}
	public ArrayList<PartidoCompetencia> getPartidos() {
		return partidos;
	}
	
	public boolean inscribirEquipo(Equipo e, ArrayList<Jugador> jugadores) {
		if (e.getCategoria() != categoria) {
			return false;
		}
		if (e.getDeporte() != deporte) {
			return false;
		}
		if (jugadores.size() < nominaMinima) {
			return false;
		}
		Nomina n = new Nomina(e);
		for (Jugador j : jugadores) {
			n.agregarJugador(j);
		}
		nominas.add(n);
		return true;
	}
	public Jugador getJugadorDestacado() {
		Jugador mejor = null;
		int mejorValor = -1;
		for (PartidoCompetencia p : partidos) {
			for (Estadistica est : p.getEstadisticas()) {
				if (est.getValorDestacado() > mejorValor) {
					mejorValor = est.getValorDestacado();
					mejor = est.getJugador();
				}
			}
		}
		return mejor;
	}

	public ArrayList<Equipo> getEquipos() {
		ArrayList<Equipo> equipos = new ArrayList<>();
		for (Nomina n : nominas) {
			equipos.add(n.getEquipo());
		}
		return equipos;
	}

	public abstract void registrarPartido(PartidoCompetencia p);

}
