package usuarios;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.EnumSet;
import java.util.Set;

public class TurnoEmpleado {
    private Empleado empleado;
    private LocalTime inicio;
    private LocalTime fin;
    /** Dias de la semana del turno; vacio significa todos los dias (comportamiento original). */
    private final Set<DayOfWeek> dias = EnumSet.noneOf(DayOfWeek.class);

    public TurnoEmpleado(Empleado empleado, LocalTime inicio, LocalTime fin) {
        this.empleado = empleado;
        this.inicio = inicio;
        this.fin = fin;
    }

    public TurnoEmpleado(Empleado empleado, LocalTime inicio, LocalTime fin, Set<DayOfWeek> dias) {
        this(empleado, inicio, fin);
        if (dias != null) {
            this.dias.addAll(dias);
        }
    }

    public Set<DayOfWeek> getDias() {
        return EnumSet.copyOf(dias.isEmpty() ? EnumSet.allOf(DayOfWeek.class) : dias);
    }

    public boolean tieneDiasEspecificos() {
        return !dias.isEmpty();
    }

    public boolean aplicaEn(LocalDate fecha) {
        return fecha != null && (dias.isEmpty() || dias.contains(fecha.getDayOfWeek()));
    }

    public boolean comparteDiaCon(TurnoEmpleado otro) {
        if (dias.isEmpty() || otro.dias.isEmpty()) {
            return true;
        }
        for (DayOfWeek d : dias) {
            if (otro.dias.contains(d)) {
                return true;
            }
        }
        return false;
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
