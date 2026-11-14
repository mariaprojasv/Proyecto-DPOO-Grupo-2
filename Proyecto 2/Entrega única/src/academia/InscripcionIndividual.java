package academia;

import usuarios.Jugador;
import deportes.Deporte;

public class InscripcionIndividual {
    private Jugador jugador;
    private Deporte deporte;
    private String nivel;
    private int ranking;

    public InscripcionIndividual(Jugador jugador, Deporte deporte) {
        this(jugador, deporte, "INICIAL", 0);
    }

    public InscripcionIndividual(Jugador jugador, Deporte deporte, String nivel, int ranking) {
        if (jugador == null || deporte == null) {
            throw new IllegalArgumentException("Jugador y deporte son obligatorios");
        }
        this.jugador = jugador;
        this.deporte = deporte;
        setNivel(nivel);
        setRanking(ranking);
    }

    public Jugador getJugador() { return jugador; }
    public Deporte getDeporte() { return deporte; }
    public String getNivel() { return nivel; }
    public int getRanking() { return ranking; }

    public void setNivel(String nivel) {
        if (nivel == null || nivel.trim().isEmpty()) {
            throw new IllegalArgumentException("El nivel no puede estar vacio");
        }
        this.nivel = nivel.trim();
    }

    public void setRanking(int ranking) {
        if (ranking < 0) {
            throw new IllegalArgumentException("El ranking no puede ser negativo");
        }
        this.ranking = ranking;
    }
}
