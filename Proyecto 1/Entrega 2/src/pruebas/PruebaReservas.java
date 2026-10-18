package pruebas;

import deportes.Deporte;
import reservas.GestionReservas;
import reservas.Instalacion;
import reservas.Reserva;
import usuarios.Socio;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

public class PruebaReservas {
    private static int verificaciones = 0;
    private static void verificar(boolean condicion, String mensaje) {
        if (!condicion) throw new AssertionError(mensaje);
        verificaciones++;
    }
    public static void main(String[] args) {
        GestionReservas gestion = new GestionReservas();
        Deporte tenis = new Deporte("Tenis", 2, 1, 0);
        Instalacion cancha = new Instalacion("Cancha 1", tenis, 4, Set.of("individual", "dobles"));
        Instalacion cancha2 = new Instalacion("Cancha 2", tenis, 4, Set.of("individual"));
        Socio ana = new Socio("ana", "1234", "Ana", 0);
        Socio luis = new Socio("luis", "1234", "Luis", 0);
        LocalDate dia = LocalDate.of(2026, 11, 12);
        verificar(gestion.agregarInstalacion(cancha), "Registrar instalacion");
        verificar(gestion.agregarInstalacion(cancha2), "Registrar segunda instalacion");
        verificar(!gestion.agregarInstalacion(cancha), "No duplicar instalacion");
        Reserva primera = gestion.realizarReserva(ana, cancha, dia, LocalTime.of(9, 0), LocalTime.of(10, 0), 2, "individual");
        verificar(primera != null, "Reserva valida");
        verificar(gestion.realizarReserva(luis, cancha, dia, LocalTime.of(9, 30), LocalTime.of(10, 30), 2, "dobles") == null, "Rechazar solapamiento parcial");
        verificar(gestion.realizarReserva(luis, cancha, dia, LocalTime.of(9, 0), LocalTime.of(10, 0), 2, "dobles") == null, "Rechazar horario identico");
        verificar(gestion.realizarReserva(luis, cancha, dia, LocalTime.of(10, 0), LocalTime.of(11, 0), 4, "dobles") != null, "Permitir horario contiguo");
        verificar(gestion.realizarReserva(luis, cancha2, dia, LocalTime.of(9, 0), LocalTime.of(10, 0), 2, "individual") != null, "Permitir otra instalacion");
        verificar(gestion.realizarReserva(luis, cancha, dia.plusDays(1), LocalTime.of(9, 0), LocalTime.of(10, 0), 2, "individual") != null, "Permitir otra fecha");
        verificar(gestion.realizarReserva(ana, cancha, dia, LocalTime.of(12, 0), LocalTime.of(13, 0), 5, "dobles") == null, "Rechazar exceso capacidad");
        verificar(gestion.realizarReserva(ana, cancha, dia, LocalTime.of(12, 0), LocalTime.of(13, 0), 2, "futbol") == null, "Rechazar modalidad");
        verificar(gestion.realizarReserva(null, cancha, dia, LocalTime.of(12, 0), LocalTime.of(13, 0), 2, "dobles") == null, "Rechazar usuario nulo");
        verificar(gestion.realizarReserva(ana, cancha, dia, LocalTime.of(13, 0), LocalTime.of(12, 0), 2, "dobles") == null, "Rechazar horario invertido");
        verificar(gestion.realizarReserva(ana, cancha, dia, LocalTime.of(12, 0), LocalTime.of(13, 0), 0, "dobles") == null, "Rechazar cero jugadores");
        verificar(gestion.consultarReservas(cancha).size() == 3, "Consultar reservas instalacion");
        verificar(gestion.consultarReservas(ana).size() == 1, "Consultar reservas socio");
        verificar(gestion.getReservas().size() == 4, "Sin reservas invalidas registradas");
        System.out.println("OK: " + verificaciones + " verificaciones de reservas superadas.");
    }
}
