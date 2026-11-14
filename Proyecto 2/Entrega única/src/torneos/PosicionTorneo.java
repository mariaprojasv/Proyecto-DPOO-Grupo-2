package torneos;

/** Fila de posiciones. En eliminacion, puesto 0 significa que el competidor sigue en competencia. */
public class PosicionTorneo {
    private final Competidor competidor;
    private int puesto;
    private int puntos;
    private int jugados;
    private int ganados;
    private int empatados;
    private int perdidos;
    private int favor;
    private int contra;

    public PosicionTorneo(Competidor competidor) {
        this.competidor = competidor;
    }

    public Competidor getCompetidor() { return competidor; }
    public int getPuesto() { return puesto; }
    public int getPuntos() { return puntos; }
    public int getJugados() { return jugados; }
    public int getGanados() { return ganados; }
    public int getEmpatados() { return empatados; }
    public int getPerdidos() { return perdidos; }
    public int getFavor() { return favor; }
    public int getContra() { return contra; }
    public int getDiferencia() { return favor - contra; }

    void setPuesto(int puesto) { this.puesto = puesto; }

    void registrar(int propios, int rivales, int puntosGanados) {
        jugados++;
        favor += propios;
        contra += rivales;
        puntos += puntosGanados;
        if (propios > rivales) {
            ganados++;
        } else if (propios == rivales) {
            empatados++;
        } else {
            perdidos++;
        }
    }
}
