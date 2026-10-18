package pruebas;

import java.io.File;
import club.ClubDeportivoSeneca;
import persistencia.PersistenciaArchivos;
import Tienda.Articulo;
import Tienda.Venta;
import usuarios.Socio;

public class PruebaPersistenciaVentas {
    public static void main(String[] args) throws Exception {
        ClubDeportivoSeneca club = new ClubDeportivoSeneca("Seneca");
        Socio socio = new Socio("cliente1", "1234", "Cliente", 0);
        club.registrarUsuario(socio);
        Articulo camiseta = new Articulo("Camiseta", "Ropa", 50000, 10);
        club.getTienda().agregarArticulo(camiseta);
        Venta venta = club.getTienda().crearVenta(socio);
        venta.agregarDetalle(camiseta, 2);
        venta.aplicarDescuento(10);
        venta.calcularTotal();
        int stock = camiseta.getCantidad();
        double total = venta.getTotal();

        File carpeta = new File("data/prueba_ventas");
        PersistenciaArchivos persistencia = new PersistenciaArchivos(carpeta.getPath());
        persistencia.guardar(club);

        ClubDeportivoSeneca recuperado = new ClubDeportivoSeneca("Seneca");
        persistencia.cargar(recuperado);
        verificar(recuperado.getTienda().getVentas().size() == 1, "Venta recuperada");
        Venta recuperada = recuperado.getTienda().getVentas().get(0);
        verificar(recuperada.getDetalles().size() == 1, "Detalle recuperado");
        verificar(recuperada.getDetalles().get(0).getCantidad() == 2, "Cantidad recuperada");
        verificar(recuperada.getTotal() == total, "Total recuperado");
        verificar(recuperado.getTienda().getArticulos().get(0).getCantidad() == stock, "Inventario sin doble descuento");
        verificar(recuperada.getComprador() == recuperado.buscarUsuario("cliente1"), "Comprador recuperado");
        System.out.println("PruebaPersistenciaVentas: 6/6");
    }

    private static void verificar(boolean condicion, String mensaje) {
        if (!condicion) {
            throw new RuntimeException(mensaje);
        }
    }
}
