package torneos;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import deportes.Categoria;
import deportes.Deporte;
import usuarios.Usuario;

/** Torneo sin costo de inscripcion cuyos ganadores reciben un bono de descuento (tienda o cafeteria). */
public class TorneoRecreativo extends Torneo {
    private final double porcentajeBono;
    private final ArrayList<BonoDescuento> bonosOtorgados = new ArrayList<>();

    public TorneoRecreativo(String nombre, Deporte deporte, Modalidad modalidad, Categoria categoria, String nivel,
            Formato formato, int cupos, LocalDate fechaInicio, LocalDate fechaFin, double porcentajeBono) {
        super(nombre, deporte, modalidad, categoria, nivel, formato, cupos, fechaInicio, fechaFin);
        if (porcentajeBono <= 0 || porcentajeBono > 100) {
            throw new IllegalArgumentException("El porcentaje del bono debe estar entre 0 y 100");
        }
        this.porcentajeBono = porcentajeBono;
    }

    public double getPorcentajeBono() { return porcentajeBono; }

    public ArrayList<BonoDescuento> getBonosOtorgados() { return new ArrayList<>(bonosOtorgados); }

    @Override public boolean esCompetitivo() { return false; }

    @Override protected double calcularTarifa(Competidor competidor) { return 0; }

    @Override protected void otorgarPremios(List<PosicionTorneo> posiciones) {
        if (posiciones.isEmpty()) {
            return;
        }
        Competidor campeon = posiciones.get(0).getCompetidor();
        for (Usuario ganador : campeon.getIntegrantes()) {
            BonoDescuento bono = new BonoDescuento(ganador, porcentajeBono, getNombre());
            ganador.guardarBono(bono);
            bonosOtorgados.add(bono);
        }
    }

    @Override protected String describirPremios() {
        StringBuilder sb = new StringBuilder("Premio: bono de " + porcentajeBono + "% (tienda o cafeteria)\n");
        for (BonoDescuento b : bonosOtorgados) {
            sb.append("  Bono para ").append(b.getTitular().getNombre()).append('\n');
        }
        return sb.toString();
    }

    public void restaurarBonoOtorgado(BonoDescuento bono) {
        if (bono != null && !bonosOtorgados.contains(bono)) {
            bonosOtorgados.add(bono);
        }
    }
}
