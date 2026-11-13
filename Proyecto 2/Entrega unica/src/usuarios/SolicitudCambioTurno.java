package usuarios;

import java.time.LocalTime;

public class SolicitudCambioTurno {
    public enum Estado { PENDIENTE, APROBADA, RECHAZADA }

    private final TurnoEmpleado turnoOriginal;
    private final LocalTime nuevoInicio;
    private final LocalTime nuevoFin;
    private Estado estado;
    private final LocalTime inicioOriginal;
    private final LocalTime finOriginal;

    public SolicitudCambioTurno(TurnoEmpleado turnoOriginal, LocalTime nuevoInicio, LocalTime nuevoFin) {
        if (turnoOriginal == null || nuevoInicio == null || nuevoFin == null || !nuevoInicio.isBefore(nuevoFin)) {
            throw new IllegalArgumentException("Los datos del cambio de turno son invalidos");
        }
        this.turnoOriginal = turnoOriginal;
        this.inicioOriginal = turnoOriginal.getInicio();
        this.finOriginal = turnoOriginal.getFin();
        this.nuevoInicio = nuevoInicio;
        this.nuevoFin = nuevoFin;
        this.estado = Estado.PENDIENTE;
    }

    public TurnoEmpleado getTurnoOriginal() { return turnoOriginal; }
    public LocalTime getNuevoInicio() { return nuevoInicio; }
    public LocalTime getNuevoFin() { return nuevoFin; }
    public Estado getEstado() { return estado; }
    public LocalTime getInicioOriginal() { return inicioOriginal; }
    public LocalTime getFinOriginal() { return finOriginal; }
    void restaurarEstado(Estado estado) {
        if (estado == null) {
            throw new IllegalArgumentException("Estado invalido");
        }
        this.estado = estado;
    }
    void aprobar() { estado = Estado.APROBADA; }
    void rechazar() { estado = Estado.RECHAZADA; }
}
