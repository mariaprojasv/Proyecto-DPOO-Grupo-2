package consola;

import java.util.List;
import java.util.ArrayList;
import cafeteria.Producto;
import cafeteria.VentaCafeteria;
import torneos.BonoDescuento;
import reservas.Instalacion;
import reservas.Reserva;
import deportes.Equipo;
import torneos.Torneo;
import usuarios.Administrador;
import usuarios.Empleado;
import usuarios.Usuario;

public final class ConsolaSocio {
    public static void main(String[] args) {
        ConsolaComun c = new ConsolaComun();
        try {
            Usuario u = c.autenticar();
            if (u == null || u instanceof Administrador || u instanceof Empleado) { System.out.println("Acceso no autorizado para esta consola."); return; }
            int opcion;
            do {
                System.out.println("\n1. Consultar torneos\n2. Inscribirse en torneo individual\n3. Retirarse de torneo\n4. Reservar instalacion\n5. Mis reservas\n6. Mis bonos\n7. Inscribir pareja\n8. Inscribir equipo\n9. Unirse a un equipo\n10. Mis equipos\n11. Verificar acceso a instalacion\n12. Comprar en cafeteria\n0. Salir");
                opcion=c.entero("Opcion",0,12);
                switch(opcion) {
                    case 1: c.historial(); break;
                    case 2: c.ejecutarSeguro(() -> { Torneo t=c.seleccionarTorneo(); if(t!=null) { c.club.getGestionTorneos().inscribirJugador(t,u); System.out.println("Inscripcion realizada."); } }); break;
                    case 3: c.ejecutarSeguro(() -> { Torneo t=c.seleccionarTorneo(); if(t!=null) { c.club.getGestionTorneos().retirarJugador(t,u); System.out.println("Retiro realizado."); } }); break;
                    case 4: c.ejecutarSeguro(() -> {
                        List<Instalacion> instalaciones=c.club.getGestionReservas().getInstalaciones();
                        if(instalaciones.isEmpty()) { System.out.println("No hay instalaciones."); return; }
                        for(int i=0;i<instalaciones.size();i++) System.out.println((i+1)+". "+instalaciones.get(i).getNombre());
                        Instalacion ins=instalaciones.get(c.entero("Instalacion",1,instalaciones.size())-1);
                        c.club.getGestionReservas().realizarReservaValidada(u,ins,c.fecha("Fecha"),c.hora("Inicio"),c.hora("Fin"),c.entero("Jugadores",1,100),c.texto("Modalidad"));
                        System.out.println("Reserva confirmada.");
                    }); break;
                    case 5: c.club.getGestionReservas().consultarReservas(u).forEach(r -> System.out.println(r.getInstalacion().getNombre()+" "+r.getFecha()+" "+r.getInicio()+"-"+r.getFin())); break;
                    case 6: u.getBonosDisponibles().forEach(b -> System.out.println(b)); break;
                    case 7: c.ejecutarSeguro(() -> {
                        Torneo t=c.seleccionarTorneo(); if(t==null) return;
                        Usuario pareja=c.club.buscarUsuario(c.texto("Login del companero"));
                        if(pareja==null) { System.out.println("El companero no existe."); return; }
                        c.club.getGestionTorneos().inscribirPareja(t,u,pareja);
                        System.out.println("Pareja inscrita.");
                    }); break;
                    case 8: c.ejecutarSeguro(() -> {
                        Torneo t=c.seleccionarTorneo(); if(t==null) return;
                        List<Equipo> equipos=c.club.getAreaDeportiva().getEquipos();
                        if(equipos.isEmpty()) { System.out.println("No hay equipos."); return; }
                        for(int i=0;i<equipos.size();i++) System.out.println((i+1)+". "+equipos.get(i).getNombre());
                        Equipo equipo=equipos.get(c.entero("Equipo",1,equipos.size())-1);
                        c.club.getGestionTorneos().inscribirEquipo(t,equipo,u);
                        System.out.println("Equipo inscrito.");
                    }); break;
                    case 9: c.ejecutarSeguro(() -> {
                        List<Equipo> equipos=c.club.getAreaDeportiva().getEquipos();
                        if(equipos.isEmpty()) { System.out.println("No hay equipos. Solicite al administrador que cree uno."); return; }
                        for(int i=0;i<equipos.size();i++) {
                            Equipo e=equipos.get(i);
                            System.out.println((i+1)+". "+e.getNombre()+" | "+e.getDeporte().getNombre()+" | "+e.getCategoria().getNombre()+" | "+e.getIntegrantes().size()+"/"+e.getCupoMaximo());
                        }
                        Equipo e=equipos.get(c.entero("Equipo",1,equipos.size())-1);
                        c.club.getAreaDeportiva().inscribirIntegranteExterno(u,e);
                        System.out.println("Integrante agregado al equipo.");
                    }); break;
                    case 10: {
                        boolean encontrado=false;
                        for(Equipo e:c.club.getAreaDeportiva().getEquipos()) if(e.getIntegrantes().contains(u)) {
                            System.out.println(e.getNombre()+" ("+e.getIntegrantes().size()+"/"+e.getCupoMaximo()+")"); encontrado=true;
                        }
                        if(!encontrado) System.out.println("No perteneces a ningun equipo.");
                    } break;
                    case 11: c.ejecutarSeguro(() -> {
                        List<Reserva> reservas=c.club.getGestionReservas().consultarReservas(u);
                        if(reservas.isEmpty()) { System.out.println("No tienes reservas."); return; }
                        for(int i=0;i<reservas.size();i++) {
                            Reserva r=reservas.get(i);
                            System.out.println((i+1)+". "+r.getInstalacion().getNombre()+" "+r.getFecha()+" "+r.getInicio());
                        }
                        Reserva r=reservas.get(c.entero("Reserva",1,reservas.size())-1);
                        boolean caliente=c.entero("Lleva bebida caliente: 1. Si  2. No",1,2)==1;
                        System.out.println(c.club.getGestionReservas().permitirUso(r,caliente)?"Acceso permitido.":"Acceso denegado por normas de la instalacion.");
                    }); break;
                    case 12: c.ejecutarSeguro(() -> {
                        List<Producto> disponibles = new ArrayList<>();
                        for (Producto p : c.club.getCafeteria().getProductos()) {
                            if (p.getCantidad() > 0) disponibles.add(p);
                        }
                        if (disponibles.isEmpty()) {
                            System.out.println("No hay productos disponibles en cafeteria.");
                            return;
                        }
                        for (int i = 0; i < disponibles.size(); i++) {
                            Producto p = disponibles.get(i);
                            System.out.println((i + 1) + ". " + p.getNombre() + " | $" + p.getPrecio()
                                    + " | Disponibles: " + p.getCantidad());
                        }
                        Producto elegido = disponibles.get(c.entero("Producto", 1, disponibles.size()) - 1);
                        int cantidad = c.entero("Cantidad", 1, elegido.getCantidad());
                        double subtotal = elegido.getPrecio() * cantidad;
                        System.out.println("Subtotal: $" + subtotal + " | Impuesto sin descuento (8%): $" + (subtotal * 0.08));
                        List<BonoDescuento> bonos = new ArrayList<>();
                        for (BonoDescuento b : u.getBonosDisponibles()) if (b.estaDisponible()) bonos.add(b);
                        BonoDescuento bono = null;
                        if (!bonos.isEmpty() && c.entero("Usar bono de torneo? 1. Si  2. No", 1, 2) == 1) {
                            for (int i = 0; i < bonos.size(); i++) {
                                System.out.println((i + 1) + ". " + bonos.get(i).getPorcentaje() + "% | " + bonos.get(i).getOrigen());
                            }
                            bono = bonos.get(c.entero("Bono", 1, bonos.size()) - 1);
                        }
                        double descuento = bono == null ? 0 : subtotal * bono.getPorcentaje() / 100.0;
                        double total = (subtotal - descuento) * 1.08;
                        System.out.println("Descuento: $" + descuento + " | Total: $" + total);
                        if (c.entero("Confirmar compra? 1. Si  2. No", 1, 2) != 1) {
                            System.out.println("Compra cancelada.");
                            return;
                        }
                        VentaCafeteria venta = bono == null
                                ? c.club.getCafeteria().registrarVentaValidada(u, elegido.getNombre(), cantidad, 0)
                                : c.club.getCafeteria().registrarVentaConBono(u, elegido.getNombre(), cantidad, 0, bono);
                        System.out.println("Compra registrada.");
                        System.out.println("Producto: " + elegido.getNombre() + " | Cantidad: " + venta.getCantidad());
                        System.out.println("Subtotal: $" + venta.getSubtotal());
                        System.out.println("Descuento: $" + (venta.getSubtotal() - (venta.getTotal() - venta.getImpuesto() - venta.getPropina())));
                        System.out.println("Impuesto (8%): $" + venta.getImpuesto());
                        System.out.println("Total pagado: $" + venta.getTotal());
                        System.out.println("Unidades restantes en inventario: " + elegido.getCantidad());
                    }); break;
                    default: break;
                }
            } while(opcion!=0);
        } finally { c.guardar(); }
    }
}
