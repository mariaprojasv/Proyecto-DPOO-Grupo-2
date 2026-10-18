package pruebas;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import deportes.*;
import usuarios.Jugador;

public class PruebaCorrecciones {
    private static int verificaciones = 0;
    private static void comprobar(boolean condicion, String mensaje) {
        if (!condicion) throw new AssertionError(mensaje);
        verificaciones++;
        System.out.println("OK: " + mensaje);
    }
    public static void main(String[] args) {
        Deporte futbol = new Deporte("Futbol", 2, 3, 1);
        Categoria adultos = new Categoria("Adultos", 18, 99);
        Equipo equipo = new Equipo("Leones", 3, futbol, adultos);
        Jugador a = new Jugador("a", "123", "Ana", 0, LocalDate.of(2000, 1, 1));
        Jugador b = new Jugador("b", "123", "Beto", 0, LocalDate.of(2000, 2, 1));
        Jugador externo = new Jugador("c", "123", "Carla", 0, LocalDate.of(2000, 3, 1));
        comprobar(equipo.agregarJugador(a, "Delantera", 1), "inscripcion valida");
        comprobar(!equipo.agregarJugador(a, "Defensa", 2), "rechazar jugador duplicado");
        comprobar(!equipo.agregarJugador(b, "Defensa", 1), "rechazar numero duplicado");
        comprobar(!equipo.agregarJugador(b, "Defensa", 0), "rechazar numero no positivo");
        comprobar(equipo.agregarJugador(b, "Defensa", 2), "segundo jugador valido");
        CompetenciaLiga liga = new CompetenciaLiga("Liga", "Club", "2026", 2, futbol, adultos);
        comprobar(!liga.inscribirEquipo(equipo, new ArrayList<>(Arrays.asList(a, externo))), "rechazar ajeno al equipo");
        comprobar(!liga.inscribirEquipo(equipo, new ArrayList<>(Arrays.asList(a, a))), "rechazar nomina duplicada");
        comprobar(liga.inscribirEquipo(equipo, new ArrayList<>(Arrays.asList(a, b))), "nomina valida");
        comprobar(!liga.inscribirEquipo(equipo, new ArrayList<>(Arrays.asList(a, b))), "rechazar equipo ya inscrito");
        EstadisticaBaloncesto basket = new EstadisticaBaloncesto(a, 10, 3);
        Estadistica vaciaBasket = basket.crearVacia();
        vaciaBasket.acumular(basket);
        comprobar(vaciaBasket instanceof EstadisticaBaloncesto && vaciaBasket.getValorDestacado() == 10, "acumulacion baloncesto");
        EstadisticaVoleibol voley = new EstadisticaVoleibol(a, 4, 2);
        Estadistica vaciaVoley = voley.crearVacia();
        vaciaVoley.acumular(voley);
        comprobar(vaciaVoley instanceof EstadisticaVoleibol && vaciaVoley.getValorDestacado() == 4, "acumulacion voleibol");
        System.out.println("TOTAL: " + verificaciones + " verificaciones correctas.");
    }
}
