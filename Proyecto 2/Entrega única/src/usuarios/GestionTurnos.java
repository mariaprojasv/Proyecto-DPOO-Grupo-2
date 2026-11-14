package usuarios;

import java.time.LocalTime;
import java.util.ArrayList;
import club.ClubDeportivoSeneca;

public class GestionTurnos {
    private ArrayList<TurnoEmpleado> turnos;
    private final ClubDeportivoSeneca club;
    private ArrayList<SolicitudCambioTurno> solicitudes = new ArrayList<>();

    public GestionTurnos(ClubDeportivoSeneca club) {
        if (club == null) {
            throw new IllegalArgumentException("El club es obligatorio");
        }
        this.club = club;
        turnos = new ArrayList<>();
    }

    public ArrayList<TurnoEmpleado> getTurnos() {
        return turnos;
    }

    public boolean agregarTurno(Empleado empleado, LocalTime inicio, LocalTime fin) {
        return agregarTurno(empleado, inicio, fin, null);
    }

    /** Agrega un turno que solo aplica a los dias indicados (null o vacio = todos los dias). */
    public boolean agregarTurno(Empleado empleado, LocalTime inicio, LocalTime fin, java.util.Set<java.time.DayOfWeek> dias) {
        if (empleado == null || inicio == null || fin == null || !inicio.isBefore(fin)) {
            return false;
        }
        TurnoEmpleado nuevo = new TurnoEmpleado(empleado, inicio, fin, dias);
        for (TurnoEmpleado turno : turnos) {
            if (turno.getEmpleado() == empleado && turno.comparteDiaCon(nuevo)
                    && inicio.isBefore(turno.getFin()) && fin.isAfter(turno.getInicio())) {
                return false;
            }
        }
        turnos.add(nuevo);
        return true;
    }

    /** Indica si el empleado tiene algun turno que aplique en la fecha dada. */
    public boolean estaDeTurno(Empleado empleado, java.time.LocalDate fecha) {
        if (empleado == null || fecha == null) {
            throw new IllegalArgumentException("Empleado y fecha son obligatorios");
        }
        for (TurnoEmpleado turno : turnos) {
            if (turno.getEmpleado() == empleado && turno.aplicaEn(fecha)) {
                return true;
            }
        }
        return false;
    }

    /** Cobertura minima en todos los dias de la semana (los turnos sin dias aplican a todos). */
    public boolean coberturaCompleta(LocalTime apertura, LocalTime cierre) {
        return coberturaCompleta(apertura, cierre, java.util.EnumSet.allOf(java.time.DayOfWeek.class));
    }

    /** Dos entrenadores y un fisioterapeuta en cada minuto de la franja, para cada uno de los dias indicados. */
    public boolean coberturaCompleta(LocalTime apertura, LocalTime cierre, java.util.Set<java.time.DayOfWeek> dias) {
        if (apertura == null || cierre == null || !apertura.isBefore(cierre) || dias == null || dias.isEmpty()) {
            return false;
        }
        for (java.time.DayOfWeek dia : dias) {
            LocalTime hora = apertura;
            while (hora.isBefore(cierre)) {
                ArrayList<Empleado> entrenadores = new ArrayList<>();
                ArrayList<Empleado> fisioterapeutas = new ArrayList<>();
                for (TurnoEmpleado turno : turnos) {
                    if (turno.cubre(hora) && turno.getDias().contains(dia)) {
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
        }
        return true;
    }

    public ArrayList<SolicitudCambioTurno> getSolicitudes() {
        return new ArrayList<>(solicitudes);
    }

    public SolicitudCambioTurno solicitarCambio(TurnoEmpleado turno, LocalTime inicio, LocalTime fin) {
        if (turno == null || !turnos.contains(turno)) {
            throw new IllegalArgumentException("El turno original no existe");
        }
        SolicitudCambioTurno solicitud = new SolicitudCambioTurno(turno, inicio, fin);
        solicitudes.add(solicitud);
        return solicitud;
    }

    public SolicitudCambioTurno restaurarSolicitud(TurnoEmpleado turno, LocalTime inicioOriginal,
            LocalTime finOriginal, LocalTime nuevoInicio, LocalTime nuevoFin,
            SolicitudCambioTurno.Estado estado) {
        if (turno == null || !turnos.contains(turno) || estado == null
                || inicioOriginal == null || finOriginal == null || !inicioOriginal.isBefore(finOriginal)) {
            throw new IllegalArgumentException("Datos de solicitud historica invalidos");
        }
        TurnoEmpleado referencia = estado == SolicitudCambioTurno.Estado.APROBADA
                ? new TurnoEmpleado(turno.getEmpleado(), inicioOriginal, finOriginal) : turno;
        SolicitudCambioTurno solicitud = new SolicitudCambioTurno(referencia, nuevoInicio, nuevoFin);
        solicitud.restaurarEstado(estado);
        solicitudes.add(solicitud);
        return solicitud;
    }

    public void rechazarCambio(Administrador administrador, SolicitudCambioTurno solicitud) {
        verificarAdministrador(administrador);
        validarSolicitudPendiente(solicitud);
        solicitud.rechazar();
    }

    public void aprobarCambio(Administrador administrador, SolicitudCambioTurno solicitud) {
        verificarAdministrador(administrador);
        validarSolicitudPendiente(solicitud);
        TurnoEmpleado anterior = solicitud.getTurnoOriginal();
        TurnoEmpleado nuevo = new TurnoEmpleado(anterior.getEmpleado(), solicitud.getNuevoInicio(), solicitud.getNuevoFin(),
                anterior.tieneDiasEspecificos() ? anterior.getDias() : null);
        for (TurnoEmpleado otro : turnos) {
            if (otro != anterior && otro.getEmpleado() == anterior.getEmpleado() && otro.comparteDiaCon(nuevo)
                    && nuevo.getInicio().isBefore(otro.getFin()) && nuevo.getFin().isAfter(otro.getInicio())) {
                throw new IllegalStateException("El nuevo turno se superpone con otro del empleado");
            }
        }
        int indice = turnos.indexOf(anterior);
        turnos.set(indice, nuevo);
        LocalTime apertura = anterior.getInicio().isBefore(nuevo.getInicio()) ? anterior.getInicio() : nuevo.getInicio();
        LocalTime cierre = anterior.getFin().isAfter(nuevo.getFin()) ? anterior.getFin() : nuevo.getFin();
        java.util.Set<java.time.DayOfWeek> afectados = java.util.EnumSet.copyOf(anterior.getDias());
        afectados.addAll(nuevo.getDias());
        if (!coberturaCompleta(apertura, cierre, afectados)) {
            turnos.set(indice, anterior);
            throw new IllegalStateException("El cambio deja menos de dos entrenadores o un fisioterapeuta");
        }
        solicitud.aprobar();
    }

    private void verificarAdministrador(Administrador administrador) {
        if (administrador == null || club.buscarUsuario(administrador.getLogin()) != administrador) {
            throw new SecurityException("Solo un administrador puede resolver cambios de turno");
        }
    }

    private void validarSolicitudPendiente(SolicitudCambioTurno solicitud) {
        if (solicitud == null || !solicitudes.contains(solicitud)) {
            throw new IllegalArgumentException("La solicitud no existe");
        }
        if (solicitud.getEstado() != SolicitudCambioTurno.Estado.PENDIENTE) {
            throw new IllegalStateException("La solicitud ya fue resuelta");
        }
        if (!turnos.contains(solicitud.getTurnoOriginal())) {
            throw new IllegalStateException("El turno original ya fue modificado");
        }
    }

}
