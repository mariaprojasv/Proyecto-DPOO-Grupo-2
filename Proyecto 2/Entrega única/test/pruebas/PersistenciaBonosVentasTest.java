package pruebas;

import static org.junit.jupiter.api.Assertions.*;
import java.nio.file.Path;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import administracion.ReporteVentas;
import club.ClubDeportivoSeneca;
import usuarios.Socio;
import Tienda.Articulo;
import Tienda.Venta;
import cafeteria.bebida;
import persistencia.PersistenciaArchivos;
import torneos.BonoDescuento;

/** Requisito: las compras pagadas con bono siguen identificables tras reiniciar. */
class PersistenciaBonosVentasTest {
    @TempDir Path carpeta;

    @Test void ventasConBonoSeConservanEnTiendaYCafeteria() throws Exception {
        ClubDeportivoSeneca club = new ClubDeportivoSeneca("Club");
        Socio socio = new Socio("socio", "clave", "Socio", 20);
        club.registrarUsuario(socio);
        Articulo balon = new Articulo("Balon", "Deportes", 20000, 10);
        club.getTienda().agregarArticulo(balon);
        club.getCafeteria().agregarProducto(new bebida("Cafe", 5000, 10, true));
        BonoDescuento bonoTienda = new BonoDescuento(socio, 10, "Torneo A");
        BonoDescuento bonoCafeteria = new BonoDescuento(socio, 15, "Torneo B");
        socio.guardarBono(bonoTienda);
        socio.guardarBono(bonoCafeteria);
        Venta venta = club.getTienda().crearVenta(socio);
        venta.agregarDetalle(balon, 2);
        venta.aplicarBono(bonoTienda);
        venta.finalizarVenta();
        club.getTienda().registrarVentaFinalizada(venta);
        club.getCafeteria().registrarVentaConBono(socio, "Cafe", 1, 0, bonoCafeteria);

        PersistenciaArchivos persistencia = new PersistenciaArchivos(carpeta.toString());
        persistencia.guardar(club);
        ClubDeportivoSeneca restaurado = new ClubDeportivoSeneca("Club");
        persistencia.cargar(restaurado);

        ReporteVentas reporte = new ReporteVentas(restaurado, LocalDate.now().minusDays(1), LocalDate.now().plusDays(1));
        assertEquals(2, reporte.getVentasConBono());
        assertEquals(bonoTienda.getId(), restaurado.getTienda().getVentas().get(0).getBonoAplicado().getId());
        assertEquals(bonoCafeteria.getId(), restaurado.getCafeteria().getVentas().get(0).getBono().getId());
        assertFalse(restaurado.getTienda().getVentas().get(0).getBonoAplicado().estaDisponible());
        assertFalse(restaurado.getCafeteria().getVentas().get(0).getBono().estaDisponible());
        assertEquals(8, restaurado.getTienda().buscarArticulo("Balon").getCantidad());
        assertEquals(9, restaurado.getCafeteria().buscarProducto("Cafe").getCantidad());
    }
}
