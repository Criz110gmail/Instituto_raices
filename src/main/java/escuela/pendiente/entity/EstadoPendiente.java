package escuela.pendiente.entity;

public enum EstadoPendiente {
 PENDIENTE("Pendiente"), EN_PROCESO("En proceso"), REALIZADO("Realizado"), CANCELADO("Cancelado");
 private final String etiqueta;
 EstadoPendiente(String etiqueta){this.etiqueta=etiqueta;}
 public String getEtiqueta(){return etiqueta;}
 public boolean abierto(){return this==PENDIENTE||this==EN_PROCESO;}
}
