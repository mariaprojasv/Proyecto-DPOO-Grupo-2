package pruebas;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import Tienda.Articulo;
import Tienda.Venta;
import Tienda.tienda;
import usuarios.Socio;

class CicloVentaTiendaTest {
    @Test void carritoSinFinalizarNoApareceComoVenta() {
        tienda comercio = new tienda();
        Socio socio = new Socio("clienteR3", "clave", "Cliente", 0);
        Articulo articulo = new Articulo("Balon", "Deportes", 20000, 5);
        comercio.agregarArticulo(articulo);
        Venta carrito = comercio.crearVenta(socio);
        carrito.agregarDetalle(articulo, 2);
        assertTrue(comercio.getVentas().isEmpty());
        assertThrows(IllegalArgumentException.class, () -> comercio.registrarVentaFinalizada(carrito));
        comercio.cancelarVenta(carrito);
        assertEquals(5, articulo.getCantidad());
        assertThrows(IllegalStateException.class, () -> carrito.finalizarVenta());
    }

    @Test void compraConfirmadaSeRegistraUnaSolaVez() {
        tienda comercio = new tienda();
        Socio socio = new Socio("clienteR3b", "clave", "Cliente", 0);
        Articulo articulo = new Articulo("Balon", "Deportes", 20000, 5);
        comercio.agregarArticulo(articulo);
        Venta venta = comercio.crearVenta(socio);
        venta.agregarDetalle(articulo, 2);
        venta.finalizarVenta();
        comercio.registrarVentaFinalizada(venta);
        assertEquals(1, comercio.getVentas().size());
        assertEquals(3, articulo.getCantidad());
        assertThrows(IllegalArgumentException.class, () -> comercio.registrarVentaFinalizada(venta));
        assertThrows(IllegalArgumentException.class, () -> comercio.cancelarVenta(venta));
    }
}
