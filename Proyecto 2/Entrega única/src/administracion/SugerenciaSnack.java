package administracion;

import usuarios.Empleado;

public class SugerenciaSnack {
    public enum Estado { PENDIENTE, COMPLETAR, APROBADA, RECHAZADA }

    private final Empleado autor;
    private final String nombre;
    private final double precio;
    private final int cantidadInicial;
    private String alergenos;
    private Estado estado;
    private String motivo;

    public SugerenciaSnack(Empleado autor, String nombre, double precio, int cantidadInicial, String alergenos) {
        if (autor == null || nombre == null || nombre.trim().isEmpty() || !Double.isFinite(precio) || precio <= 0 || cantidadInicial < 0) {
            throw new IllegalArgumentException("Datos invalidos para la sugerencia de snack");
        }
        this.autor = autor;
        this.nombre = nombre.trim();
        this.precio = precio;
        this.cantidadInicial = cantidadInicial;
        this.alergenos = alergenos == null ? "" : alergenos.trim();
        this.estado = this.alergenos.isEmpty() ? Estado.COMPLETAR : Estado.PENDIENTE;
        this.motivo = "";
    }

    public Empleado getAutor() { return autor; }
    public String getNombre() { return nombre; }
    public double getPrecio() { return precio; }
    public int getCantidadInicial() { return cantidadInicial; }
    public String getAlergenos() { return alergenos; }
    public Estado getEstado() { return estado; }
    public String getMotivo() { return motivo; }

    public void completarAlergenos(String alergenos) {
        if (estado != Estado.COMPLETAR) throw new IllegalStateException("La sugerencia no requiere completar alergenos");
        if (alergenos == null || alergenos.trim().isEmpty()) {
            throw new IllegalArgumentException("Debe indicar alergenos o Ninguno");
        }
        this.alergenos = alergenos.trim();
        estado = Estado.PENDIENTE;
    }

    void aprobar() {
        if (alergenos.isEmpty()) {
            throw new IllegalStateException("Complete la informacion de alergenos antes de aprobar");
        }
        verificarPendiente();
        estado = Estado.APROBADA;
    }

    void rechazar(String motivo) {
        verificarPendiente();
        if (motivo == null || motivo.trim().isEmpty()) {
            throw new IllegalArgumentException("Indique el motivo del rechazo");
        }
        this.motivo = motivo.trim();
        estado = Estado.RECHAZADA;
    }

    void restaurarEstado(Estado estado, String motivo) {
        if (estado == null) {
            throw new IllegalArgumentException("Estado obligatorio");
        }
        if (estado == Estado.RECHAZADA && (motivo == null || motivo.trim().isEmpty())) {
            throw new IllegalArgumentException("Motivo de rechazo obligatorio");
        }
        this.estado = estado;
        this.motivo = motivo == null ? "" : motivo.trim();
    }

    private void verificarPendiente() {
        if (estado != Estado.PENDIENTE && estado != Estado.COMPLETAR) {
            throw new IllegalStateException("La sugerencia ya fue resuelta");
        }
    }
}
