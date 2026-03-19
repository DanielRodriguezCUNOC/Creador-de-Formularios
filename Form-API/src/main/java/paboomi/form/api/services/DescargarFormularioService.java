package paboomi.form.api.services;

import java.sql.SQLException;
import java.util.Optional;

import paboomi.form.api.db.dto.PaginatedFormulariosResponse;
import paboomi.form.api.db.formularios.FormularioDbPort;
import paboomi.form.api.db.formularios.FormularioDbRepository;
import paboomi.form.api.db.model.FormularioStored;

public class DescargarFormularioService {

  private final FormularioDbPort formularioDbPort;

  public DescargarFormularioService() {
    this.formularioDbPort = new FormularioDbRepository();
  }

  public PaginatedFormulariosResponse obtenerPagina(int page, int size) throws SQLException {
    return formularioDbPort.obtenerPaginaFormularios(page, size);
  }

  public Optional<FormularioStored> obtenerPorId(long idFormulario) throws SQLException {
    if (idFormulario <= 0) {
      throw new IllegalArgumentException("El id del formulario debe ser mayor a 0.");
    }

    return formularioDbPort.obtenerFormularioPorId(idFormulario);
  }

}
