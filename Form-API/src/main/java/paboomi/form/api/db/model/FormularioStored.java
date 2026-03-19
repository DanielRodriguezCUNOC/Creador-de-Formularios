package paboomi.form.api.db.model;

import java.time.LocalDateTime;

public class FormularioStored {

  private long id;
  private String autor;
  private LocalDateTime fechaCreacion;
  private byte[] contenido;

  public FormularioStored() {
  }

  public FormularioStored(long id, String autor, LocalDateTime fechaCreacion, byte[] contenido) {
    this.id = id;
    this.autor = autor;
    this.fechaCreacion = fechaCreacion;
    this.contenido = contenido;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getAutor() {
    return autor;
  }

  public void setAutor(String autor) {
    this.autor = autor;
  }

  public LocalDateTime getFechaCreacion() {
    return fechaCreacion;
  }

  public void setFechaCreacion(LocalDateTime fechaCreacion) {
    this.fechaCreacion = fechaCreacion;
  }

  public byte[] getContenido() {
    return contenido;
  }

  public void setContenido(byte[] contenido) {
    this.contenido = contenido;
  }

  public String buildFileName() {
    return "formulario_" + id + ".pkm.txt";
  }
}
