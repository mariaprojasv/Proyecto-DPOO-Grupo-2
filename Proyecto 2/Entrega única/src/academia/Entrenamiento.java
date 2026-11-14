package academia;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import usuarios.Entrenador;
import usuarios.Jugador;
import deportes.Deporte;
import reservas.Instalacion;

public class Entrenamiento {
    private LocalDate fecha;
    private Deporte deporte;
    private Entrenador entrenador;
    private LocalTime horaInicio;
    private int duracionMinutos;
    private Instalacion instalacion;
    private ArrayList<Jugador> invitados;
    private ArrayList<AsistenciaEntrenamiento> asistencias;

    public Entrenamiento(LocalDate fecha, Deporte deporte, Entrenador entrenador) {
        this(fecha, deporte, entrenador, null, 0, null);
    }

    public Entrenamiento(LocalDate fecha, Deporte deporte, Entrenador entrenador,
                         LocalTime horaInicio, int duracionMinutos, Instalacion instalacion) {
        if (fecha == null || deporte == null || entrenador == null || duracionMinutos < 0) {
            throw new IllegalArgumentException("Datos del entrenamiento invalidos");
        }
        if (horaInicio != null && duracionMinutos == 0) {
            throw new IllegalArgumentException("La duracion debe ser positiva si se indica una hora");
        }
        if (instalacion != null && instalacion.getDeporte() != deporte) {
            throw new IllegalArgumentException("La instalacion no corresponde al deporte");
        }
        this.fecha = fecha;
        this.deporte = deporte;
        this.entrenador = entrenador;
        this.horaInicio = horaInicio;
        this.duracionMinutos = duracionMinutos;
        this.instalacion = instalacion;
        this.invitados = new ArrayList<>();
        this.asistencias = new ArrayList<>();
    }

    public LocalDate getFecha() { return fecha; }
    public Deporte getDeporte() { return deporte; }
    public Entrenador getEntrenador() { return entrenador; }
    public LocalTime getHoraInicio() { return horaInicio; }
    public int getDuracionMinutos() { return duracionMinutos; }
    public Instalacion getInstalacion() { return instalacion; }
    public ArrayList<Jugador> getInvitados() { return new ArrayList<>(invitados); }
    public ArrayList<AsistenciaEntrenamiento> getAsistencias() { return asistencias; }

    public void invitarJugador(Jugador jugador) {
        if (jugador == null) throw new IllegalArgumentException("Jugador nulo");
        if (invitados.contains(jugador)) throw new IllegalStateException("Jugador ya invitado");
        invitados.add(jugador);
    }

    public void registrarAsistenciaValidada(Jugador jugador, AsistenciaEntrenamiento.Estado estado,
                                            int calificacion, String observacion) {
        if (jugador == null || estado == null) throw new IllegalArgumentException("Datos invalidos");
        if (calificacion < 1 || calificacion > 5) throw new IllegalArgumentException("Calificacion entre 1 y 5");
        if (!invitados.isEmpty() && !invitados.contains(jugador)) throw new IllegalStateException("Jugador no invitado");
        for (AsistenciaEntrenamiento a : asistencias) {
            if (a.getJugador() == jugador) throw new IllegalStateException("Asistencia duplicada");
        }
        asistencias.add(new AsistenciaEntrenamiento(jugador, estado, calificacion, observacion));
    }

    public void restaurarAsistencia(Jugador jugador, AsistenciaEntrenamiento.Estado estado,
                                   int calificacion, String observacion) {
        if (jugador == null || estado == null || calificacion < 0 || calificacion > 5) {
            throw new IllegalArgumentException("Datos de asistencia invalidos");
        }
        if (!invitados.isEmpty() && !invitados.contains(jugador)) {
            throw new IllegalStateException("Jugador no invitado");
        }
        for (AsistenciaEntrenamiento asistencia : asistencias) {
            if (asistencia.getJugador() == jugador) {
                throw new IllegalStateException("Asistencia duplicada");
            }
        }
        asistencias.add(new AsistenciaEntrenamiento(jugador, estado, calificacion, observacion));
    }

    public boolean registrarAsistencia(Jugador jugador, boolean asistio, String observacion) {
        if (jugador == null) return false;
        for (AsistenciaEntrenamiento a : asistencias) {
            if (a.getJugador() == jugador) return false;
        }
        if (!invitados.isEmpty() && !invitados.contains(jugador)) return false;
        asistencias.add(new AsistenciaEntrenamiento(jugador, asistio, observacion));
        return true;
    }
}
