package usuarios;

import club.ClubDeportivoSeneca;
import cafeteria.Producto;
import Tienda.Articulo;
import reservas.Instalacion;

public class Administrador extends Empleado {
    public Administrador(String login, String password, String nombre) {
        super(login, password, nombre);
    }

    public boolean registrarUsuario(ClubDeportivoSeneca club, Usuario usuario) {
        if (club == null) {
            return false;
        }
        return club.registrarUsuario(usuario);
    }

    public boolean agregarInstalacion(ClubDeportivoSeneca club, Instalacion instalacion) {
        if (club == null) {
            return false;
        }
        return club.getGestionReservas().agregarInstalacion(instalacion);
    }

    public boolean agregarArticulo(ClubDeportivoSeneca club, Articulo articulo) {
        if (club == null || articulo == null || club.getTienda().buscarArticulo(articulo.getNombre()) != null) {
            return false;
        }
        club.getTienda().agregarArticulo(articulo);
        return true;
    }

    public boolean agregarProducto(ClubDeportivoSeneca club, Producto producto) {
        if (club == null || producto == null || club.getCafeteria().buscarProducto(producto.getNombre()) != null) {
            return false;
        }
        club.getCafeteria().agregarProducto(producto);
        return true;
    }

    public boolean reabastecerArticulo(ClubDeportivoSeneca club, String nombre, int cantidad) {
        if (club == null || cantidad <= 0 || club.getTienda().buscarArticulo(nombre) == null) {
            return false;
        }
        club.getTienda().reabastecer(nombre, cantidad);
        return true;
    }

    public boolean reabastecerProducto(ClubDeportivoSeneca club, String nombre, int cantidad) {
        if (club == null || cantidad <= 0 || club.getCafeteria().buscarProducto(nombre) == null) {
            return false;
        }
        club.getCafeteria().reabastecer(nombre, cantidad);
        return true;
    }
}
