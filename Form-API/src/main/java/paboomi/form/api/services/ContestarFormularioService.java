package paboomi.form.api.services;

import java.sql.SQLException;
import java.util.Optional;

import paboomi.form.api.db.model.FormularioStored;

public class ContestarFormularioService {

  private final DescargarFormularioService descargarFormularioService;

  public ContestarFormularioService() {
    this(new DescargarFormularioService());
  }

  public ContestarFormularioService(DescargarFormularioService descargarFormularioService) {
    this.descargarFormularioService = descargarFormularioService;
  }

  public Optional<FormularioStored> obtenerFormularioParaContestar(String nombreFormulario)
      throws SQLException {
    if (nombreFormulario == null || nombreFormulario.isBlank()) {
      throw new IllegalArgumentException("El parametro 'nombreFormulario' es obligatorio.");
    }

    long idFormulario;
    try {
      idFormulario = Long.parseLong(nombreFormulario.trim());
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException(
          "Con el mapeo actual de DB, 'nombreFormulario' debe ser el ID numerico del formulario.");
    }

    return descargarFormularioService.obtenerPorId(idFormulario);
  }

}
