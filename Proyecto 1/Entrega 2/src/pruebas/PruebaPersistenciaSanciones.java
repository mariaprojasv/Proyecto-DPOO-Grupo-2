package pruebas;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import club.ClubDeportivoSeneca;
import deportes.Tarjeta;
import persistencia.PersistenciaArchivos;
import usuarios.Jugador;

public class PruebaPersistenciaSanciones {
    public static void main(String[] args) throws Exception {
        Path carpeta = Files.createTempDirectory("seneca16");
        ClubDeportivoSeneca original = new ClubDeportivoSeneca("Seneca");
        Jugador ana = new Jugador("ana16", "123", "Ana", 0, LocalDate.of(2000, 1, 1));
        Jugador luis = new Jugador("luis16", "123", "Luis", 0, LocalDate.of(2000, 1, 1));
        original.registrarUsuario(ana);
        original.registrarUsuario(luis);
        original.getControlSanciones().registrarTarjeta(new Tarjeta(ana, "AMARILLA"));
        original.getControlSanciones().registrarTarjeta(new Tarjeta(luis, "ROJA"));
        PersistenciaArchivos archivos = new PersistenciaArchivos(carpeta.toString());
        archivos.guardar(original);
        ClubDeportivoSeneca recuperado = new ClubDeportivoSeneca("Seneca");
        archivos.cargar(recuperado);
        Jugador anaNueva = (Jugador) recuperado.buscarUsuario("ana16");
        Jugador luisNuevo = (Jugador) recuperado.buscarUsuario("luis16");
        if (recuperado.getControlSanciones().getAmarillas(anaNueva) != 1) {
            throw new RuntimeException("No recupero amarillas");
        }
        if (!recuperado.getControlSanciones().estaSuspendido(luisNuevo)) {
            throw new RuntimeException("No recupero suspension");
        }
        recuperado.getControlSanciones().cumplirSuspension(luisNuevo);
        archivos.guardar(recuperado);
        ClubDeportivoSeneca tercero = new ClubDeportivoSeneca("Seneca");
        archivos.cargar(tercero);
        if (tercero.getControlSanciones().estaSuspendido((Jugador) tercero.buscarUsuario("luis16"))) {
            throw new RuntimeException("La suspension cumplida reaparecio");
        }
        System.out.println("PruebaPersistenciaSanciones: 3/3");
    }
}
