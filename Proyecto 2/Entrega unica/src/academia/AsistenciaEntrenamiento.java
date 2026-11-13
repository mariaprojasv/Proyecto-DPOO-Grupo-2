package academia;

import usuarios.Jugador;

public class AsistenciaEntrenamiento {
    public enum Estado { PRESENTE, TARDE, AUSENTE }
    private Jugador jugador;
    private Estado estado;
    private int calificacion;
    private String observacion;

    public AsistenciaEntrenamiento(Jugador jugador, boolean asistio, String observacion) {
        this(jugador, asistio ? Estado.PRESENTE : Estado.AUSENTE, 0, observacion);
    }

    public AsistenciaEntrenamiento(Jugador jugador, Estado estado, int calificacion, String observacion) {
        if (jugador == null || estado == null || calificacion < 0 || calificacion > 5) {
            throw new IllegalArgumentException("Datos de asistencia invalidos");
        }
        this.jugador = jugador;
        this.estado = estado;
        this.calificacion = calificacion;
        this.observacion = observacion == null ? "" : observacion;
    }

    public Jugador getJugador() { return jugador; }
    public boolean isAsistio() { return estado != Estado.AUSENTE; }
    public Estado getEstado() { return estado; }
    public int getCalificacion() { return calificacion; }
    public String getObservacion() { return observacion; }
}
