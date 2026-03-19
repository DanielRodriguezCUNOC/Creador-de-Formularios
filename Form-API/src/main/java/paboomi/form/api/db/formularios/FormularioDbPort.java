package paboomi.form.api.db.formularios;

import java.sql.SQLException;
import java.util.Optional;

import paboomi.form.api.db.dto.PaginatedFormulariosResponse;
import paboomi.form.api.db.model.FormularioStored;

public interface FormularioDbPort {

  long guardarFormulario(String autor, byte[] formularioBytes) throws SQLException;

  PaginatedFormulariosResponse obtenerPaginaFormularios(int page, int size) throws SQLException;

  Optional<FormularioStored> obtenerFormularioPorId(long idFormulario) throws SQLException;
}
