package torneos;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import deportes.Categoria;
import deportes.Deporte;
import usuarios.Empleado;
import usuarios.Usuario;

/**
 * Torneo con tarifa de inscripcion y premio en efectivo calculado sobre el total recaudado.
 * La tarifa se cobra por persona en torneos individuales y de dobles, y por equipo en los de conjunto.
 * Los empleados no pagan ni reciben el premio en efectivo.
 */
public class TorneoCompetitivo extends Torneo {
    private final double tarifaInscripcion;
    private final double porcentajePremio;
    private final ArrayList<PremioEfectivo> premiosEntregados = new ArrayList<>();

    public TorneoCompetitivo(String nombre, Deporte deporte, Modalidad modalidad, Categoria categoria, String nivel,
            Formato formato, int cupos, LocalDate fechaInicio, LocalDate fechaFin,
            double tarifaInscripcion, double porcentajePremio) {
        super(nombre, deporte, modalidad, categoria, nivel, formato, cupos, fechaInicio, fechaFin);
        if (tarifaInscripcion <= 0 || !Double.isFinite(tarifaInscripcion)) {
            throw new IllegalArgumentException("Un torneo competitivo exige una tarifa de inscripcion positiva");
        }
        if (porcentajePremio <= 0 || porcentajePremio > 1) {
            throw new IllegalArgumentException("El porcentaje del premio debe estar entre 0 (exclusivo) y 1");
        }
        this.tarifaInscripcion = tarifaInscripcion;
        this.porcentajePremio = porcentajePremio;
    }

    public double getTarifaInscripcion() { return tarifaInscripcion; }
    public double getPorcentajePremio() { return porcentajePremio; }

    /** Bolsa del premio: fraccion del total recaudado. */
    public double getPremioEnEfectivo() { return getTotalRecaudado() * porcentajePremio; }

    public ArrayList<PremioEfectivo> getPremiosEntregados() { return new ArrayList<>(premiosEntregados); }

    @Override public boolean esCompetitivo() { return true; }

    @Override protected double calcularTarifa(Competidor competidor) {
        if (competidor instanceof CompetidorEquipo) {
            return tarifaInscripcion;
        }
        int pagan = 0;
        for (Usuario u : competidor.getIntegrantes()) {
            if (!(u instanceof Empleado)) {
                pagan++;
            }
        }
        return tarifaInscripcion * pagan;
    }

    @Override protected void otorgarPremios(List<PosicionTorneo> posiciones) {
        double bolsa = getPremioEnEfectivo();
        if (bolsa <= 0) {
            return;
        }
        // El premio es del campeon; si solo hay empleados entre sus integrantes pasa al mejor puesto siguiente.
        for (PosicionTorneo posicion : posiciones) {
            ArrayList<Usuario> elegibles = new ArrayList<>();
            for (Usuario u : posicion.getCompetidor().getIntegrantes()) {
                if (!(u instanceof Empleado)) {
                    elegibles.add(u);
                }
            }
            if (!elegibles.isEmpty()) {
                for (Usuario u : elegibles) {
                    premiosEntregados.add(new PremioEfectivo(u, bolsa / elegibles.size()));
                }
                return;
            }
        }
    }

    @Override protected String describirPremios() {
        StringBuilder sb = new StringBuilder("Tarifa: " + tarifaInscripcion + " | Recaudado: " + getTotalRecaudado()
                + " | Bolsa del premio: " + getPremioEnEfectivo() + "\n");
        for (PremioEfectivo p : premiosEntregados) {
            sb.append("  Premio para ").append(p.getBeneficiario().getNombre()).append(": ").append(p.getMonto()).append('\n');
        }
        return sb.toString();
    }

    public void restaurarPremio(Usuario beneficiario, double monto) {
        premiosEntregados.add(new PremioEfectivo(beneficiario, monto));
    }
}
