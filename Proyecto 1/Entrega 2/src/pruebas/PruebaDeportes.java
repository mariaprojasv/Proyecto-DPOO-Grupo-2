package pruebas;

import java.time.LocalDate;
import java.util.ArrayList;
import deportes.AreaDeportiva;
import deportes.Categoria;
import deportes.CompetenciaLiga;
import deportes.Deporte;
import deportes.Equipo;
import deportes.EstadisticaFutbol;
import deportes.PartidoCompetencia;
import deportes.PartidoInterno;
import deportes.PosicionTabla;
import deportes.Tarjeta;
import usuarios.Jugador;

public class PruebaDeportes {

	public static void main(String[] args) {
		AreaDeportiva area = new AreaDeportiva();

		// 1. crear deportes y categorias
		Deporte futbol = new Deporte("Futbol", 7, 3, 1);
		Deporte baloncesto = new Deporte("Baloncesto", 5, 2, 0);
		area.agregarDeporte(futbol);
		area.agregarDeporte(baloncesto);
		Categoria mayores = new Categoria("Mayores", 18, 99);
		Categoria sub17 = new Categoria("Sub-17", 15, 17);
		area.agregarCategoria(mayores);
		area.agregarCategoria(sub17);
		System.out.println("Deportes: " + area.getDeportes().size() + ", categorias: " + area.getCategorias().size());

		// 2. crear equipos
		Equipo leones = area.crearEquipo("Leones", futbol, mayores, 18);
		Equipo tigres = area.crearEquipo("Tigres", futbol, mayores, 18);
		Equipo aguilas = area.crearEquipo("Aguilas", baloncesto, mayores, 12);
		System.out.println("Equipos creados: " + area.getEquipos().size());

		// 3. crear jugadores e inscripciones
		ArrayList<Jugador> jugadores = new ArrayList<>();
		for (int i = 1; i <= 8; i++) {
			Jugador j = new Jugador("jug" + i, "1234", "Jugador " + i, 0, LocalDate.of(2000, 1, i));
			jugadores.add(j);
			boolean ok = area.inscribirJugadorEnEquipo(j, leones, "delantero", i);
			System.out.println("Inscribir " + j.getNombre() + " en Leones con #" + i + ": " + ok);
		}

		// inscripciones que deben fallar
		Jugador extra = new Jugador("extra", "1234", "Extra", 0, LocalDate.of(2001, 5, 5));
		boolean r1 = area.inscribirJugadorEnEquipo(extra, leones, "portero", 3);
		System.out.println("Numero repetido (#3): " + r1);
		boolean r2 = area.inscribirJugadorEnEquipo(jugadores.get(0), tigres, "defensa", 30);
		System.out.println("Mismo jugador en otro equipo del mismo deporte: " + r2);
		boolean r3 = area.inscribirJugadorEnEquipo(jugadores.get(0), aguilas, "base", 10);
		System.out.println("Mismo jugador en otro deporte: " + r3);
		Jugador menor = new Jugador("menor", "1234", "Menor", 0, LocalDate.of(2012, 3, 3));
		boolean r4 = area.inscribirJugadorEnEquipo(menor, leones, "defensa", 40);
		System.out.println("Menor de edad en Mayores: " + r4);

		// 4. Crear Partido Interno
		System.out.println("Leones tiene minimo? " + leones.tieneMinimoJugadores());
		System.out.println("Tigres tiene minimo? " + tigres.tieneMinimoJugadores());
		PartidoInterno rechazado = area.programarPartidoInterno(LocalDate.now(), leones, tigres);
		if (rechazado == null) {
			System.out.println("Partido Leones vs Tigres (Tigres sin minimo): RECHAZADO");
		} else {
			System.out.println("Partido Leones vs Tigres (Tigres sin minimo): creado");
		}

		for (int i = 20; i < 27; i++) {
			Jugador j = new Jugador("t" + i, "1234", "Tigre " + i, 0, LocalDate.of(1999, 2, 2));
			area.inscribirJugadorEnEquipo(j, tigres, "medio", i);
		}
		PartidoInterno partido = area.programarPartidoInterno(LocalDate.now(), leones, tigres);
		if (partido == null) {
			System.out.println("Partido Leones vs Tigres (ambos con minimo): rechazado");
		} else {
			System.out.println("Partido Leones vs Tigres (ambos con minimo): CREADO");
		}
		partido.registrarMarcador(2, 1);
		partido.registrarEstadistica(new EstadisticaFutbol(jugadores.get(0), 2, 0));
		partido.registrarTarjeta(new Tarjeta(jugadores.get(1), "ROJA"));
		System.out.println("Resultado para Leones: " + partido.getResultado());
		System.out.println("Jugador 2 suspendido para el proximo partido? " + area.estaSuspendido(jugadores.get(1), leones));
		System.out.println("Jugador 1 suspendido? " + area.estaSuspendido(jugadores.get(0), leones));

		// 5. Crear competencia liga
		CompetenciaLiga cerros = new CompetenciaLiga("Torneo de los Cerros", "Universidades", "2026-2", 7, futbol, mayores);
		area.crearCompetencia(cerros);
		boolean insc1 = area.inscribirEquipoEnCompetencia(leones, cerros, jugadores);
		System.out.println("Inscribir Leones en Cerros: " + insc1);
		boolean insc2 = area.inscribirEquipoEnCompetencia(aguilas, cerros, jugadores);
		System.out.println("Inscribir Aguilas (baloncesto) en Cerros: " + insc2);

		PartidoCompetencia pc1 = new PartidoCompetencia(LocalDate.now(), futbol, leones, "Universidad Rival", "Cancha 1");
		pc1.registrarMarcador(3, 0);
		pc1.registrarEstadistica(new EstadisticaFutbol(jugadores.get(2), 3, 1));
		area.registrarPartidoCompetencia(cerros, pc1);

		PartidoCompetencia pc2 = new PartidoCompetencia(LocalDate.now(), futbol, leones, "Otra Universidad", "Cancha 2");
		pc2.registrarMarcador(1, 1);
		area.registrarPartidoCompetencia(cerros, pc2);

		System.out.println("--- Tabla de posiciones ---");
		for (PosicionTabla fila : cerros.getTablaPosiciones()) {
			System.out.println(fila.getEquipo().getNombre() + " | PJ " + fila.getPartidosJugados() + " | G " + fila.getGanados() + " | E " + fila.getEmpatados() + " | P " + fila.getPerdidos() + " | Pts " + fila.getPuntos());
		}

		Jugador goleador = cerros.getJugadorDestacado();
		int goles = goleador.getEstadisticasAcumuladas(cerros).getValorDestacado();
		System.out.println("Goleador: " + goleador.getNombre() + " con " + goles + " goles");
	}
}