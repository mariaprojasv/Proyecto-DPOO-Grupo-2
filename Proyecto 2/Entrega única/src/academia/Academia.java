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
    public Entrenamiento programarEntrenamientoValidado(LocalDate fecha, Deporte deporte,
            Entrenador entrenador, java.time.LocalTime hora, int duracion, reservas.Instalacion instalacion) {
        Entrenamiento e = new Entrenamiento(fecha, deporte, entrenador, hora, duracion, instalacion);
        entrenamientos.add(e);
        return e;
    }

    public void registrarAsistenciaValidada(Entrenamiento entrenamiento, Jugador jugador,
            AsistenciaEntrenamiento.Estado estado, int calificacion, String observacion) {
        if (entrenamiento == null || jugador == null) throw new IllegalArgumentException("Datos nulos");
        if (!entrenamientos.contains(entrenamiento)) throw new IllegalStateException("Entrenamiento no registrado");
        boolean inscrito = false;
        for (InscripcionIndividual i : inscripciones) {
            if (i.getJugador() == jugador && i.getDeporte() == entrenamiento.getDeporte()) inscrito = true;
        }
        if (!inscrito) throw new IllegalStateException("Jugador no inscrito en la academia del deporte");
        entrenamiento.registrarAsistenciaValidada(jugador, estado, calificacion, observacion);
    }

    public void agregarJugadorPartidoPractica(PartidoPractica partido, Jugador jugador) {
        if (partido == null || jugador == null) throw new IllegalArgumentException("Datos nulos");
        if (!partidosPractica.contains(partido)) throw new IllegalStateException("Partido no registrado");
        boolean inscrito = false;
        for (InscripcionIndividual i : inscripciones) {
            if (i.getJugador() == jugador && i.getDeporte() == partido.getDeporte()) inscrito = true;
        }
        if (!inscrito) throw new IllegalStateException("Jugador no inscrito en el deporte");
        partido.agregarJugador(jugador);
    }

    public boolean inscribirJugador(Jugador jugador, Deporte deporte, String nivel) {
        if (jugador == null || deporte == null || nivel == null || nivel.trim().isEmpty()) {
            throw new IllegalArgumentException("Datos de inscripcion invalidos");
        }
        for (InscripcionIndividual inscripcion : inscripciones) {
            if (inscripcion.getJugador() == jugador && inscripcion.getDeporte() == deporte) {
                throw new IllegalStateException("El jugador ya esta inscrito en este deporte");
            }
        }
        inscripciones.add(new InscripcionIndividual(jugador, deporte, nivel, 0));
        return true;
    }

    public ArrayList<InscripcionIndividual> consultarInscripciones(Jugador jugador) {
        if (jugador == null) {
            throw new IllegalArgumentException("Jugador obligatorio");
        }
        ArrayList<InscripcionIndividual> resultado = new ArrayList<>();
        for (InscripcionIndividual inscripcion : inscripciones) {
            if (inscripcion.getJugador() == jugador) {
                resultado.add(inscripcion);
            }
        }
        return resultado;
    }

    public void actualizarRanking(InscripcionIndividual inscripcion, int puntos) {
        if (inscripcion == null || !inscripciones.contains(inscripcion)) {
            throw new IllegalStateException("Inscripcion no registrada en la academia");
        }
        if (puntos < 0) {
            throw new IllegalArgumentException("Los puntos no pueden ser negativos");
        }
        if (inscripcion.getRanking() > Integer.MAX_VALUE - puntos) {
            throw new IllegalStateException("Desbordamiento del ranking");
        }
        inscripcion.setRanking(inscripcion.getRanking() + puntos);
    }

    public ArrayList<InscripcionIndividual> consultarRanking(Deporte deporte) {
        if (deporte == null) {
            throw new IllegalArgumentException("Deporte obligatorio");
        }
        ArrayList<InscripcionIndividual> resultado = new ArrayList<>();
        for (InscripcionIndividual inscripcion : inscripciones) {
            if (inscripcion.getDeporte() == deporte) {
                resultado.add(inscripcion);
            }
        }
        resultado.sort((a, b) -> Integer.compare(b.getRanking(), a.getRanking()));
        return resultado;
    }
}
