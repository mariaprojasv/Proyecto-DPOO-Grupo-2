package torneos;

/** Encuentro entre dos competidores de un torneo. Un encuentro sin rival (bye) avanza solo en eliminacion. */
public class Encuentro {
    private final int ronda;
    private final Competidor a;
    private final Competidor b;
    private int marcadorA;
    private int marcadorB;
    private boolean jugado;

    public Encuentro(int ronda, Competidor a, Competidor b) {
        if (ronda <= 0 || a == null) {
            throw new IllegalArgumentException("Encuentro invalido");
        }
        this.ronda = ronda;
        this.a = a;
        this.b = b;
        this.jugado = b == null;
    }

    public int getRonda() { return ronda; }
    public Competidor getCompetidorA() { return a; }
    public Competidor getCompetidorB() { return b; }
    public int getMarcadorA() { return marcadorA; }
    public int getMarcadorB() { return marcadorB; }
    public boolean estaJugado() { return jugado; }
    public boolean esBye() { return b == null; }

    void registrar(int marcadorA, int marcadorB) {
        if (esBye()) {
            throw new IllegalStateException("Un encuentro sin rival no tiene marcador");
        }
        if (jugado) {
            throw new IllegalStateException("El encuentro ya tiene resultado");
        }
        if (marcadorA < 0 || marcadorB < 0) {
            throw new IllegalArgumentException("Los marcadores no pueden ser negativos");
        }
        this.marcadorA = marcadorA;
        this.marcadorB = marcadorB;
        this.jugado = true;
    }

    /** Solo para restaurar desde almacenamiento. */
    void restaurar(int marcadorA, int marcadorB, boolean jugado) {
        this.marcadorA = marcadorA;
        this.marcadorB = marcadorB;
        this.jugado = jugado || esBye();
    }

    /** Ganador del encuentro; null si aun no se juega o si fue empate. */
    public Competidor getGanador() {
        if (!jugado) {
            return null;
        }
        if (esBye()) {
            return a;
        }
        return marcadorA > marcadorB ? a : (marcadorB > marcadorA ? b : null);
    }

    public Competidor getPerdedor() {
        Competidor ganador = getGanador();
        return ganador == null || esBye() ? null : (ganador == a ? b : a);
    }
}
