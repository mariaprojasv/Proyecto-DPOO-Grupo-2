package pruebas;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import Tienda.Articulo;
import Tienda.CodigoDescuento;
import Tienda.Venta;
import usuarios.Socio;

@DisplayName("Venta: inventario, descuentos y fidelizacion")
class VentaTest {
    private Socio socio;
    private Articulo articulo;
    private Venta venta;

    @BeforeEach
    void setUp() {
        // GIVEN: un socio con 100 puntos y articulo con 5 unidades.
        socio = new Socio("venta39", "1234", "Cliente", 100);
        articulo = new Articulo("Balon", "Deportes", 100, 5);
        venta = new Venta(socio);
    }

    @ParameterizedTest(name = "comprar {0} unidades deja {1} en inventario")
    @CsvSource({"1, 4", "3, 2", "5, 0"})
    void compraValidaReduceInventario(int cantidad, int esperado) {
        // WHEN
        venta.agregarDetalle(articulo, cantidad);
        // THEN
        assertEquals(esperado, articulo.getCantidad());
        assertEquals(100.0 * cantidad, venta.getSubtotal(), 0.0001);
    }

    @Test
    void compraMayorAlInventarioNoModificaEstado() {
        venta.agregarDetalle(articulo, 6);
        assertEquals(5, articulo.getCantidad());
        assertTrue(venta.getDetalles().isEmpty());
    }

    @Test
    void compraConCantidadCeroNoModificaEstado() {
        venta.agregarDetalle(articulo, 0);
        assertEquals(5, articulo.getCantidad());
        assertTrue(venta.getDetalles().isEmpty());
    }

    @Test
    void totalIncluyeIvaDelDiecinuevePorCiento() {
        venta.agregarDetalle(articulo, 1);
        venta.calcularTotal();
        assertEquals(119.0, venta.getTotal(), 0.0001);
    }

    @Test
    void descuentoSeAplicaAntesDelIva() {
        venta.agregarDetalle(articulo, 1);
        venta.aplicarCodigo(new CodigoDescuento("PROMO", 20));
        assertEquals(20.0, venta.getDescuento(), 0.0001);
        assertEquals(95.2, venta.getTotal(), 0.0001);
    }

    @Test
    void ventaSinDetallesNoSePuedeFinalizar() {
        assertThrows(IllegalStateException.class, () -> venta.finalizarVenta());
        assertFalse(venta.isFinalizada());
    }

    @Test
    void finalizarVentaAcreditaPuntosUnaSolaVez() {
        venta.agregarDetalle(articulo, 1);
        venta.finalizarVenta();
        assertEquals(102, socio.getPuntosFidelidad());
        assertTrue(venta.isFinalizada());
        assertThrows(IllegalStateException.class, () -> venta.finalizarVenta());
        assertEquals(102, socio.getPuntosFidelidad());
    }

    @Test
    void codigoSoloSeConsumeAlFinalizar() {
        CodigoDescuento codigo = new CodigoDescuento("PROMO", 20);
        venta.agregarDetalle(articulo, 1);
        venta.aplicarCodigo(codigo);
        assertTrue(codigo.estaDisponible());
        venta.finalizarVenta();
        assertFalse(codigo.estaDisponible());
    }

    @Test
    void puntosSeRedimenAlFinalizar() {
        venta.agregarDetalle(articulo, 1);
        venta.redimirPuntosEnVenta(50);
        assertEquals(100, socio.getPuntosFidelidad());
        assertEquals(69.0, venta.getTotal(), 0.0001);
        venta.finalizarVenta();
        assertEquals(51, socio.getPuntosFidelidad());
    }

    @Test
    void ventaFinalizadaNoPermiteNuevosProductos() {
        venta.agregarDetalle(articulo, 1);
        venta.finalizarVenta();
        assertThrows(IllegalStateException.class, () -> venta.agregarDetalle(articulo, 1));
        assertEquals(4, articulo.getCantidad());
    }
}
