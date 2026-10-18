package pruebas;

import java.time.LocalDate;
import java.util.ArrayList;
import club.ClubDeportivoSeneca;
import deportes.Categoria;
import deportes.CompetenciaLiga;
import deportes.Deporte;
import deportes.Equipo;
import deportes.EstadisticaFutbol;
import deportes.PartidoCompetencia;
import persistencia.PersistenciaArchivos;
import usuarios.Jugador;

public class PruebaPersistenciaCompetencias {
    public static void main(String[] args) throws Exception {
        ClubDeportivoSeneca original = new ClubDeportivoSeneca("Seneca");
        Deporte futbol = new Deporte("Futbol", 7, 3, 1);
        Categoria mayores = new Categoria("Mayores", 18, 99);
        original.getAreaDeportiva().agregarDeporte(futbol);
        original.getAreaDeportiva().agregarCategoria(mayores);
        Equipo leones = original.getAreaDeportiva().crearEquipo("Leones", futbol, mayores, 12);
        ArrayList<Jugador> jugadores = new ArrayList<>();
        for (int i = 1; i <= 7; i++) {
            Jugador j = new Jugador("jug" + i, "1234", "Jugador " + i, 0, LocalDate.of(2000, 1, i));
            original.registrarUsuario(j);
            original.getAreaDeportiva().inscribirJugadorEnEquipo(j, leones, "Jugador", i);
            jugadores.add(j);
        }
        CompetenciaLiga liga = new CompetenciaLiga("Liga", "Seneca", "2026", 7, futbol, mayores);
        original.getAreaDeportiva().crearCompetencia(liga);
        comprobar(liga.inscribirEquipo(leones, jugadores), "nomina original");
        PartidoCompetencia partido = new PartidoCompetencia(LocalDate.of(2026, 10, 1), futbol, leones, "Rivales", "Cancha");
        partido.registrarMarcador(3, 1);
        partido.registrarEstadistica(new EstadisticaFutbol(jugadores.get(0), 2, 1));
        original.getAreaDeportiva().registrarPartidoCompetencia(liga, partido);
        PersistenciaArchivos persistencia = new PersistenciaArchivos("data/prueba_competencias");
        persistencia.guardar(original);
        ClubDeportivoSeneca cargado = new ClubDeportivoSeneca("Seneca");
        persistencia.cargar(cargado);
        comprobar(cargado.getAreaDeportiva().getCompetencias().size() == 1, "competencia");
        CompetenciaLiga recuperada = (CompetenciaLiga) cargado.getAreaDeportiva().getCompetencias().get(0);
        comprobar(recuperada.getNominas().size() == 1, "nomina");
        comprobar(recuperada.getNominas().get(0).getJugadores().size() == 7, "jugadores");
        comprobar(recuperada.getPartidos().size() == 1, "partido");
        comprobar(recuperada.getPartidos().get(0).getMarcadorLocal() == 3, "marcador");
        comprobar(recuperada.getPartidos().get(0).getEstadisticas().size() == 1, "estadistica");
        comprobar(recuperada.getPartidos().get(0).getEstadisticas().get(0).getValorDestacado() == 2, "goles");
        comprobar(recuperada.getTablaPosiciones().get(0).getPuntos() == 3, "tabla");
        System.out.println("PruebaPersistenciaCompetencias: 8 verificaciones correctas");
    }

    private static void comprobar(boolean condicion, String nombre) {
        if (!condicion) {
            throw new RuntimeException("Error en " + nombre);
        }
    }
}
