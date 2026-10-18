package academia;

import usuarios.Jugador;

public class AsistenciaEntrenamiento {
    private Jugador jugador;
    private boolean asistio;
    private String observacion;
    public AsistenciaEntrenamiento(Jugador jugador, boolean asistio, String observacion) {
        this.jugador = jugador;
        this.asistio = asistio;
        this.observacion = observacion;
    }
    public Jugador getJugador() {
	return jugador;
	}
    public boolean isAsistio() {
        return asistio;
    }
    public String getObservacion() {
	return observacion;
	}
}
