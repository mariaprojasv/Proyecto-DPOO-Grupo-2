package pruebas;

import java.time.LocalTime;
import club.ClubDeportivoSeneca;
import usuarios.Administrador;
import usuarios.Entrenador;
import usuarios.Fisioterapeuta;
import usuarios.Socio;
import cafeteria.snack;
import cafeteria.VentaCafeteria;

public class PruebaAdministracionTurnosCafeteria {
    private static int verificaciones = 0;

    private static void verificar(boolean condicion) {
        if (!condicion) {
            throw new RuntimeException("Fallo en verificacion " + (verificaciones + 1));
        }
        verificaciones++;
    }

    public static void main(String[] args) {
        ClubDeportivoSeneca club = new ClubDeportivoSeneca("Seneca");
        Administrador administrador = new Administrador("admin", "123", "Admin");
        Socio socio = new Socio("s1", "123", "Socio", 0);
        verificar(administrador.registrarUsuario(club, socio));
        verificar(!administrador.registrarUsuario(club, socio));
        verificar(administrador.agregarProducto(club, new snack("Barra", 1000, 10, "Ninguno")));
        verificar(!administrador.agregarProducto(club, new snack("Barra", 1000, 10, "Ninguno")));
        verificar(administrador.reabastecerProducto(club, "Barra", 5));
        verificar(club.getCafeteria().buscarProducto("Barra").getCantidad() == 15);
        VentaCafeteria venta = club.getCafeteria().registrarVenta(socio, "Barra", 2, 500);
        verificar(venta != null);
        verificar(venta.getSubtotal() == 2000);
        verificar(venta.getImpuesto() == 160);
        verificar(venta.getTotal() == 2660);
        verificar(club.getCafeteria().buscarProducto("Barra").getCantidad() == 13);
        verificar(club.getCafeteria().registrarVenta(socio, "Barra", 50, 0) == null);
        verificar(club.getCafeteria().getVentas().size() == 1);
        verificar(!club.getGestionTurnos().coberturaCompleta(LocalTime.of(8, 0), LocalTime.of(10, 0)));
        verificar(club.getGestionTurnos().agregarTurno(new Entrenador("e1", "1", "Uno", "Futbol"), LocalTime.of(8, 0), LocalTime.of(10, 0)));
        verificar(club.getGestionTurnos().agregarTurno(new Entrenador("e2", "1", "Dos", "Futbol"), LocalTime.of(8, 0), LocalTime.of(10, 0)));
        verificar(club.getGestionTurnos().agregarTurno(new Fisioterapeuta("f1", "1", "Fis"), LocalTime.of(8, 0), LocalTime.of(10, 0)));
        verificar(club.getGestionTurnos().coberturaCompleta(LocalTime.of(8, 0), LocalTime.of(10, 0)));
        verificar(!club.getGestionTurnos().coberturaCompleta(LocalTime.of(8, 0), LocalTime.of(11, 0)));
        System.out.println("Revision 12: " + verificaciones + " verificaciones correctas");
    }
}
