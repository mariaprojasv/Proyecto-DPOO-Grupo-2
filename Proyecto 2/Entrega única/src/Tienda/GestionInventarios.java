package Tienda;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GestionInventarios {
    private final Map<String, tienda> tiendas;

    public GestionInventarios() {
        tiendas = new HashMap<>();
    }

    public void registrarTienda(String ubicacion, tienda inventario) {
        if (ubicacion == null || ubicacion.trim().isEmpty() || inventario == null) {
            throw new IllegalArgumentException("La ubicacion y la tienda son obligatorias");
        }
        String clave = ubicacion.trim().toLowerCase();
        if (tiendas.containsKey(clave)) {
            throw new IllegalStateException("Ya existe una tienda en esa ubicacion");
        }
        tiendas.put(clave, inventario);
    }

    public tienda consultarTienda(String ubicacion) {
        if (ubicacion == null || ubicacion.trim().isEmpty()) {
            throw new IllegalArgumentException("Debe indicar la ubicacion");
        }
        tienda inventario = tiendas.get(ubicacion.trim().toLowerCase());
        if (inventario == null) {
            throw new IllegalArgumentException("No existe una tienda en esa ubicacion");
        }
        return inventario;
    }

    public void transferirArticulo(String origen, String destino, String nombreArticulo) {
        if (nombreArticulo == null || nombreArticulo.trim().isEmpty()) {
            throw new IllegalArgumentException("Debe indicar el articulo");
        }
        tienda tiendaOrigen = consultarTienda(origen);
        tienda tiendaDestino = consultarTienda(destino);
        if (tiendaOrigen == tiendaDestino) {
            throw new IllegalArgumentException("Origen y destino deben ser diferentes");
        }
        Articulo articulo = tiendaOrigen.buscarArticulo(nombreArticulo);
        if (articulo == null || articulo.getCantidad() <= 0) {
            throw new IllegalStateException("El articulo no esta disponible en el origen");
        }
        if (tiendaDestino.buscarArticulo(nombreArticulo) != null) {
            throw new IllegalStateException("El articulo ya existe en el destino");
        }
        Articulo copia = new Articulo(articulo.getNombre(), articulo.getCategoria(), articulo.getPrecio(), articulo.getCantidad());
        // Se retira del origen mediante la API de tienda, no mediante la copia defensiva.
        if (!tiendaOrigen.retirarArticulo(nombreArticulo)) {
            throw new IllegalStateException("No fue posible retirar el articulo del origen");
        }
        tiendaDestino.agregarArticulo(copia);
        if (tiendaDestino.buscarArticulo(nombreArticulo) == null) {
            tiendaOrigen.agregarArticulo(articulo);
            throw new IllegalStateException("No fue posible registrar el articulo en el destino");
        }
    }

    public ArrayList<String> getUbicaciones() {
        return new ArrayList<>(tiendas.keySet());
    }
}
