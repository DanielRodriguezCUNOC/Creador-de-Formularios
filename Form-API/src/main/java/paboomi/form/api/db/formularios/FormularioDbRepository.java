package paboomi.form.api.db.formularios;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import paboomi.form.api.db.connection.DBConnectionSingleton;
import paboomi.form.api.db.dto.FormularioMetadataResponse;
import paboomi.form.api.db.dto.PaginatedFormulariosResponse;
import paboomi.form.api.db.model.FormularioStored;

public class FormularioDbRepository implements FormularioDbPort {

  private static final String INSERT_FORMULARIO_SQL = """
      INSERT INTO formularios (autor, formulario)
      VALUES (?, ?)
      """;

  private static final String COUNT_FORMULARIOS_SQL = "SELECT COUNT(*) AS total FROM formularios";

  private static final String PAGE_FORMULARIOS_SQL = """
      SELECT id, autor, TIMESTAMP(fecha_creacion, hora_creacion) AS fecha_hora, OCTET_LENGTH(formulario) AS tamanio
      FROM formularios
      ORDER BY id DESC
      LIMIT ? OFFSET ?
      """;

  private static final String FIND_BY_ID_SQL = """
      SELECT id, autor, TIMESTAMP(fecha_creacion, hora_creacion) AS fecha_hora, formulario
      FROM formularios
      WHERE id = ?
      """;

  @Override
  public long guardarFormulario(String autor, byte[] formularioBytes) throws SQLException {
    try (Connection connection = DBConnectionSingleton.getInstance().getConnection()) {
      if (connection == null) {
        throw new SQLException("No se pudo obtener conexion con la base de datos.");
      }

      try (PreparedStatement statement = connection.prepareStatement(
          INSERT_FORMULARIO_SQL,
          Statement.RETURN_GENERATED_KEYS)) {
        statement.setString(1, autor);
        statement.setBytes(2, formularioBytes);
        statement.executeUpdate();

        try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
          if (generatedKeys.next()) {
            return generatedKeys.getLong(1);
          }
          throw new SQLException("No se genero ID para el formulario guardado.");
        }
      }
    }
  }

  @Override
  public PaginatedFormulariosResponse obtenerPaginaFormularios(int page, int size) throws SQLException {
    int safePage = Math.max(page, 1);
    int safeSize = Math.min(Math.max(size, 1), 100);
    int offset = (safePage - 1) * safeSize;

    try (Connection connection = DBConnectionSingleton.getInstance().getConnection()) {
      if (connection == null) {
        throw new SQLException("No se pudo obtener conexion con la base de datos.");
      }

      long totalElements = contarFormularios(connection);
      int totalPages = totalElements == 0 ? 0 : (int) Math.ceil((double) totalElements / safeSize);
      List<FormularioMetadataResponse> formularios = obtenerFormulariosDePagina(connection, safeSize, offset);

      return new PaginatedFormulariosResponse(safePage, safeSize, totalElements, totalPages, formularios);
    }
  }

  @Override
  public Optional<FormularioStored> obtenerFormularioPorId(long idFormulario) throws SQLException {
    try (Connection connection = DBConnectionSingleton.getInstance().getConnection()) {
      if (connection == null) {
        throw new SQLException("No se pudo obtener conexion con la base de datos.");
      }

      try (PreparedStatement statement = connection.prepareStatement(FIND_BY_ID_SQL)) {
        statement.setLong(1, idFormulario);

        try (ResultSet rs = statement.executeQuery()) {
          if (!rs.next()) {
            return Optional.empty();
          }

          return Optional.of(new FormularioStored(
              rs.getLong("id"),
              rs.getString("autor"),
              mapFechaHora(rs.getTimestamp("fecha_hora")),
              rs.getBytes("formulario")));
        }
      }
    }
  }

  private long contarFormularios(Connection connection) throws SQLException {
    try (PreparedStatement statement = connection.prepareStatement(COUNT_FORMULARIOS_SQL);
        ResultSet rs = statement.executeQuery()) {
      if (rs.next()) {
        return rs.getLong("total");
      }
      return 0;
    }
  }

  private List<FormularioMetadataResponse> obtenerFormulariosDePagina(Connection connection, int size, int offset)
      throws SQLException {
    List<FormularioMetadataResponse> result = new ArrayList<>();

    try (PreparedStatement statement = connection.prepareStatement(PAGE_FORMULARIOS_SQL)) {
      statement.setInt(1, size);
      statement.setInt(2, offset);

      try (ResultSet rs = statement.executeQuery()) {
        while (rs.next()) {
          result.add(new FormularioMetadataResponse(
              rs.getLong("id"),
              rs.getString("autor"),
              mapFechaHora(rs.getTimestamp("fecha_hora")),
              rs.getInt("tamanio")));
        }
      }
    }
    return result;
  }

  private LocalDateTime mapFechaHora(Timestamp timestamp) {
    if (timestamp == null) {
      return null;
    }
    return timestamp.toLocalDateTime();
  }
}
