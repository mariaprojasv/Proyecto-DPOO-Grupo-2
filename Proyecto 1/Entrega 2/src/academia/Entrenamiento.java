package academia;

import java.time.LocalDate;
import java.util.ArrayList;
import usuarios.Entrenador;
import usuarios.Jugador;
import deportes.Deporte;

public class Entrenamiento {
    private LocalDate fecha;
    private Deporte deporte;
    private Entrenador entrenador;
    private ArrayList<AsistenciaEntrenamiento> asistencias;
    public Entrenamiento(LocalDate fecha, Deporte deporte, Entrenador entrenador) {
        this.fecha = fecha;
        this.deporte = deporte;
        this.entrenador = entrenador;
        this.asistencias = new ArrayList<>();
    }
    public LocalDate getFecha() {
	return fecha;
	}
    public Deporte getDeporte() {
	return deporte;
	}
    public Entrenador getEntrenador() {
	return entrenador;
	}
    public ArrayList<AsistenciaEntrenamiento> getAsistencias() {
	return asistencias;
	}
    public boolean registrarAsistencia(Jugador jugador, boolean asistio, String observacion) {
        if (jugador == null) {
            return false;
        }
        for (AsistenciaEntrenamiento a : asistencias) {
            if (a.getJugador() == jugador) {
                return false;
            }
        }
        asistencias.add(new AsistenciaEntrenamiento(jugador, asistio, observacion));
        return true;
    }
}
