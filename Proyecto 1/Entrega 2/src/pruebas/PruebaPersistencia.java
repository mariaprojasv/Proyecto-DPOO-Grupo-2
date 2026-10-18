package pruebas;

import java.io.File;
import java.time.LocalDate;
import club.ClubDeportivoSeneca;
import persistencia.PersistenciaArchivos;
import usuarios.Jugador;
import usuarios.Socio;
import deportes.Deporte;
import Tienda.Articulo;
import cafeteria.bebida;

public class PruebaPersistencia {
    public static void main(String[] args) throws Exception {
        ClubDeportivoSeneca original = new ClubDeportivoSeneca("Seneca");
        original.registrarUsuario(new Socio("ana", "1234", "Ana", 10));
        original.registrarUsuario(new Jugador("juan", "abcd", "Juan", 20, LocalDate.of(2002, 5, 12)));
        original.getAreaDeportiva().agregarDeporte(new Deporte("Futbol", 7, 3, 1));
        original.getTienda().agregarArticulo(new Articulo("Camiseta", "Ropa", 50000, 5));
        original.getCafeteria().agregarProducto(new bebida("Cafe", 4000, 8, true));
        PersistenciaArchivos persistencia = new PersistenciaArchivos("data/prueba");
        persistencia.guardar(original);
        ClubDeportivoSeneca cargado = new ClubDeportivoSeneca("Seneca");
        persistencia.cargar(cargado);
        comprobar(cargado.getUsuarios().size() == 2, "usuarios");
        comprobar(cargado.iniciarSesion("ana", "1234") != null, "autenticacion");
        comprobar(cargado.getAreaDeportiva().getDeportes().size() == 1, "deportes");
        comprobar(cargado.getTienda().buscarArticulo("Camiseta").getCantidad() == 5, "articulos");
        comprobar(cargado.getCafeteria().buscarProducto("Cafe").getCantidad() == 8, "cafeteria");
        System.out.println("PruebaPersistencia: 5 verificaciones correctas");
    }

    private static void comprobar(boolean condicion, String nombre) {
        if (!condicion) {
            throw new RuntimeException("Error en " + nombre);
        }
    }
}
