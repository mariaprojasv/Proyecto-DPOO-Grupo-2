package deportes;

import java.time.LocalDate;
import java.util.ArrayList;
import usuarios.Jugador;

public abstract class Partido {
	private LocalDate fecha;
	private Deporte deporte;
	private Equipo equipoClub;
	private int marcadorLocal;
	private int marcadorVisitante;
	private ArrayList<Estadistica> estadisticas;
	private ArrayList<Tarjeta> tarjetas;
	private ControlSanciones controlSanciones;

	public Partido(LocalDate fecha, Deporte deporte, Equipo equipoClub) {
		this.fecha = fecha;
		this.deporte = deporte;
		this.equipoClub = equipoClub;
		this.marcadorLocal = 0;
		this.marcadorVisitante = 0;
		this.estadisticas = new ArrayList<>();
		this.tarjetas = new ArrayList<>();
		this.controlSanciones = null;
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
		if (local < 0 || visitante < 0) {
			return;
		}
		this.marcadorLocal = local;
		this.marcadorVisitante = visitante;
		
	}
	public void registrarEstadistica(Estadistica e) {
	    estadisticas.add(e);
	}
	public void establecerControlSanciones(ControlSanciones control) {
        controlSanciones = control;
    }

    public boolean puedeAlinearse(Jugador jugador) {
        if (jugador == null || equipoClub == null || !equipoClub.getJugadores().contains(jugador)) {
            return false;
        }
        if (controlSanciones != null && !controlSanciones.puedeAlinearse(jugador)) {
            return false;
        }
        return true;
    }

    public boolean registrarTarjeta(Tarjeta t) {
        if (t == null || t.getJugador() == null || t.getTipo() == null) {
            return false;
        }
        if (!t.getTipo().equalsIgnoreCase("AMARILLA") && !t.getTipo().equalsIgnoreCase("ROJA")) {
            return false;
        }
        if (controlSanciones != null && !controlSanciones.registrarTarjeta(t)) {
            return false;
        }
        tarjetas.add(t);
        return true;
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

	
