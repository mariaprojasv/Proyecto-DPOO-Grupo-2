package academia;

import usuarios.Jugador;
import deportes.Deporte;

public class InscripcionIndividual {
    private Jugador jugador;
    private Deporte deporte;
    public InscripcionIndividual(Jugador jugador, Deporte deporte) {
        this.jugador = jugador;
        this.deporte = deporte;
    }
    public Jugador getJugador() {
	return jugador;
	}
    public Deporte getDeporte() {
	return deporte;
	}
}
