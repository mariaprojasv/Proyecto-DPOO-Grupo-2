package pruebas;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import Tienda.Articulo;
import Tienda.Venta;
import Tienda.tienda;
import cafeteria.Cafeteria;
import cafeteria.VentaCafeteria;
import cafeteria.snack;
import usuarios.Socio;

/** Requisitos: no perder ventas, evitar alterar reportes y restaurar sin descontar stock. */
class IntegridadVentasTest {
    @Test
    void tiendaNoPermiteBorrarVentasPorSuListado() {
        tienda comercio = new tienda();
        Socio socio = new Socio("comprador", "clave", "Comprador", 0);
        Articulo articulo = new Articulo("Camiseta", "Ropa", 100, 3);
        comercio.agregarArticulo(articulo);
        assertTrue(comercio.venderArticulo(socio, "Camiseta", 1));
        comercio.getVentas().clear();
        assertEquals(1, comercio.getVentas().size());
        assertEquals(2, articulo.getCantidad());
    }

    @Test
    void restaurarVentaDeTiendaNoDescuentaExistenciasDosVeces() {
        tienda comercio = new tienda();
        Socio socio = new Socio("comprador", "clave", "Comprador", 0);
        Articulo articulo = new Articulo("Balon", "Deportes", 100, 5);
        comercio.agregarArticulo(articulo);
        Venta historica = new Venta(socio);
        historica.agregarDetalleGuardado(articulo, 2, 100);
        comercio.restaurarVenta(historica);
        assertEquals(5, articulo.getCantidad());
        assertEquals(1, comercio.getVentas().size());
        assertThrows(IllegalArgumentException.class, () -> comercio.restaurarVenta(historica));
    }

    @Test
    void cafeteriaConservaVentasYRestauracionNoCambiaInventario() {
        Cafeteria cafeteria = new Cafeteria();
        Socio socio = new Socio("comprador", "clave", "Comprador", 0);
        snack producto = new snack("Galleta", 100, 5, "");
        cafeteria.agregarProducto(producto);
        VentaCafeteria historica = new VentaCafeteria(socio, producto, 2, 0);
        cafeteria.restaurarVenta(historica);
        cafeteria.getVentas().clear();
        assertEquals(1, cafeteria.getVentas().size());
        assertEquals(5, producto.getCantidad());
        assertThrows(IllegalArgumentException.class, () -> cafeteria.restaurarVenta(historica));
    }
}
