package pruebas;

import java.time.LocalDate;
import academia.Academia;
import academia.Entrenamiento;
import deportes.Deporte;
import usuarios.Entrenador;
import usuarios.Jugador;

public class PruebaAcademia {
    public static void main(String[] args) {
        Academia academia = new Academia();
        Deporte tenis = new Deporte("Tenis", 2, 1, 0);
        Jugador ana = new Jugador("ana", "1234", "Ana", 0, LocalDate.of(2000, 1, 1));
        Jugador luis = new Jugador("luis", "1234", "Luis", 0, LocalDate.of(2001, 1, 1));
        Entrenador entrenador = new Entrenador("ent", "1234", "Carlos", "Tenis");
        comprobar(academia.inscribirJugador(ana, tenis), "Inscripcion correcta");
        comprobar(!academia.inscribirJugador(ana, tenis), "No repetir inscripcion");
        Entrenamiento e = academia.programarEntrenamiento(LocalDate.of(2026, 11, 15), tenis, entrenador);
        comprobar(e != null, "Programar entrenamiento");
        comprobar(academia.registrarAsistencia(e, ana, true, "Buen trabajo"), "Registrar asistencia");
        comprobar(!academia.registrarAsistencia(e, ana, false, ""), "No repetir asistencia");
        comprobar(!academia.registrarAsistencia(e, luis, true, ""), "Rechazar no inscrito");
        comprobar(e.getAsistencias().size() == 1, "Una asistencia registrada");
        comprobar(academia.programarPartidoPractica(LocalDate.of(2026, 11, 16), tenis, "Practica") != null, "Partido practica");
        System.out.println("PruebaAcademia terminada correctamente.");
    }
    private static void comprobar(boolean condicion, String mensaje) {
        if (!condicion) throw new AssertionError(mensaje);
        System.out.println("OK: " + mensaje);
    }
}
