package pruebas;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import club.ClubDeportivoSeneca;
import deportes.Categoria;
import deportes.CompetenciaLiga;
import deportes.Deporte;
import deportes.Equipo;
import deportes.EstadisticaFutbol;
import deportes.PartidoCompetencia;
import academia.Entrenamiento;
import reservas.Instalacion;
import Tienda.Articulo;
import Tienda.Venta;
import cafeteria.bebida;
import cafeteria.snack;
import usuarios.Administrador;
import usuarios.Entrenador;
import usuarios.Jugador;
import usuarios.Socio;
import persistencia.PersistenciaArchivos;

public class PruebaGeneral {
    private static int verificaciones = 0;

    public static void comprobar(boolean condicion, String mensaje) {
        if (!condicion) {
            throw new RuntimeException("FALLO: " + mensaje);
        }
        verificaciones++;
        System.out.println("OK: " + mensaje);
    }

    public static void main(String[] args) throws Exception {
        String carpeta = "data/demo";
        if (args.length > 0) {
            carpeta = args[0];
        }

        ClubDeportivoSeneca club = new ClubDeportivoSeneca("Club Deportivo Seneca");
        Administrador administrador = new Administrador("admin", "1234", "Administrador Demo");
        Entrenador entrenador = new Entrenador("entrenador", "1234", "Entrenador Demo", "Futbol");
        Socio socio = new Socio("socio", "1234", "Socio Demo", 0);
        comprobar(club.registrarUsuario(administrador), "Administrador registrado");
        comprobar(club.registrarUsuario(entrenador), "Entrenador registrado");
        comprobar(club.registrarUsuario(socio), "Socio registrado");
        comprobar(club.iniciarSesion("admin", "1234") == administrador, "Inicio de sesion");

        Deporte futbol = new Deporte("Futbol", 7, 3, 1);
        Categoria mayores = new Categoria("Mayores", 18, 99);
        club.getAreaDeportiva().agregarDeporte(futbol);
        club.getAreaDeportiva().agregarCategoria(mayores);
        Equipo leones = club.getAreaDeportiva().crearEquipo("Leones", futbol, mayores, 18);
        ArrayList<Jugador> jugadores = new ArrayList<>();
        for (int i = 1; i <= 8; i++) {
            Jugador jugador = new Jugador("jug" + i, "1234", "Jugador " + i, 0, LocalDate.of(2000, 1, i));
            club.registrarUsuario(jugador);
            jugadores.add(jugador);
            comprobar(club.getAreaDeportiva().inscribirJugadorEnEquipo(jugador, leones, "Delantero", i), "Jugador " + i + " inscrito");
        }
        comprobar(leones.tieneMinimoJugadores(), "Equipo con jugadores suficientes");

        CompetenciaLiga liga = new CompetenciaLiga("Liga Demo", "Club Seneca", "2026-2", 7, futbol, mayores);
        club.getAreaDeportiva().crearCompetencia(liga);
        comprobar(club.getAreaDeportiva().inscribirEquipoEnCompetencia(leones, liga, jugadores), "Equipo inscrito en liga");
        PartidoCompetencia partido = new PartidoCompetencia(LocalDate.of(2026, 11, 3), futbol, leones, "Rivales", "Cancha Norte");
        partido.registrarMarcador(2, 1);
        partido.registrarEstadistica(new EstadisticaFutbol(jugadores.get(0), 2, 0));
        club.getAreaDeportiva().registrarPartidoCompetencia(liga, partido);
        comprobar(liga.getTablaPosiciones().get(0).getPuntos() == 3, "Tabla de liga actualizada");

        comprobar(club.getAcademia().inscribirJugador(jugadores.get(0), futbol), "Inscripcion en academia");
        Entrenamiento entrenamiento = club.getAcademia().programarEntrenamiento(LocalDate.of(2026, 11, 4), futbol, entrenador);
        comprobar(entrenamiento != null, "Entrenamiento programado");
        comprobar(club.getAcademia().registrarAsistencia(entrenamiento, jugadores.get(0), true, "Asistio"), "Asistencia registrada");

        HashSet<String> modalidades = new HashSet<>();
        modalidades.add("Futbol 7");
        Instalacion cancha = new Instalacion("Cancha Principal", futbol, 14, modalidades);
        comprobar(club.getGestionReservas().agregarInstalacion(cancha), "Instalacion registrada");
        comprobar(club.getGestionReservas().realizarReserva(socio, cancha, LocalDate.of(2026, 11, 5), LocalTime.of(10, 0), LocalTime.of(11, 0), 10, "Futbol 7") != null, "Reserva valida");
        comprobar(club.getGestionReservas().realizarReserva(socio, cancha, LocalDate.of(2026, 11, 5), LocalTime.of(10, 30), LocalTime.of(11, 30), 10, "Futbol 7") == null, "Conflicto de reserva rechazado");

        Articulo camiseta = new Articulo("Camiseta", "Ropa", 50000, 20);
        club.getTienda().agregarArticulo(camiseta);
        Venta venta = club.getTienda().crearVenta(socio);
        venta.agregarDetalle(camiseta, 2);
        venta.calcularTotal();
        comprobar(camiseta.getCantidad() == 18, "Inventario descontado");
        club.getCafeteria().agregarProducto(new bebida("Cafe", 5000, 20, true));
        club.getCafeteria().agregarProducto(new snack("Sandwich", 10000, 10, "Gluten"));
        comprobar(club.getCafeteria().venderProducto("Cafe", 2), "Compra en cafeteria");

        PersistenciaArchivos persistencia = new PersistenciaArchivos(carpeta);
        persistencia.guardar(club);
        ClubDeportivoSeneca recuperado = new ClubDeportivoSeneca("Club Deportivo Seneca");
        persistencia.cargar(recuperado);
        comprobar(recuperado.getUsuarios().size() == club.getUsuarios().size(), "Usuarios recuperados");
        comprobar(recuperado.getAreaDeportiva().getEquipos().size() == 1, "Equipo recuperado");
        comprobar(recuperado.getAreaDeportiva().getCompetencias().size() == 1, "Competencia recuperada");
        comprobar(recuperado.getGestionReservas().getReservas().size() == 1, "Reserva recuperada");
        comprobar(recuperado.getAcademia().getEntrenamientos().size() == 1, "Entrenamiento recuperado");
        comprobar(recuperado.getTienda().getVentas().size() == 1, "Venta recuperada");
        comprobar(recuperado.getTienda().buscarArticulo("Camiseta").getCantidad() == 18, "Inventario recuperado sin doble descuento");
        comprobar(recuperado.getCafeteria().buscarProducto("Cafe").getCantidad() == 18, "Cafeteria recuperada");
        System.out.println("DEMOSTRACION COMPLETA: " + verificaciones + " verificaciones correctas");
        System.out.println("Archivos guardados en: " + carpeta);
    }
}
