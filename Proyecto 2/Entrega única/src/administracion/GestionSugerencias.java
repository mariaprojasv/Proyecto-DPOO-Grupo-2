package administracion;

import java.util.ArrayList;
import cafeteria.Cafeteria;
import club.ClubDeportivoSeneca;
import cafeteria.snack;
import usuarios.Administrador;
import usuarios.Empleado;

public class GestionSugerencias {
    private final ArrayList<SugerenciaSnack> sugerencias;
    private final Cafeteria cafeteria;
    private final ClubDeportivoSeneca club;

    public GestionSugerencias(Cafeteria cafeteria, ClubDeportivoSeneca club) {
        if (cafeteria == null) {
            throw new IllegalArgumentException("La cafeteria es obligatoria");
        }
        if (club == null) {
            throw new IllegalArgumentException("El club es obligatorio");
        }
        this.cafeteria = cafeteria;
        this.club = club;
        sugerencias = new ArrayList<>();
    }

    public SugerenciaSnack sugerirSnack(Empleado autor, String nombre, double precio, int cantidad, String alergenos) {
        verificarAutorRegistrado(autor);
        SugerenciaSnack sugerencia = new SugerenciaSnack(autor, nombre, precio, cantidad, alergenos);
        if (cafeteria.buscarProducto(sugerencia.getNombre()) != null) {
            throw new IllegalStateException("El producto ya existe en la cafeteria");
        }
        for (SugerenciaSnack existente : sugerencias) {
            if ((existente.getEstado() == SugerenciaSnack.Estado.PENDIENTE || existente.getEstado() == SugerenciaSnack.Estado.COMPLETAR) && existente.getNombre().equalsIgnoreCase(sugerencia.getNombre())) {
                throw new IllegalStateException("Ya hay una sugerencia pendiente con ese nombre");
            }
        }
        sugerencias.add(sugerencia);
        return sugerencia;
    }

    public void completarAlergenos(Empleado autor, SugerenciaSnack sugerencia, String alergenos) {
        verificarAutorRegistrado(autor);
        if (sugerencia == null || !sugerencias.contains(sugerencia)) {
            throw new IllegalArgumentException("La sugerencia no pertenece a esta gestion");
        }
        if (sugerencia.getAutor() != autor) {
            throw new SecurityException("Solo el empleado autor puede completar su sugerencia");
        }
        sugerencia.completarAlergenos(alergenos);
    }

    public void aprobar(Administrador administrador, SugerenciaSnack sugerencia) {
        verificarAdministradorYSugerencia(administrador, sugerencia);
        if (sugerencia.getEstado() != SugerenciaSnack.Estado.PENDIENTE) {
            throw new IllegalStateException("La sugerencia requiere completar informacion o ya fue resuelta");
        }
        if (cafeteria.buscarProducto(sugerencia.getNombre()) != null) {
            throw new IllegalStateException("El producto ya existe en la cafeteria");
        }
        if (sugerencia.getAlergenos() == null || sugerencia.getAlergenos().trim().isEmpty()) {
            throw new IllegalStateException("Complete la informacion de alergenos antes de aprobar");
        }
        cafeteria.agregarProducto(new snack(sugerencia.getNombre(), sugerencia.getPrecio(), sugerencia.getCantidadInicial(), sugerencia.getAlergenos()));
        sugerencia.aprobar();
    }

    public void rechazar(Administrador administrador, SugerenciaSnack sugerencia, String motivo) {
        verificarAdministradorYSugerencia(administrador, sugerencia);
        sugerencia.rechazar(motivo);
    }

    private void verificarAutorRegistrado(Empleado autor) {
        if (autor == null) {
            throw new IllegalArgumentException("El empleado autor es obligatorio");
        }
        if (club.buscarUsuario(autor.getLogin()) != autor) {
            throw new SecurityException("El empleado debe estar registrado en el club");
        }
    }

    private void verificarAdministradorYSugerencia(Administrador administrador, SugerenciaSnack sugerencia) {
        if (administrador == null || club.buscarUsuario(administrador.getLogin()) != administrador) {
            throw new SecurityException("Solo un administrador puede resolver sugerencias");
        }
        if (sugerencia == null || !sugerencias.contains(sugerencia)) {
            throw new IllegalArgumentException("La sugerencia no pertenece a esta gestion");
        }
    }

    public SugerenciaSnack restaurarSugerencia(Empleado autor, String nombre, double precio,
            int cantidad, String alergenos, SugerenciaSnack.Estado estado, String motivo) {
        SugerenciaSnack sugerencia = new SugerenciaSnack(autor, nombre, precio, cantidad, alergenos);
        if (estado == null) {
            throw new IllegalArgumentException("Estado obligatorio");
        }
        for (SugerenciaSnack existente : sugerencias) {
            if (existente.getNombre().equalsIgnoreCase(sugerencia.getNombre())
                    && existente.getAutor().getLogin().equals(autor.getLogin())) {
                throw new IllegalStateException("Sugerencia repetida en el archivo");
            }
        }
        sugerencia.restaurarEstado(estado, motivo);
        sugerencias.add(sugerencia);
        return sugerencia;
    }

    public ArrayList<SugerenciaSnack> getSugerencias() {
        return new ArrayList<>(sugerencias);
    }
}
