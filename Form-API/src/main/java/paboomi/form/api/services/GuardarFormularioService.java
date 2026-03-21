package paboomi.form.api.services;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;

import paboomi.form.api.db.formularios.FormularioDbPort;
import paboomi.form.api.db.formularios.FormularioDbRepository;

public class GuardarFormularioService {

  private final FormularioDbPort formularioDbPort;
  private final PkmStorageService pkmStorageService;

  public GuardarFormularioService() {
    this.formularioDbPort = new FormularioDbRepository();
    this.pkmStorageService = new PkmStorageService();
  }

  public GuardarFormularioResult guardar(String autor, String nombreFormulario, byte[] formularioBytes)
      throws SQLException, IOException {
    validarCampos(autor, nombreFormulario, formularioBytes);

    String autorLimpio = autor.trim();
    String nombreFormularioLimpio = nombreFormulario.trim();

    long idFormulario = formularioDbPort.guardarFormulario(autorLimpio, nombreFormularioLimpio, formularioBytes);
    String nombreArchivoServidor = pkmStorageService.guardarArchivo(idFormulario, nombreFormularioLimpio, formularioBytes);

    return new GuardarFormularioResult(idFormulario, nombreArchivoServidor);
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

  public record GuardarFormularioResult(long idFormulario, String nombreArchivoServidor) {
  }

}
