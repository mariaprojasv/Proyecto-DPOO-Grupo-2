package deportes;

import java.time.LocalDate;
import java.util.ArrayList;

public abstract class Partido {
	private LocalDate fecha;
	private Deporte deporte;
	private Equipo equipoClub;
	private int marcadorLocal;
	private int marcadorVisitante;
	private ArrayList<Estadistica> estadisticas;
	private ArrayList<Tarjeta> tarjetas;

	public Partido(LocalDate fecha, Deporte deporte, Equipo equipoClub) {
		this.fecha = fecha;
		this.deporte = deporte;
		this.equipoClub = equipoClub;
		this.marcadorLocal = 0;
		this.marcadorVisitante = 0;
		this.estadisticas = new ArrayList<>();
		this.tarjetas = new ArrayList<>();
	}

	public LocalDate getFecha() {
		return fecha;
	}

	public Deporte getDeporte() {
		return deporte;
	}

	public Equipo getEquipoClub() {
		return equipoClub;
	}

	public int getMarcadorLocal() {
		return marcadorLocal;
	}

	public int getMarcadorVisitante() {
		return marcadorVisitante;
	}

	public ArrayList<Estadistica> getEstadisticas() {
		return estadisticas;
	}

	public ArrayList<Tarjeta> getTarjetas() {
		return tarjetas;
	}
	public abstract boolean puedeJugarse();
	
	public void registrarMarcador(int local, int visitante) {
		this.marcadorLocal = local;
		this.marcadorVisitante =visitante;
		
	}
	public void registrarEstadistica(Estadistica e) {
		estadisticas.add(e);
	}
	public void registrarTarjeta(Tarjeta t) {
		tarjetas.add(t);
	}
	public String getResultado() {
		if (marcadorLocal > marcadorVisitante) {
			return "VICTORIA";
		} 
		else if (marcadorLocal == marcadorVisitante) {
			return "EMPATE";
		}
		return "DERROTA";
	}
}

	
