package reservas;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import usuarios.Usuario;
import usuarios.Administrador;
import java.util.HashMap;

public class GestionReservas {
    private ArrayList<Instalacion> instalaciones;
    private ArrayList<Reserva> reservas;
    private HashMap<String, Integer> maximosSimultaneos;
    public GestionReservas() {
        instalaciones = new ArrayList<>();
        reservas = new ArrayList<>();
        maximosSimultaneos = new HashMap<>();
    }
    public boolean agregarInstalacion(Instalacion instalacion) {
        if (instalacion == null || instalaciones.contains(instalacion)) {
            return false;
        }
        instalaciones.add(instalacion);
        return true;
    }
    public ArrayList<Instalacion> getInstalaciones() {
	return new ArrayList<>(instalaciones);
	}
    public ArrayList<Reserva> getReservas() {
	return new ArrayList<>(reservas);
	}
    public ArrayList<Reserva> consultarReservas(Instalacion instalacion) {
        ArrayList<Reserva> resultado = new ArrayList<>();
        for (Reserva r : reservas) {
            if (r.getInstalacion() == instalacion) resultado.add(r);
        }
        return resultado;
    }
    public ArrayList<Reserva> consultarReservas(Usuario usuario) {
        ArrayList<Reserva> resultado = new ArrayList<>();
        for (Reserva r : reservas) {
            if (r.getUsuario() == usuario) resultado.add(r);
        }
        return resultado;
    }
    public boolean configurarMaximoSimultaneo(String deporte, int maximo) {
        if (deporte == null || deporte.isBlank() || maximo <= 0) {
            return false;
        }
        maximosSimultaneos.put(deporte.trim().toLowerCase(), maximo);
        return true;
    }

    public boolean permitirUso(Reserva reserva, boolean llevaBebidaCaliente) {
        if (reserva == null || !reservas.contains(reserva)) {
            return false;
        }
        String deporte = reserva.getInstalacion().getDeporte().getNombre();
        if (llevaBebidaCaliente && (deporte.equalsIgnoreCase("Padel")
                || deporte.equalsIgnoreCase("Pádel")
                || (deporte.equalsIgnoreCase("Tenis de mesa") && reserva.getInstalacion().esTechada()))) {
            return false;
        }
        return true;
    }

    public Reserva realizarReservaValidada(Usuario usuario, Instalacion instalacion, LocalDate fecha,
                                         LocalTime inicio, LocalTime fin, int jugadores, String modalidad) {
        if (usuario == null || instalacion == null || fecha == null || inicio == null || fin == null) {
            throw new IllegalArgumentException("Los datos de la reserva no pueden ser nulos");
        }
        if (usuario instanceof Administrador) {
            throw new SecurityException("Los administradores no pueden reservar instalaciones");
        }
        if (!instalaciones.contains(instalacion)) {
            throw new IllegalArgumentException("La instalacion no esta registrada");
        }
        if (!inicio.isBefore(fin)) {
            throw new IllegalArgumentException("La hora inicial debe ser anterior a la final");
        }
        if (jugadores <= 0 || jugadores > instalacion.getCapacidadMaxima()) {
            throw new IllegalArgumentException("Cantidad de jugadores incompatible con la instalacion");
        }
        if (!instalacion.admiteModalidad(modalidad)) {
            throw new IllegalArgumentException("Modalidad no admitida por la instalacion");
        }
        String deporte = instalacion.getDeporte().getNombre();
        if ((deporte.equalsIgnoreCase("Padel") || deporte.equalsIgnoreCase("Pádel"))
                && (jugadores != 4 || !modalidad.equalsIgnoreCase("dobles"))) {
            throw new IllegalArgumentException("Padel exige cuatro jugadores en modalidad dobles");
        }
        Reserva nueva = new Reserva(usuario, instalacion, fecha, inicio, fin, jugadores, modalidad);
        int ocupadas = 0;
        Integer maximo = maximosSimultaneos.get(deporte.trim().toLowerCase());
        for (Reserva existente : reservas) {
            if (nueva.seSuperpone(existente)) {
                throw new IllegalStateException("La instalacion ya esta reservada en ese horario");
            }
            if (existente.getInstalacion().getDeporte().getNombre().equalsIgnoreCase(deporte)
                    && existente.getFecha().equals(fecha)
                    && inicio.isBefore(existente.getFin())
                    && existente.getInicio().isBefore(fin)) {
                ocupadas++;
            }
        }
        if (maximo != null && ocupadas >= maximo) {
            throw new IllegalStateException("Se alcanzo el maximo simultaneo de instalaciones");
        }
        reservas.add(nueva);
        return nueva;
    }

    public Reserva realizarReserva(Usuario usuario, Instalacion instalacion, LocalDate fecha,
                                 LocalTime inicio, LocalTime fin, int jugadores, String modalidad) {
        try {
            return realizarReservaValidada(usuario, instalacion, fecha, inicio, fin, jugadores, modalidad);
        }
        catch (IllegalArgumentException | IllegalStateException | SecurityException e) {
            return null;
        }
    }
}
