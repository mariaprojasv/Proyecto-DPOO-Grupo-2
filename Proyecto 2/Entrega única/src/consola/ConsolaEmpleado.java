package consola;

import torneos.Torneo;
import administracion.SugerenciaSnack;
import usuarios.Empleado;
import usuarios.Usuario;

public final class ConsolaEmpleado {
    public static void main(String[] args) {
        ConsolaComun c = new ConsolaComun();
        try {
            Usuario u=c.autenticar();
            if (!(u instanceof Empleado)) { System.out.println("Acceso exclusivo para empleados."); return; }
            int opcion;
            do {
                System.out.println("\n1. Consultar historial de torneo\n2. Inscribirse en individual\n3. Retirarse\n4. Inscribir pareja\n5. Sugerir snack\n6. Mis sugerencias\n7. Completar sugerencia\n0. Salir");
                opcion=c.entero("Opcion",0,7);
                switch(opcion) {
                    case 1: c.historial(); break;
                    case 2: c.ejecutarSeguro(() -> { Torneo t=c.seleccionarTorneo(); if(t!=null) { c.club.getGestionTorneos().inscribirJugador(t,u); System.out.println("Inscripcion registrada."); } }); break;
                    case 3: c.ejecutarSeguro(() -> { Torneo t=c.seleccionarTorneo(); if(t!=null) { c.club.getGestionTorneos().retirarJugador(t,u); System.out.println("Retiro registrado."); } }); break;
                    case 4: c.ejecutarSeguro(() -> {
                        Torneo t=c.seleccionarTorneo(); if(t==null) return;
                        Usuario pareja=c.club.buscarUsuario(c.texto("Login del companero"));
                        if(pareja==null) { System.out.println("El companero no existe."); return; }
                        c.club.getGestionTorneos().inscribirPareja(t,u,pareja);
                        System.out.println("Pareja inscrita.");
                    }); break;
                    case 5: c.ejecutarSeguro(() -> {
                        String nombre=c.texto("Nombre del snack");
                        double precio=c.decimal("Precio",0.01,100000000);
                        int cantidad=c.entero("Cantidad inicial",0,1000000);
                        String alergenos=c.textoOpcional("Alergenos (Ninguno si no contiene)");
                        c.club.getGestionSugerencias().sugerirSnack((Empleado)u,nombre,precio,cantidad,alergenos);
                        System.out.println("Sugerencia registrada.");
                    }); break;
                    case 6: {
                        boolean encontrado=false;
                        for(SugerenciaSnack s:c.club.getGestionSugerencias().getSugerencias())
                            if(s.getAutor()==u) { System.out.println(s.getNombre()+" | "+s.getEstado()+" | Motivo: "+s.getMotivo()); encontrado=true; }
                        if(!encontrado) System.out.println("No hay sugerencias propias.");
                    } break;
                    case 7: c.ejecutarSeguro(() -> {
                        java.util.ArrayList<SugerenciaSnack> pendientes=new java.util.ArrayList<>();
                        for(SugerenciaSnack s:c.club.getGestionSugerencias().getSugerencias()) {
                            if(s.getAutor()==u && s.getEstado()==SugerenciaSnack.Estado.COMPLETAR) {
                                pendientes.add(s);
                                System.out.println(pendientes.size()+". "+s.getNombre()+" | "+s.getEstado());
                            }
                        }
                        if(pendientes.isEmpty()) { System.out.println("No hay sugerencias por completar."); return; }
                        SugerenciaSnack seleccion=pendientes.get(c.entero("Sugerencia",1,pendientes.size())-1);
                        c.club.getGestionSugerencias().completarAlergenos((Empleado)u,seleccion,c.texto("Alergenos (Ninguno si no contiene)"));
                        System.out.println("Sugerencia completada y pendiente de aprobacion.");
                    }); break;
                    default: break;
                }
            } while(opcion!=0);
        } finally { c.guardar(); }
    }
}
