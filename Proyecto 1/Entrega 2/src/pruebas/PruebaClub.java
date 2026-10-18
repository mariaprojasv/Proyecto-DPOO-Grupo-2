package pruebas;

import club.ClubDeportivoSeneca;
import usuarios.Administrador;
import usuarios.Socio;
import usuarios.Usuario;

public class PruebaClub {
    private static int verificaciones = 0;

    public static void comprobar(boolean condicion, String mensaje) {
        if (!condicion) {
            throw new RuntimeException("Fallo: " + mensaje);
        }
        verificaciones++;
        System.out.println("Correcto: " + mensaje);
    }

    public static void main(String[] args) {
        ClubDeportivoSeneca club = new ClubDeportivoSeneca("Club Deportivo Seneca");
        Administrador administrador = new Administrador("admin", "1234", "Administrador");
        Socio socio = new Socio("socio1", "abcd", "Socio Uno", 0);

        comprobar(club.getNombre().equals("Club Deportivo Seneca"), "Nombre del club");
        comprobar(club.registrarUsuario(administrador), "Registrar administrador");
        comprobar(club.registrarUsuario(socio), "Registrar socio");
        comprobar(!club.registrarUsuario(new Socio("SOCIO1", "otra", "Duplicado", 0)), "No repetir login");
        comprobar(!club.registrarUsuario(null), "Rechazar usuario nulo");
        comprobar(club.buscarUsuario("socio1") == socio, "Buscar usuario");
        comprobar(club.iniciarSesion("admin", "1234") == administrador, "Inicio de sesion correcto");
        comprobar(club.iniciarSesion("admin", "incorrecta") == null, "Contrasena incorrecta");
        comprobar(club.iniciarSesion("desconocido", "1234") == null, "Usuario inexistente");
        comprobar(club.getAreaDeportiva() != null, "Area deportiva disponible");
        comprobar(club.getAcademia() != null, "Academia disponible");
        comprobar(club.getGestionReservas() != null, "Reservas disponibles");
        comprobar(club.getTienda() != null, "Tienda disponible");
        comprobar(club.getCafeteria() != null, "Cafeteria disponible");
        comprobar(club.getUsuarios().size() == 2, "Cantidad de usuarios");

        System.out.println("Verificaciones superadas: " + verificaciones);
    }
}
