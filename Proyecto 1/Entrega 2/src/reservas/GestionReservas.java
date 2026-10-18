package reservas;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import usuarios.Usuario;

public class GestionReservas {
    private ArrayList<Instalacion> instalaciones;
    private ArrayList<Reserva> reservas;
    public GestionReservas() {
        instalaciones = new ArrayList<>();
        reservas = new ArrayList<>();
    }
    public boolean agregarInstalacion(Instalacion instalacion) {
        if (instalacion == null || instalaciones.contains(instalacion)) {
            return false;
        }
        instalaciones.add(instalacion);
        return true;
    }
    public ArrayList<Instalacion> getInstalaciones() {
	return instalaciones;
	}
    public ArrayList<Reserva> getReservas() {
	return reservas;
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
    public Reserva realizarReserva(Usuario usuario, Instalacion instalacion, LocalDate fecha,
                                 LocalTime inicio, LocalTime fin, int jugadores, String modalidad) {
        if (usuario == null || instalacion == null || fecha == null || inicio == null || fin == null) {
            return null;
        }
        if (!instalaciones.contains(instalacion) || !inicio.isBefore(fin)) {
            return null;
        }
        if (jugadores <= 0 || jugadores > instalacion.getCapacidadMaxima()) {
            return null;
        }
        if (!instalacion.admiteModalidad(modalidad)) {
            return null;
        }
        Reserva nueva = new Reserva(usuario, instalacion, fecha, inicio, fin, jugadores, modalidad);
        for (Reserva existente : reservas) {
            if (nueva.seSuperpone(existente)) {
                return null;
            }
        }
        reservas.add(nueva);
        return nueva;
    }
}
