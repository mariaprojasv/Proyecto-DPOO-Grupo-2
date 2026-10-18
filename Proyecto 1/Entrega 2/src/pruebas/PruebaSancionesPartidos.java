package pruebas;

import java.time.LocalDate;
import deportes.Categoria;
import deportes.ControlSanciones;
import deportes.Deporte;
import deportes.Equipo;
import deportes.PartidoInterno;
import deportes.Tarjeta;
import usuarios.Jugador;

public class PruebaSancionesPartidos {
    private static int verificaciones = 0;

    public static void comprobar(boolean condicion, String nombre) {
        if (!condicion) {
            throw new RuntimeException("Fallo: " + nombre);
        }
        verificaciones++;
    }

    public static void main(String[] args) {
        Deporte futbol = new Deporte("Futbol", 1, 3, 1);
        Categoria categoria = new Categoria("Mayores", 18, 60);
        Equipo local = new Equipo("Local", 5, futbol, categoria);
        Equipo rival = new Equipo("Rival", 5, futbol, categoria);
        Jugador ana = new Jugador("ana15", "123", "Ana", 0, LocalDate.of(2000, 1, 1));
        Jugador luis = new Jugador("luis15", "123", "Luis", 0, LocalDate.of(2000, 1, 1));
        comprobar(local.agregarJugador(ana, "Delantera", 7), "Inscripcion local");
        comprobar(rival.agregarJugador(luis, "Defensa", 4), "Inscripcion rival");
        ControlSanciones control = new ControlSanciones();
        PartidoInterno primero = new PartidoInterno(LocalDate.of(2026, 10, 18), futbol, local, rival);
        primero.establecerControlSanciones(control);
        comprobar(primero.puedeJugarse(), "Partido habilitado");
        comprobar(primero.puedeAlinearse(ana), "Alineacion permitida");
        comprobar(!primero.puedeAlinearse(luis), "No alinear rivales como locales");
        comprobar(primero.registrarTarjeta(new Tarjeta(ana, "AMARILLA")), "Primera amarilla");
        comprobar(primero.registrarTarjeta(new Tarjeta(ana, "AMARILLA")), "Segunda amarilla");
        comprobar(control.estaSuspendido(ana), "Suspension aplicada");
        comprobar(primero.getTarjetas().size() == 2, "Tarjetas guardadas");
        PartidoInterno siguiente = new PartidoInterno(LocalDate.of(2026, 10, 25), futbol, local, rival);
        siguiente.establecerControlSanciones(control);
        comprobar(!siguiente.puedeAlinearse(ana), "Sancion en partido siguiente");
        comprobar(!siguiente.registrarTarjeta(new Tarjeta(ana, "AZUL")), "Rechazar tarjeta invalida");
        comprobar(siguiente.getTarjetas().isEmpty(), "No guardar tarjeta invalida");
        control.cumplirSuspension(ana);
        comprobar(siguiente.puedeAlinearse(ana), "Alineacion tras cumplir suspension");
        System.out.println("PruebaSancionesPartidos: " + verificaciones + "/" + verificaciones);
    }
}
