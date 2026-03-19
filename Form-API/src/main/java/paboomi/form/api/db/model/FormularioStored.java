package paboomi.form.api.db.model;

import java.time.LocalDateTime;

public class FormularioStored {

  private long id;
  private String nombreFormulario;
  private String autor;
  private LocalDateTime fechaCreacion;
  private byte[] contenido;

  public FormularioStored() {
  }

  public FormularioStored(long id, String nombreFormulario, String autor, LocalDateTime fechaCreacion,
      byte[] contenido) {
    this.id = id;
    this.nombreFormulario = nombreFormulario;
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

  public String getNombreFormulario() {
    return nombreFormulario;
  }

  public void setNombreFormulario(String nombreFormulario) {
    this.nombreFormulario = nombreFormulario;
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
    if (nombreFormulario == null || nombreFormulario.isBlank()) {
      return "formulario_" + id + ".pkm.txt";
    }
    return nombreFormulario + ".pkm.txt";
  }
}
