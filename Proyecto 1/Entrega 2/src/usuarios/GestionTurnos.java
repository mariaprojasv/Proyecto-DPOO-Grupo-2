package usuarios;

import java.time.LocalTime;
import java.util.ArrayList;

public class GestionTurnos {
    private ArrayList<TurnoEmpleado> turnos;

    public GestionTurnos() {
        turnos = new ArrayList<>();
    }

    public ArrayList<TurnoEmpleado> getTurnos() {
        return turnos;
    }

    public boolean agregarTurno(Empleado empleado, LocalTime inicio, LocalTime fin) {
        if (empleado == null || inicio == null || fin == null || !inicio.isBefore(fin)) {
            return false;
        }
        for (TurnoEmpleado turno : turnos) {
            if (turno.getEmpleado() == empleado && inicio.isBefore(turno.getFin()) && fin.isAfter(turno.getInicio())) {
                return false;
            }
        }
        turnos.add(new TurnoEmpleado(empleado, inicio, fin));
        return true;
    }

    public boolean coberturaCompleta(LocalTime apertura, LocalTime cierre) {
        if (apertura == null || cierre == null || !apertura.isBefore(cierre)) {
            return false;
        }
        LocalTime hora = apertura;
        while (hora.isBefore(cierre)) {
            ArrayList<Empleado> entrenadores = new ArrayList<>();
            ArrayList<Empleado> fisioterapeutas = new ArrayList<>();
            for (TurnoEmpleado turno : turnos) {
                if (turno.cubre(hora)) {
                    Empleado empleado = turno.getEmpleado();
                    if (empleado instanceof Entrenador && !entrenadores.contains(empleado)) {
                        entrenadores.add(empleado);
                    }
                    if (empleado instanceof Fisioterapeuta && !fisioterapeutas.contains(empleado)) {
                        fisioterapeutas.add(empleado);
                    }
                }
            }
            if (entrenadores.size() < 2 || fisioterapeutas.size() < 1) {
                return false;
            }
            hora = hora.plusMinutes(1);
        }
        return true;
    }
}
