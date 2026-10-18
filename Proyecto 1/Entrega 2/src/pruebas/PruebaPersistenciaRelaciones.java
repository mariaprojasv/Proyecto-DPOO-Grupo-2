package pruebas;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import club.ClubDeportivoSeneca;
import deportes.Categoria;
import deportes.Deporte;
import deportes.Equipo;
import academia.Entrenamiento;
import persistencia.PersistenciaArchivos;
import reservas.Instalacion;
import usuarios.Entrenador;
import usuarios.Jugador;

public class PruebaPersistenciaRelaciones {
    public static void main(String[] args) throws Exception {
        ClubDeportivoSeneca original = new ClubDeportivoSeneca("Seneca");
        Deporte futbol = new Deporte("Futbol", 7, 3, 1);
        Categoria mayores = new Categoria("Mayores", 18, 99);
        Jugador jugador = new Jugador("jug1", "1234", "Jugador Uno", 0, LocalDate.of(2000, 1, 1));
        Entrenador entrenador = new Entrenador("ent1", "1234", "Entrenador Uno", "Futbol");
        original.registrarUsuario(jugador);
        original.registrarUsuario(entrenador);
        original.getAreaDeportiva().agregarDeporte(futbol);
        original.getAreaDeportiva().agregarCategoria(mayores);
        Equipo equipo = original.getAreaDeportiva().crearEquipo("Leones", futbol, mayores, 18);
        comprobar(original.getAreaDeportiva().inscribirJugadorEnEquipo(jugador, equipo, "Defensa", 4), "inscripcion inicial");
        HashSet<String> modalidades = new HashSet<>();
        modalidades.add("Futbol 7");
        Instalacion cancha = new Instalacion("Cancha Norte", futbol, 14, modalidades);
        original.getGestionReservas().agregarInstalacion(cancha);
        comprobar(original.getGestionReservas().realizarReserva(jugador, cancha, LocalDate.of(2026, 11, 2), LocalTime.of(10, 0), LocalTime.of(11, 0), 10, "Futbol 7") != null, "reserva inicial");
        comprobar(original.getAcademia().inscribirJugador(jugador, futbol), "academia inicial");
        Entrenamiento entrenamiento = original.getAcademia().programarEntrenamiento(LocalDate.of(2026, 11, 3), futbol, entrenador);
        comprobar(original.getAcademia().registrarAsistencia(entrenamiento, jugador, true, "Puntual"), "asistencia inicial");
        PersistenciaArchivos persistencia = new PersistenciaArchivos("data/prueba_relaciones");
        persistencia.guardar(original);
        ClubDeportivoSeneca cargado = new ClubDeportivoSeneca("Seneca");
        persistencia.cargar(cargado);
        comprobar(cargado.getAreaDeportiva().getCategorias().size() == 1, "categorias");
        comprobar(cargado.getAreaDeportiva().getEquipos().size() == 1, "equipos");
        comprobar(cargado.getAreaDeportiva().getEquipos().get(0).getJugadores().size() == 1, "jugadores del equipo");
        comprobar(cargado.getGestionReservas().getInstalaciones().size() == 1, "instalaciones");
        comprobar(cargado.getGestionReservas().getReservas().size() == 1, "reservas");
        comprobar(cargado.getGestionReservas().getReservas().get(0).getUsuario() == cargado.buscarUsuario("jug1"), "relacion usuario reserva");
        comprobar(cargado.getAcademia().getInscripciones().size() == 1, "inscripciones academia");
        comprobar(cargado.getAcademia().getEntrenamientos().size() == 1, "entrenamientos");
        comprobar(cargado.getAcademia().getEntrenamientos().get(0).getAsistencias().size() == 1, "asistencias");
        comprobar(cargado.buscarUsuario("ent1") instanceof Entrenador, "entrenador");
        System.out.println("PruebaPersistenciaRelaciones: 10 verificaciones correctas");
    }

    private static void comprobar(boolean condicion, String nombre) {
        if (!condicion) {
            throw new RuntimeException("Error en " + nombre);
        }
    }
}
