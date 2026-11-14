package administracion;

import club.ClubDeportivoSeneca;
import Tienda.Articulo;
import Tienda.tienda;

public class ReporteInventarios {
    private final int ubicaciones;
    private final int referencias;
    private final int unidades;
    private final double valorExistencias;

    public ReporteInventarios(ClubDeportivoSeneca club) {
        if (club == null) {
            throw new IllegalArgumentException("El club es obligatorio");
        }
        int cantidadReferencias = 0;
        int cantidadUnidades = 0;
        double valor = 0;
        int cantidadUbicaciones = 1 + club.getGestionInventarios().getUbicaciones().size();
        tienda principal = club.getTienda();
        for (Articulo articulo : principal.getArticulos()) {
            if (articulo != null) {
                cantidadReferencias++;
                cantidadUnidades += articulo.getCantidad();
                valor += articulo.getCantidad() * articulo.getPrecio();
            }
        }
        for (String ubicacion : club.getGestionInventarios().getUbicaciones()) {
            tienda inventario = club.getGestionInventarios().consultarTienda(ubicacion);
            for (Articulo articulo : inventario.getArticulos()) {
                if (articulo != null) {
                    cantidadReferencias++;
                    cantidadUnidades += articulo.getCantidad();
                    valor += articulo.getCantidad() * articulo.getPrecio();
                }
            }
        }
        ubicaciones = cantidadUbicaciones;
        referencias = cantidadReferencias;
        unidades = cantidadUnidades;
        valorExistencias = valor;
    }

    public int getUbicaciones() { return ubicaciones; }
    public int getReferencias() { return referencias; }
    public int getUnidades() { return unidades; }
    public double getValorExistencias() { return valorExistencias; }
}
