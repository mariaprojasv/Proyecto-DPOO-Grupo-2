package pruebas;

import java.time.LocalDate;
import Tienda.Articulo;
import Tienda.Venta;
import deportes.Categoria;
import deportes.CompetenciaLiga;
import deportes.Deporte;
import deportes.Equipo;
import deportes.PartidoCompetencia;
import usuarios.Socio;

public class PruebaFinal {
    private static int verificaciones = 0;

    public static void comprobar(boolean condicion, String nombre) {
        if (!condicion) {
            throw new RuntimeException("Fallo: " + nombre);
        }
        verificaciones++;
    }

    public static void main(String[] args) {
        Deporte futbol = new Deporte("Futbol", 7, 3, 1);
        Categoria mayores = new Categoria("Mayores", 18, 99);
        Equipo equipo = new Equipo("Leones", 18, futbol, mayores);
        PartidoCompetencia partido = new PartidoCompetencia(LocalDate.now(), futbol, equipo, "Rival", "Cancha");
        partido.registrarMarcador(2, 1);
        comprobar(partido.getMarcadorLocal() == 2, "Marcador valido");
        partido.registrarMarcador(-1, 3);
        comprobar(partido.getMarcadorLocal() == 2, "Marcador negativo rechazado");
        CompetenciaLiga liga = new CompetenciaLiga("Liga", "Club", "2026", 7, futbol, mayores);
        liga.registrarPartido(partido);
        liga.registrarPartido(partido);
        comprobar(liga.getPartidos().size() == 1, "Partido no duplicado");
        comprobar(liga.getTablaPosiciones().get(0).getPartidosJugados() == 1, "Tabla no duplicada");
        Articulo articulo = new Articulo("Camiseta", "Ropa", 10000, 10);
        Venta venta = new Venta(new Socio("socio", "1234", "Socio", 0));
        venta.agregarDetalle(articulo, 1);
        venta.aplicarDescuento(10);
        venta.agregarDetalle(articulo, 1);
        venta.calcularTotal();
        comprobar(venta.getSubtotal() == 20000, "Subtotal correcto");
        comprobar(venta.getDescuento() == 2000, "Descuento actualizado");
        comprobar(articulo.getCantidad() == 8, "Inventario correcto");
        System.out.println("PruebaFinal: " + verificaciones + "/" + verificaciones);
    }
}
