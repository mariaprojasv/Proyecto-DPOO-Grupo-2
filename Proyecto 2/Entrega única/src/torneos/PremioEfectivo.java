package torneos;

import usuarios.Usuario;

/** Premio en efectivo entregado a un ganador de un torneo competitivo. */
public class PremioEfectivo {
    private final Usuario beneficiario;
    private final double monto;

    public PremioEfectivo(Usuario beneficiario, double monto) {
        if (beneficiario == null || monto < 0 || !Double.isFinite(monto)) {
            throw new IllegalArgumentException("Premio invalido");
        }
        this.beneficiario = beneficiario;
        this.monto = monto;
    }

    public Usuario getBeneficiario() { return beneficiario; }
    public double getMonto() { return monto; }
}
