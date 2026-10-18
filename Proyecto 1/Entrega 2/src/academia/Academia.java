package academia;

import java.time.LocalDate;
import java.util.ArrayList;
import deportes.Deporte;
import usuarios.Jugador;
import usuarios.Entrenador;

public class Academia {
    private ArrayList<InscripcionIndividual> inscripciones;
    private ArrayList<Entrenamiento> entrenamientos;
    private ArrayList<PartidoPractica> partidosPractica;
    public Academia() {
        inscripciones = new ArrayList<>();
        entrenamientos = new ArrayList<>();
        partidosPractica = new ArrayList<>();
    }
    public ArrayList<InscripcionIndividual> getInscripciones() {
	return inscripciones;
	}
    public ArrayList<Entrenamiento> getEntrenamientos() {
	return entrenamientos;
	}
    public ArrayList<PartidoPractica> getPartidosPractica() {
	return partidosPractica;
	}
    public boolean inscribirJugador(Jugador jugador, Deporte deporte) {
        if (jugador == null || deporte == null) {
            return false;
        }
        for (InscripcionIndividual i : inscripciones) {
            if (i.getJugador() == jugador && i.getDeporte() == deporte) {
                return false;
            }
        }
        inscripciones.add(new InscripcionIndividual(jugador, deporte));
        return true;
    }
    public Entrenamiento programarEntrenamiento(LocalDate fecha, Deporte deporte, Entrenador entrenador) {
        if (fecha == null || deporte == null || entrenador == null) {
            return null;
        }
        Entrenamiento e = new Entrenamiento(fecha, deporte, entrenador);
        entrenamientos.add(e);
        return e;
    }
    public boolean registrarAsistencia(Entrenamiento entrenamiento, Jugador jugador, boolean asistio, String observacion) {
        if (!entrenamientos.contains(entrenamiento)) {
            return false;
        }
        boolean inscrito = false;
        for (InscripcionIndividual i : inscripciones) {
            if (i.getJugador() == jugador && i.getDeporte() == entrenamiento.getDeporte()) inscrito = true;
        }
        if (!inscrito) {
            return false;
        }
        return entrenamiento.registrarAsistencia(jugador, asistio, observacion);
    }
    public PartidoPractica programarPartidoPractica(LocalDate fecha, Deporte deporte, String observacion) {
        if (fecha == null || deporte == null) {
            return null;
        }
        PartidoPractica p = new PartidoPractica(fecha, deporte, observacion);
        partidosPractica.add(p);
        return p;
    }
}
