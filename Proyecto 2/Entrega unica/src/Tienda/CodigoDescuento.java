package Tienda;

public class CodigoDescuento {
    private String codigo;
    private double porcentaje;
    private boolean utilizado;

    public CodigoDescuento(String codigo, double porcentaje) {
        if (codigo == null || codigo.trim().isEmpty() || porcentaje <= 0 || porcentaje > 100) {
            throw new IllegalArgumentException("Codigo o porcentaje invalido");
        }
        this.codigo = codigo;
        this.porcentaje = porcentaje;
        this.utilizado = false;
    }

    public String getCodigo() {
        return codigo;
    }

    public double getPorcentaje() {
        return porcentaje;
    }

    public boolean estaDisponible() {
        return !utilizado;
    }

    public void marcarUtilizado() {
        if (utilizado) {
            throw new IllegalStateException("Codigo ya utilizado");
        }
        utilizado = true;
    }
}
