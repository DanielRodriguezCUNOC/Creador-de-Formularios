package paboomi.form.api.services;

import java.sql.SQLException;
import java.util.Objects;

import paboomi.form.api.db.formularios.FormularioDbPort;
import paboomi.form.api.db.formularios.FormularioDbRepository;

public class GuardarFormularioService {

  private final FormularioDbPort formularioDbPort;

  public GuardarFormularioService() {
    this.formularioDbPort = new FormularioDbRepository();
  }

  public long guardar(String autor, String nombreFormulario, byte[] formularioBytes)
      throws SQLException {
    validarCampos(autor, nombreFormulario, formularioBytes);
    return formularioDbPort.guardarFormulario(autor.trim(), nombreFormulario.trim(), formularioBytes);
  }

  private void validarCampos(String autor, String nombreFormulario, byte[] formularioBytes) {
    if (autor == null || autor.isBlank()) {
      throw new IllegalArgumentException("El campo 'autor' es obligatorio.");
    }
    if (nombreFormulario == null || nombreFormulario.isBlank()) {
      throw new IllegalArgumentException("El campo 'nombreFormulario' es obligatorio.");
    }
    if (Objects.isNull(formularioBytes) || formularioBytes.length == 0) {
      throw new IllegalArgumentException("El archivo .pkm es obligatorio.");
    }
  }

}
