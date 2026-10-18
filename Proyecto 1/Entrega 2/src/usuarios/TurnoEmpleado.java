package usuarios;

import java.time.LocalTime;

public class TurnoEmpleado {
    private Empleado empleado;
    private LocalTime inicio;
    private LocalTime fin;

    public TurnoEmpleado(Empleado empleado, LocalTime inicio, LocalTime fin) {
        this.empleado = empleado;
        this.inicio = inicio;
        this.fin = fin;
    }

    public Empleado getEmpleado() {
        return empleado;
    }

    public LocalTime getInicio() {
        return inicio;
    }

    public LocalTime getFin() {
        return fin;
    }

    public boolean cubre(LocalTime hora) {
        if (hora == null || inicio == null || fin == null) {
            return false;
        }
        return !hora.isBefore(inicio) && hora.isBefore(fin);
    }
}
