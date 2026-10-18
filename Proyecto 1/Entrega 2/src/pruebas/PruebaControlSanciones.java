package pruebas;

import java.time.LocalDate;
import deportes.ControlSanciones;
import deportes.Tarjeta;
import usuarios.Jugador;

public class PruebaControlSanciones {
    private static int verificaciones = 0;

    public static void comprobar(boolean condicion, String nombre) {
        if (!condicion) {
            throw new RuntimeException("Fallo: " + nombre);
        }
        verificaciones++;
    }

    public static void main(String[] args) {
        Jugador ana = new Jugador("ana14", "123", "Ana", 0, LocalDate.of(2000, 1, 1));
        Jugador luis = new Jugador("luis14", "123", "Luis", 0, LocalDate.of(2000, 1, 1));
        ControlSanciones control = new ControlSanciones();
        comprobar(control.puedeAlinearse(ana), "Disponible inicialmente");
        comprobar(control.registrarTarjeta(new Tarjeta(ana, "AMARILLA")), "Primera amarilla");
        comprobar(control.getAmarillas(ana) == 1, "Una amarilla acumulada");
        comprobar(control.puedeAlinearse(ana), "Una amarilla no suspende");
        comprobar(control.registrarTarjeta(new Tarjeta(ana, "AMARILLA")), "Segunda amarilla");
        comprobar(control.estaSuspendido(ana), "Dos amarillas suspenden");
        comprobar(control.getAmarillas(ana) == 0, "Reinicio del conteo");
        comprobar(control.registrarTarjeta(new Tarjeta(luis, "ROJA")), "Tarjeta roja");
        comprobar(control.estaSuspendido(luis), "Roja suspende");
        comprobar(!control.registrarTarjeta(new Tarjeta(luis, "AZUL")), "Tarjeta invalida");
        comprobar(!control.registrarTarjeta(null), "Tarjeta nula");
        control.cumplirSuspension(ana);
        comprobar(control.puedeAlinearse(ana), "Suspension cumplida");
        comprobar(control.estaSuspendido(luis), "Suspension independiente");
        System.out.println("PruebaControlSanciones: " + verificaciones + "/" + verificaciones);
    }
}
