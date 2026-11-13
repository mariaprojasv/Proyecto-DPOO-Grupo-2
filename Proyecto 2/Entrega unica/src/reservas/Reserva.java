package reservas;

import usuarios.Usuario;
import java.time.LocalDate;
import java.time.LocalTime;

public class Reserva {
    private Usuario usuario;
    private Instalacion instalacion;
    private LocalDate fecha;
    private LocalTime inicio;
    private LocalTime fin;
    private int cantidadJugadores;
    private String modalidad;
    public Reserva(Usuario usuario, Instalacion instalacion, LocalDate fecha, LocalTime inicio,
                   LocalTime fin, int cantidadJugadores, String modalidad) {
        this.usuario = usuario;
        this.instalacion = instalacion;
        this.fecha = fecha;
        this.inicio = inicio;
        this.fin = fin;
        this.cantidadJugadores = cantidadJugadores;
        this.modalidad = modalidad;
    }
    public Usuario getUsuario() {
	return usuario;
	}
    public Instalacion getInstalacion() {
	return instalacion;
	}
    public LocalDate getFecha() {
	return fecha;
	}
    public LocalTime getInicio() {
	return inicio;
	}
    public LocalTime getFin() {
	return fin;
	}
    public int getCantidadJugadores() {
	return cantidadJugadores;
	}
    public String getModalidad() {
	return modalidad;
	}
    public boolean seSuperpone(Reserva otra) {
        return otra != null && instalacion == otra.getInstalacion() && fecha.equals(otra.getFecha())
                && inicio.isBefore(otra.getFin()) && otra.getInicio().isBefore(fin);
    }
}
