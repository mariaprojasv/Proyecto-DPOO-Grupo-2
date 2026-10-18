package deportes;

import java.util.ArrayList;
import usuarios.Jugador;

public class ControlSanciones {
    private ArrayList<Jugador> jugadores;
    private ArrayList<Integer> amarillas;
    private ArrayList<Jugador> suspendidos;

    public ControlSanciones() {
        jugadores = new ArrayList<>();
        amarillas = new ArrayList<>();
        suspendidos = new ArrayList<>();
    }

    public boolean registrarTarjeta(Tarjeta tarjeta) {
        if (tarjeta == null || tarjeta.getJugador() == null || tarjeta.getTipo() == null) {
            return false;
        }
        Jugador jugador = tarjeta.getJugador();
        String tipo = tarjeta.getTipo();
        if (!tipo.equalsIgnoreCase("AMARILLA") && !tipo.equalsIgnoreCase("ROJA")) {
            return false;
        }
        if (tipo.equalsIgnoreCase("ROJA")) {
            if (!suspendidos.contains(jugador)) {
                suspendidos.add(jugador);
            }
            return true;
        }
        int posicion = jugadores.indexOf(jugador);
        if (posicion == -1) {
            jugadores.add(jugador);
            amarillas.add(1);
        }
        else {
            int cantidad = amarillas.get(posicion) + 1;
            amarillas.set(posicion, cantidad);
            if (cantidad >= 2) {
                if (!suspendidos.contains(jugador)) {
                    suspendidos.add(jugador);
                }
                amarillas.set(posicion, 0);
            }
        }
        return true;
    }

    public ArrayList<Jugador> getSuspendidos() {
        return new ArrayList<>(suspendidos);
    }

    public ArrayList<Jugador> getJugadoresConAmarillas() {
        return new ArrayList<>(jugadores);
    }

    public void recuperarEstado(Jugador jugador, int cantidadAmarillas, boolean suspendido) {
        if (jugador == null || cantidadAmarillas < 0 || cantidadAmarillas > 1) {
            return;
        }
        int posicion = jugadores.indexOf(jugador);
        if (posicion == -1) {
            jugadores.add(jugador);
            amarillas.add(cantidadAmarillas);
        }
        else {
            amarillas.set(posicion, cantidadAmarillas);
        }
        if (suspendido && !suspendidos.contains(jugador)) {
            suspendidos.add(jugador);
        }
    }

    public boolean estaSuspendido(Jugador jugador) {
        return suspendidos.contains(jugador);
    }

    public boolean puedeAlinearse(Jugador jugador) {
        return jugador != null && !estaSuspendido(jugador);
    }

    public void cumplirSuspension(Jugador jugador) {
        suspendidos.remove(jugador);
    }

    public int getAmarillas(Jugador jugador) {
        int posicion = jugadores.indexOf(jugador);
        if (posicion == -1) {
            return 0;
        }
        return amarillas.get(posicion);
    }
}
