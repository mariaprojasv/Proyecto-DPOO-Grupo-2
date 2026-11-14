package torneos;

import usuarios.Usuario;

/**
 * Bono de descuento ganado en un torneo recreativo. Se guarda en el titular y se redime una sola vez,
 * en la tienda o en la cafeteria, y no es acumulable con otros descuentos.
 */
public class BonoDescuento {
    private final String id;
    private final Usuario titular;
    private final double porcentaje;
    private final String origen;
    private boolean usado;

    public BonoDescuento(Usuario titular, double porcentaje, String origen) {
        this(java.util.UUID.randomUUID().toString(), titular, porcentaje, origen);
    }

    public BonoDescuento(String id, Usuario titular, double porcentaje, String origen) {
        if (id == null || id.trim().isEmpty() || id.contains("|")) {
            throw new IllegalArgumentException("Identificador de bono invalido");
        }
        this.id = id;
        if (titular == null || origen == null || origen.trim().isEmpty() || porcentaje <= 0 || porcentaje > 100) {
            throw new IllegalArgumentException("Bono invalido");
        }
        this.titular = titular;
        this.porcentaje = porcentaje;
        this.origen = origen;
    }

    public String getId() { return id; }
    public Usuario getTitular() { return titular; }
    public double getPorcentaje() { return porcentaje; }
    public String getOrigen() { return origen; }
    public boolean estaDisponible() { return !usado; }

    public void marcarUsado() {
        if (usado) {
            throw new IllegalStateException("El bono ya fue utilizado");
        }
        usado = true;
    }

    /** Solo para restaurar un bono ya consumido desde el almacenamiento. */
    public void restaurarUsado(boolean usado) {
        this.usado = usado;
    }
}
