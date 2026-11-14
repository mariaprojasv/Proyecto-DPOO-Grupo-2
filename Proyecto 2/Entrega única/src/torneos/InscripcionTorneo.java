package torneos;

/** Cupo tomado por un competidor en un torneo, con la tarifa que se le cobro (0 si es gratuito). */
public class InscripcionTorneo {
    private final Competidor competidor;
    private final Torneo.TipoCupo tipoCupo;
    private final double tarifaCobrada;

    public InscripcionTorneo(Competidor competidor, Torneo.TipoCupo tipoCupo, double tarifaCobrada) {
        if (competidor == null || tipoCupo == null || tarifaCobrada < 0 || !Double.isFinite(tarifaCobrada)) {
            throw new IllegalArgumentException("Inscripcion invalida");
        }
        this.competidor = competidor;
        this.tipoCupo = tipoCupo;
        this.tarifaCobrada = tarifaCobrada;
    }

    public Competidor getCompetidor() { return competidor; }
    public Torneo.TipoCupo getTipoCupo() { return tipoCupo; }
    public double getTarifaCobrada() { return tarifaCobrada; }
}
